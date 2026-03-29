package com.im.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件服务 - 处理图片、文件、语音等上传
 */
@Service
public class FileService {

    @Value("${app.file.upload-path:./uploads}")
    private String uploadPath;

    @Value("${app.file.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        // 创建上传目录
        try {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 创建子目录
            File imagesDir = new File(uploadPath + "/images");
            File voicesDir = new File(uploadPath + "/voices");
            File filesDir = new File(uploadPath + "/files");
            File avatarsDir = new File(uploadPath + "/avatars");
            File groupsDir = new File(uploadPath + "/groups");
            
            if (!imagesDir.exists()) imagesDir.mkdirs();
            if (!voicesDir.exists()) voicesDir.mkdirs();
            if (!filesDir.exists()) filesDir.mkdirs();
            if (!avatarsDir.exists()) avatarsDir.mkdirs();
            if (!groupsDir.exists()) groupsDir.mkdirs();
            
        } catch (Exception e) {
            System.err.println("Failed to create upload directories: " + e.getMessage());
        }
    }

    /**
     * 保存图片
     */
    public String saveImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        
        // 生成唯一文件名
        String filename = UUID.randomUUID().toString() + "." + extension;
        
        // 验证图片格式
        if (!isImageExtension(extension)) {
            throw new IOException("不支持的图片格式: " + extension);
        }
        
        // 保存文件
        Path path = Paths.get(uploadPath + "/images", filename);
        Files.write(path, file.getBytes());
        
        return baseUrl + "/images/" + filename;
    }

    /**
     * 保存语音
     */
    public String saveVoice(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        
        // 生成唯一文件名
        String filename = UUID.randomUUID().toString() + "." + extension;
        
        // 验证音频格式
        if (!isVoiceExtension(extension)) {
            throw new IOException("不支持的音频格式: " + extension);
        }
        
        // 保存文件
        Path path = Paths.get(uploadPath + "/voices", filename);
        Files.write(path, file.getBytes());
        
        return baseUrl + "/voices/" + filename;
    }

    /**
     * 保存普通文件
     */
    public String saveFile(MultipartFile file, String type) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        
        // 生成唯一文件名
        String filename = UUID.randomUUID().toString() + "_" + originalFilename;
        
        // 保存文件
        Path path = Paths.get(uploadPath + "/files", filename);
        Files.write(path, file.getBytes());
        
        return baseUrl + "/files/" + filename;
    }

    /**
     * 保存用户头像
     */
    public String saveUserAvatar(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        
        // 验证图片格式
        if (!isImageExtension(extension)) {
            throw new IOException("不支持的图片格式: " + extension);
        }
        
        // 生成唯一文件名
        String filename = UUID.randomUUID().toString() + "." + extension;
        
        // 保存文件
        Path path = Paths.get(uploadPath + "/avatars", filename);
        Files.write(path, file.getBytes());
        
        return baseUrl + "/avatars/" + filename;
    }

    /**
     * 保存群头像
     */
    public String saveGroupAvatar(MultipartFile file, Long groupId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        
        // 验证图片格式
        if (!isImageExtension(extension)) {
            throw new IOException("不支持的图片格式: " + extension);
        }
        
        // 生成唯一文件名
        String filename = "group_" + groupId + "_" + UUID.randomUUID().toString() + "." + extension;
        
        // 保存文件
        Path path = Paths.get(uploadPath + "/groups", filename);
        Files.write(path, file.getBytes());
        
        return baseUrl + "/groups/" + filename;
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        // 从URL中提取文件路径
        String relativePath = fileUrl.replace(baseUrl + "/", "");
        Path path = Paths.get(uploadPath, relativePath);
        
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 检查是否为图片扩展名
     */
    private boolean isImageExtension(String extension) {
        return extension.equals("jpg") || 
               extension.equals("jpeg") || 
               extension.equals("png") || 
               extension.equals("gif") || 
               extension.equals("webp") ||
               extension.equals("bmp");
    }

    /**
     * 检查是否为语音扩展名
     */
    private boolean isVoiceExtension(String extension) {
        return extension.equals("mp3") || 
               extension.equals("wav") || 
               extension.equals("ogg") ||
               extension.equals("m4a") ||
               extension.equals("aac");
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        String relativePath = fileUrl.replace(baseUrl + "/", "");
        Path path = Paths.get(uploadPath, relativePath);
        
        return Files.exists(path);
    }

    /**
     * 获取文件信息
     */
    public FileInfo getFileInfo(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        
        String relativePath = fileUrl.replace(baseUrl + "/", "");
        Path path = Paths.get(uploadPath, relativePath);
        
        if (!Files.exists(path)) {
            return null;
        }
        
        try {
            FileInfo info = new FileInfo();
            info.setUrl(fileUrl);
            info.setSize(Files.size(path));
            info.setContentType(Files.probeContentType(path));
            info.setCreateTime(Files.getLastModifiedTime(path).toMillis());
            return info;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 文件信息类
     */
    public static class FileInfo {
        private String url;
        private long size;
        private String contentType;
        private long createTime;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
