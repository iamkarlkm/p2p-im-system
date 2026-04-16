package com.im.service.message.service;

import com.im.dto.ImageMessageRequest;
import com.im.dto.ImageMessageResponse;
import com.im.entity.ImageMessage;
import com.im.entity.Message;
import com.im.repository.ImageMessageRepository;
import com.im.repository.MessageRepository;
import com.im.storage.ImageStorageService;
import com.im.websocket.WebSocketMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 图片消息服务
 * 处理图片消息的发送、接收、预览等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageMessageService {

    private final MessageRepository messageRepository;
    private final ImageMessageRepository imageMessageRepository;
    private final ImageStorageService storageService;
    private final WebSocketMessageHandler webSocketHandler;

    /**
     * 发送图片消息
     */
    @Transactional
    public ImageMessageResponse sendImageMessage(Long senderId, ImageMessageRequest request) {
        // 保存消息主表
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setContent("[图片]");
        message.setMessageType("IMAGE");
        message.setSendTime(LocalDateTime.now());
        message.setStatus(0);
        Message savedMessage = messageRepository.save(message);

        // 保存图片消息详情
        ImageMessage imageMessage = new ImageMessage();
        imageMessage.setMessageId(savedMessage.getId());
        imageMessage.setOriginalUrl(request.getImageUrl());
        imageMessage.setThumbnailUrl(generateThumbnailUrl(request.getImageUrl()));
        imageMessage.setFileName(request.getFileName());
        imageMessage.setFileSize(request.getFileSize());
        imageMessage.setWidth(request.getWidth());
        imageMessage.setHeight(request.getHeight());
        imageMessage.setFormat(request.getFormat());
        imageMessage.setUploadTime(LocalDateTime.now());
        ImageMessage savedImage = imageMessageRepository.save(imageMessage);

        // WebSocket推送
        webSocketHandler.sendPrivateMessage(request.getReceiverId(), 
                convertToDTO(savedMessage, savedImage));

        log.info("图片消息发送成功: messageId={}, sender={}", savedMessage.getId(), senderId);
        return convertToResponse(savedMessage, savedImage);
    }

    /**
     * 批量发送图片
     */
    @Transactional
    public List<ImageMessageResponse> sendBatchImages(Long senderId, Long receiverId, List<String> imageUrls) {
        return imageUrls.stream()
                .map(url -> {
                    ImageMessageRequest request = new ImageMessageRequest();
                    request.setReceiverId(receiverId);
                    request.setImageUrl(url);
                    request.setFileName(extractFileName(url));
                    return sendImageMessage(senderId, request);
                })
                .collect(Collectors.toList());
    }

    /**
     * 上传图片文件
     */
    public String uploadImage(Long userId, MultipartFile file) {
        try {
            // 验证图片格式
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            if (!isValidImageFormat(extension)) {
                throw new RuntimeException("不支持的图片格式，仅支持: jpg, jpeg, png, gif, webp");
            }

            // 验证文件大小（最大10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new RuntimeException("图片大小不能超过10MB");
            }

            // 生成存储路径
            String fileName = UUID.randomUUID().toString() + "." + extension;
            String path = String.format("images/%d/%s/%s", 
                    userId, 
                    LocalDateTime.now().toString().substring(0, 7), 
                    fileName);

            // 上传到存储服务
            String imageUrl = storageService.uploadFile(path, file.getBytes(), file.getContentType());
            
            log.info("图片上传成功: userId={}, fileName={}", userId, fileName);
            return imageUrl;

        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取图片预览
     */
    public ImageMessageResponse getImagePreview(Long userId, Long messageId) {
        ImageMessage imageMessage = imageMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("图片消息不存在"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证权限
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权查看此图片");
        }

        return convertToResponse(message, imageMessage);
    }

    /**
     * 获取原图下载链接
     */
    public String getImageDownloadUrl(Long userId, Long messageId) {
        ImageMessage imageMessage = imageMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("图片消息不存在"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证权限
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权下载此图片");
        }

        // 生成临时下载链接（有效期5分钟）
        return storageService.generatePresignedUrl(imageMessage.getOriginalUrl(), 300);
    }

    /**
     * 获取缩略图
     */
    public byte[] getThumbnail(Long userId, Long messageId, Integer width) {
        try {
            ImageMessage imageMessage = imageMessageRepository.findByMessageId(messageId)
                    .orElseThrow(() -> new RuntimeException("图片消息不存在"));

            // 如果已有缩略图URL，直接返回
            if (imageMessage.getThumbnailUrl() != null) {
                return storageService.downloadFile(imageMessage.getThumbnailUrl());
            }

            // 否则实时生成缩略图
            byte[] originalImage = storageService.downloadFile(imageMessage.getOriginalUrl());
            return generateThumbnail(originalImage, width);

        } catch (Exception e) {
            log.error("生成缩略图失败", e);
            throw new RuntimeException("生成缩略图失败: " + e.getMessage());
        }
    }

    /**
     * 删除图片消息
     */
    @Transactional
    public void deleteImageMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证权限（只能删除自己发送的）
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("只能删除自己发送的图片");
        }

        ImageMessage imageMessage = imageMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("图片消息不存在"));

        // 删除存储的文件
        storageService.deleteFile(imageMessage.getOriginalUrl());
        if (imageMessage.getThumbnailUrl() != null) {
            storageService.deleteFile(imageMessage.getThumbnailUrl());
        }

        // 删除数据库记录
        imageMessageRepository.delete(imageMessage);
        messageRepository.delete(message);

        log.info("图片消息已删除: messageId={}", messageId);
    }

    /**
     * 获取聊天中的图片列表
     */
    public List<ImageMessageResponse> getImageList(Long userId, Long targetUserId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sendTime"));
        
        List<Message> messages = messageRepository.findPrivateChatHistory(userId, targetUserId, pageRequest);
        
        return messages.stream()
                .filter(msg -> "IMAGE".equals(msg.getMessageType()))
                .map(msg -> {
                    ImageMessage img = imageMessageRepository.findByMessageId(msg.getId())
                            .orElse(null);
                    return img != null ? convertToResponse(msg, img) : null;
                })
                .filter(resp -> resp != null)
                .collect(Collectors.toList());
    }

    // ============ 私有方法 ============

    private String generateThumbnailUrl(String originalUrl) {
        // 在实际实现中，这里应该返回缩略图的URL
        // 例如通过添加后缀或参数来区分
        return originalUrl + "?thumbnail=true&width=200";
    }

    private byte[] generateThumbnail(byte[] originalImage, Integer width) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(originalImage);
        BufferedImage originalBufferedImage = ImageIO.read(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        Thumbnails.of(originalBufferedImage)
                .width(width)
                .outputFormat("jpg")
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    private String extractFileName(String url) {
        if (url == null || url.isEmpty()) {
            return "unknown.jpg";
        }
        int lastSlash = url.lastIndexOf('/');
        int lastQuestion = url.lastIndexOf('?');
        if (lastQuestion > lastSlash) {
            return url.substring(lastSlash + 1, lastQuestion);
        }
        return url.substring(lastSlash + 1);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidImageFormat(String extension) {
        return extension.matches("^(jpg|jpeg|png|gif|webp)$");
    }

    private Object convertToDTO(Message message, ImageMessage imageMessage) {
        // 返回DTO对象给WebSocket
        ImageMessageResponse dto = convertToResponse(message, imageMessage);
        return dto;
    }

    private ImageMessageResponse convertToResponse(Message message, ImageMessage imageMessage) {
        ImageMessageResponse response = new ImageMessageResponse();
        response.setMessageId(message.getId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setOriginalUrl(imageMessage.getOriginalUrl());
        response.setThumbnailUrl(imageMessage.getThumbnailUrl());
        response.setFileName(imageMessage.getFileName());
        response.setFileSize(imageMessage.getFileSize());
        response.setWidth(imageMessage.getWidth());
        response.setHeight(imageMessage.getHeight());
        response.setFormat(imageMessage.getFormat());
        response.setSendTime(message.getSendTime());
        return response;
    }
}
