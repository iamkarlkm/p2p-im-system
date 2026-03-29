package com.im.backend.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 文件存储服务
 * 负责文件的物理存储和读取
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.storage.root-path:./uploads}")
    private String rootPath;

    @Value("${file.storage.create-daily-dir:true}")
    private boolean createDailyDir;

    private Path storageLocation;

    @PostConstruct
    public void init() {
        this.storageLocation = Paths.get(rootPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageLocation);
            logger.info("File storage initialized at: {}", storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("无法创建文件存储目录: " + rootPath, e);
        }
    }

    /**
     * 存储文件
     */
    public String store(MultipartFile file, String fileId) throws IOException {
        // 获取原始文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // 验证文件名
        if (originalFilename.contains("..")) {
            throw new IOException("文件名包含非法字符: " + originalFilename);
        }

        // 确定存储路径
        Path targetLocation = getTargetLocation(fileId, originalFilename);

        // 创建父目录
        Files.createDirectories(targetLocation.getParent());

        // 复制文件
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        logger.info("File stored: {} at {}", fileId, targetLocation);

        return targetLocation.toString();
    }

    /**
     * 加载文件
     */
    public Resource load(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Resource resource = new UrlResource(path.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("无法读取文件: " + filePath);
        }
    }

    /**
     * 删除文件
     */
    public void delete(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
        logger.info("File deleted: {}", filePath);
    }

    /**
     * 获取目标存储位置
     */
    private Path getTargetLocation(String fileId, String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String filename = fileId + (extension.isEmpty() ? "" : "." + extension);

        if (createDailyDir) {
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            return storageLocation.resolve(dateDir).resolve(filename);
        } else {
            return storageLocation.resolve(filename);
        }
    }

    /**
     * 计算文件哈希
     */
    public String calculateHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("Failed to calculate file hash: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 获取存储统计信息
     */
    public StorageStats getStorageStats() throws IOException {
        StorageStats stats = new StorageStats();
        stats.setTotalSize(calculateDirectorySize(storageLocation));
        stats.setFileCount(countFiles(storageLocation));
        return stats;
    }

    /**
     * 计算目录大小
     */
    private long calculateDirectorySize(Path path) throws IOException {
        return Files.walk(path)
            .filter(Files::isRegularFile)
            .mapToLong(p -> {
                try {
                    return Files.size(p);
                } catch (IOException e) {
                    return 0;
                }
            })
            .sum();
    }

    /**
     * 统计文件数量
     */
    private long countFiles(Path path) throws IOException {
        return Files.walk(path)
            .filter(Files::isRegularFile)
            .count();
    }

    // ========== 存储统计类 ==========

    public static class StorageStats {
        private long totalSize;
        private long fileCount;

        public long getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(long totalSize) {
            this.totalSize = totalSize;
        }

        public long getFileCount() {
            return fileCount;
        }

        public void setFileCount(long fileCount) {
            this.fileCount = fileCount;
        }

        public String getFormattedTotalSize() {
            if (totalSize < 1024) {
                return totalSize + " B";
            } else if (totalSize < 1024 * 1024) {
                return String.format("%.2f KB", totalSize / 1024.0);
            } else if (totalSize < 1024 * 1024 * 1024) {
                return String.format("%.2f MB", totalSize / (1024.0 * 1024.0));
            } else {
                return String.format("%.2f GB", totalSize / (1024.0 * 1024.0 * 1024.0));
            }
        }
    }
}
