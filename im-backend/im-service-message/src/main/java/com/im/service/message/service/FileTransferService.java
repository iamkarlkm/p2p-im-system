package com.im.service.message.service;

import com.im.dto.FileMessageRequest;
import com.im.dto.FileMessageResponse;
import com.im.dto.FileUploadRequest;
import com.im.entity.FileMessage;
import com.im.entity.Message;
import com.im.repository.FileMessageRepository;
import com.im.repository.MessageRepository;
import com.im.storage.FileStorageService;
import com.im.websocket.WebSocketMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 文件传输服务
 * 处理文件发送、接收、断点续传等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileTransferService {

    private final MessageRepository messageRepository;
    private final FileMessageRepository fileMessageRepository;
    private final FileStorageService storageService;
    private final WebSocketMessageHandler webSocketHandler;

    // 内存中存储分片上传信息
    private final Map<String, UploadContext> uploadContexts = new ConcurrentHashMap<>();

    /**
     * 初始化文件上传
     */
    public FileMessageResponse initFileUpload(Long senderId, FileUploadRequest request) {
        // 验证文件大小（最大100MB）
        if (request.getFileSize() > 100 * 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过100MB");
        }

        // 生成上传ID
        String uploadId = UUID.randomUUID().toString();

        // 创建上传上下文
        UploadContext context = new UploadContext();
        context.setUploadId(uploadId);
        context.setSenderId(senderId);
        context.setFileName(request.getFileName());
        context.setFileSize(request.getFileSize());
        context.setFileHash(request.getFileHash());
        context.setChunkSize(2 * 1024 * 1024); // 2MB分片
        context.setTotalChunks((int) Math.ceil((double) request.getFileSize() / context.getChunkSize()));
        context.setUploadTime(LocalDateTime.now());
        context.setUploadedChunks(new HashSet<>());

        uploadContexts.put(uploadId, context);

        log.info("初始化文件上传: uploadId={}, fileName={}, totalChunks={}", 
                uploadId, request.getFileName(), context.getTotalChunks());

        FileMessageResponse response = new FileMessageResponse();
        response.setUploadId(uploadId);
        response.setChunkSize(context.getChunkSize());
        response.setTotalChunks(context.getTotalChunks());
        response.setStatus("INIT");
        return response;
    }

    /**
     * 上传分片
     */
    public FileMessageResponse uploadChunk(Long userId, String uploadId, Integer chunkIndex, MultipartFile chunk) {
        UploadContext context = uploadContexts.get(uploadId);
        if (context == null) {
            throw new RuntimeException("上传任务不存在或已过期");
        }

        // 验证用户
        if (!context.getSenderId().equals(userId)) {
            throw new RuntimeException("无权操作此上传任务");
        }

        try {
            // 保存分片到临时目录
            String tempDir = System.getProperty("java.io.tmpdir") + "/im_uploads/" + uploadId;
            Path chunkPath = Paths.get(tempDir, "chunk_" + chunkIndex);
            Files.createDirectories(chunkPath.getParent());
            Files.write(chunkPath, chunk.getBytes());

            // 标记分片已上传
            context.getUploadedChunks().add(chunkIndex);

            log.info("分片上传成功: uploadId={}, chunkIndex={}/{}", 
                    uploadId, chunkIndex, context.getTotalChunks());

            FileMessageResponse response = new FileMessageResponse();
            response.setUploadId(uploadId);
            response.setChunkIndex(chunkIndex);
            response.setUploadedChunks(context.getUploadedChunks().size());
            response.setTotalChunks(context.getTotalChunks());
            response.setProgress((int) ((context.getUploadedChunks().size() * 100.0) / context.getTotalChunks()));
            response.setStatus("UPLOADING");
            return response;

        } catch (IOException e) {
            log.error("分片上传失败", e);
            throw new RuntimeException("分片上传失败: " + e.getMessage());
        }
    }

    /**
     * 完成上传（合并分片）
     */
    @Transactional
    public FileMessageResponse completeUpload(Long userId, String uploadId) {
        UploadContext context = uploadContexts.get(uploadId);
        if (context == null) {
            throw new RuntimeException("上传任务不存在或已过期");
        }

        // 检查是否所有分片都已上传
        if (context.getUploadedChunks().size() < context.getTotalChunks()) {
            throw new RuntimeException("还有未上传的分片");
        }

        try {
            // 合并分片
            String tempDir = System.getProperty("java.io.tmpdir") + "/im_uploads/" + uploadId;
            Path mergedFile = Paths.get(tempDir, "merged_" + context.getFileName());

            try (FileOutputStream fos = new FileOutputStream(mergedFile.toFile())) {
                for (int i = 0; i < context.getTotalChunks(); i++) {
                    Path chunkPath = Paths.get(tempDir, "chunk_" + i);
                    Files.copy(chunkPath, fos);
                }
            }

            // 验证文件哈希
            String actualHash = calculateFileHash(mergedFile.toFile());
            if (!actualHash.equalsIgnoreCase(context.getFileHash())) {
                throw new RuntimeException("文件校验失败，请重新上传");
            }

            // 上传到存储服务
            String storagePath = String.format("files/%d/%s/%s", 
                    userId,
                    LocalDateTime.now().toString().substring(0, 7),
                    UUID.randomUUID().toString() + "_" + context.getFileName());

            String fileUrl = storageService.uploadFile(storagePath, Files.readAllBytes(mergedFile), 
                    getContentType(context.getFileName()));

            // 清理临时文件
            cleanupTempFiles(tempDir);

            // 移除上传上下文
            uploadContexts.remove(uploadId);

            log.info("文件上传完成: uploadId={}, fileUrl={}", uploadId, fileUrl);

            FileMessageResponse response = new FileMessageResponse();
            response.setUploadId(uploadId);
            response.setFileUrl(fileUrl);
            response.setFileName(context.getFileName());
            response.setFileSize(context.getFileSize());
            response.setFileHash(actualHash);
            response.setStatus("COMPLETED");
            return response;

        } catch (Exception e) {
            log.error("合并文件失败", e);
            throw new RuntimeException("合并文件失败: " + e.getMessage());
        }
    }

    /**
     * 发送文件消息
     */
    @Transactional
    public FileMessageResponse sendFileMessage(Long senderId, FileMessageRequest request) {
        // 保存消息主表
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setContent("[文件] " + request.getFileName());
        message.setMessageType("FILE");
        message.setSendTime(LocalDateTime.now());
        message.setStatus(0);
        Message savedMessage = messageRepository.save(message);

        // 保存文件消息详情
        FileMessage fileMessage = new FileMessage();
        fileMessage.setMessageId(savedMessage.getId());
        fileMessage.setFileUrl(request.getFileUrl());
        fileMessage.setFileName(request.getFileName());
        fileMessage.setFileSize(request.getFileSize());
        fileMessage.setFileHash(request.getFileHash());
        fileMessage.setFileType(getFileType(request.getFileName()));
        fileMessage.setUploadTime(LocalDateTime.now());
        FileMessage savedFile = fileMessageRepository.save(fileMessage);

        // WebSocket推送
        webSocketHandler.sendPrivateMessage(request.getReceiverId(), 
                convertToDTO(savedMessage, savedFile));

        log.info("文件消息发送成功: messageId={}, sender={}", savedMessage.getId(), senderId);
        return convertToResponse(savedMessage, savedFile);
    }

    /**
     * 下载文件（支持断点续传）
     */
    public void downloadFile(Long userId, Long messageId, HttpServletRequest request, 
                             HttpServletResponse response) throws IOException {
        FileMessage fileMessage = fileMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("文件消息不存在"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证权限
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权下载此文件");
        }

        // 获取文件数据
        byte[] fileData = storageService.downloadFile(fileMessage.getFileUrl());

        // 处理断点续传
        String rangeHeader = request.getHeader("Range");
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            // 解析Range头
            String[] ranges = rangeHeader.substring(6).split("-");
            long start = Long.parseLong(ranges[0]);
            long end = ranges.length > 1 && !ranges[1].isEmpty() ? 
                    Long.parseLong(ranges[1]) : fileData.length - 1;

            long contentLength = end - start + 1;
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileData.length);
            response.setContentLengthLong(contentLength);

            // 写入响应
            try (OutputStream os = response.getOutputStream()) {
                os.write(fileData, (int) start, (int) contentLength);
            }
        } else {
            // 完整下载
            response.setContentLength(fileData.length);
            try (OutputStream os = response.getOutputStream()) {
                os.write(fileData);
            }
        }

        response.setContentType(getContentType(fileMessage.getFileName()));
        response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + fileMessage.getFileName() + "\"");
    }

    /**
     * 获取文件下载链接
     */
    public String getDownloadUrl(Long userId, Long messageId) {
        FileMessage fileMessage = fileMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("文件消息不存在"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证权限
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权下载此文件");
        }

        // 生成临时下载链接（有效期30分钟）
        return storageService.generatePresignedUrl(fileMessage.getFileUrl(), 1800);
    }

    /**
     * 获取文件信息
     */
    public FileMessageResponse getFileInfo(Long userId, Long messageId) {
        FileMessage fileMessage = fileMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("文件消息不存在"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证权限
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权查看此文件");
        }

        return convertToResponse(message, fileMessage);
    }

    /**
     * 取消上传
     */
    public void cancelUpload(Long userId, String uploadId) {
        UploadContext context = uploadContexts.get(uploadId);
        if (context != null) {
            if (!context.getSenderId().equals(userId)) {
                throw new RuntimeException("无权取消此上传任务");
            }

            // 清理临时文件
            String tempDir = System.getProperty("java.io.tmpdir") + "/im_uploads/" + uploadId;
            cleanupTempFiles(tempDir);

            uploadContexts.remove(uploadId);
            log.info("上传任务已取消: uploadId={}", uploadId);
        }
    }

    /**
     * 删除文件消息
     */
    @Transactional
    public void deleteFileMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("只能删除自己发送的文件");
        }

        FileMessage fileMessage = fileMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("文件消息不存在"));

        // 删除存储的文件
        storageService.deleteFile(fileMessage.getFileUrl());

        // 删除数据库记录
        fileMessageRepository.delete(fileMessage);
        messageRepository.delete(message);

        log.info("文件消息已删除: messageId={}", messageId);
    }

    /**
     * 获取聊天中的文件列表
     */
    public List<FileMessageResponse> getFileList(Long userId, Long targetUserId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sendTime"));

        List<Message> messages = messageRepository.findPrivateChatHistory(userId, targetUserId, pageRequest);

        return messages.stream()
                .filter(msg -> "FILE".equals(msg.getMessageType()))
                .map(msg -> {
                    FileMessage file = fileMessageRepository.findByMessageId(msg.getId())
                            .orElse(null);
                    return file != null ? convertToResponse(msg, file) : null;
                })
                .filter(resp -> resp != null)
                .collect(Collectors.toList());
    }

    /**
     * 检查文件是否已存在（秒传）
     */
    public FileMessageResponse checkFileExists(Long userId, String fileHash, Long fileSize) {
        // 根据哈希和大小查找已存在的文件
        Optional<FileMessage> existingFile = fileMessageRepository.findByFileHashAndFileSize(fileHash, fileSize);

        FileMessageResponse response = new FileMessageResponse();
        if (existingFile.isPresent()) {
            response.setExists(true);
            response.setFileUrl(existingFile.get().getFileUrl());
            response.setFileName(existingFile.get().getFileName());
            response.setFileSize(existingFile.get().getFileSize());
            response.setStatus("EXISTED");
        } else {
            response.setExists(false);
            response.setStatus("NOT_FOUND");
        }
        return response;
    }

    // ============ 私有方法 ============

    private String calculateFileHash(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void cleanupTempFiles(String tempDir) {
        try {
            Path path = Paths.get(tempDir);
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                log.warn("删除临时文件失败: {}", p, e);
                            }
                        });
            }
        } catch (IOException e) {
            log.error("清理临时文件失败", e);
        }
    }

    private String getContentType(String fileName) {
        String ext = getFileExtension(fileName).toLowerCase();
        switch (ext) {
            case "pdf": return "application/pdf";
            case "doc": return "application/msword";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls": return "application/vnd.ms-excel";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt": return "application/vnd.ms-powerpoint";
            case "pptx": return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt": return "text/plain";
            case "jpg": case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "mp4": return "video/mp4";
            case "mp3": return "audio/mpeg";
            case "zip": return "application/zip";
            default: return "application/octet-stream";
        }
    }

    private String getFileType(String fileName) {
        String ext = getFileExtension(fileName).toLowerCase();
        if (Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp").contains(ext)) {
            return "IMAGE";
        } else if (Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv").contains(ext)) {
            return "VIDEO";
        } else if (Arrays.asList("mp3", "wav", "wma", "aac", "flac").contains(ext)) {
            return "AUDIO";
        } else if (Arrays.asList("pdf").contains(ext)) {
            return "PDF";
        } else if (Arrays.asList("doc", "docx").contains(ext)) {
            return "WORD";
        } else if (Arrays.asList("xls", "xlsx").contains(ext)) {
            return "EXCEL";
        } else if (Arrays.asList("ppt", "pptx").contains(ext)) {
            return "PPT";
        } else {
            return "OTHER";
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private Object convertToDTO(Message message, FileMessage fileMessage) {
        return convertToResponse(message, fileMessage);
    }

    private FileMessageResponse convertToResponse(Message message, FileMessage fileMessage) {
        FileMessageResponse response = new FileMessageResponse();
        response.setMessageId(message.getId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setFileUrl(fileMessage.getFileUrl());
        response.setFileName(fileMessage.getFileName());
        response.setFileSize(fileMessage.getFileSize());
        response.setFileHash(fileMessage.getFileHash());
        response.setFileType(fileMessage.getFileType());
        response.setSendTime(message.getSendTime());
        return response;
    }

    /**
     * 上传上下文
     */
    private static class UploadContext {
        private String uploadId;
        private Long senderId;
        private String fileName;
        private Long fileSize;
        private String fileHash;
        private Integer chunkSize;
        private Integer totalChunks;
        private Set<Integer> uploadedChunks;
        private LocalDateTime uploadTime;

        // Getters and Setters
        public String getUploadId() { return uploadId; }
        public void setUploadId(String uploadId) { this.uploadId = uploadId; }
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        public String getFileHash() { return fileHash; }
        public void setFileHash(String fileHash) { this.fileHash = fileHash; }
        public Integer getChunkSize() { return chunkSize; }
        public void setChunkSize(Integer chunkSize) { this.chunkSize = chunkSize; }
        public Integer getTotalChunks() { return totalChunks; }
        public void setTotalChunks(Integer totalChunks) { this.totalChunks = totalChunks; }
        public Set<Integer> getUploadedChunks() { return uploadedChunks; }
        public void setUploadedChunks(Set<Integer> uploadedChunks) { this.uploadedChunks = uploadedChunks; }
        public LocalDateTime getUploadTime() { return uploadTime; }
        public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }
    }
}
