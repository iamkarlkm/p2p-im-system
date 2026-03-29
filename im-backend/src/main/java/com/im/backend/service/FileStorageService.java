package com.im.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.storage.path:./uploads}")
    private String storagePath;

    @Value("${file.storage.temp-path:./temp}")
    private String tempPath;

    @Value("${file.storage.base-url:http://localhost:8080/files}")
    private String baseUrl;

    private Path storageDir;
    private Path tempDir;

    @PostConstruct
    public void init() throws IOException {
        storageDir = Paths.get(storagePath).toAbsolutePath().normalize();
        tempDir = Paths.get(tempPath).toAbsolutePath().normalize();

        // 创建存储目录
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
            logger.info("创建存储目录: {}", storageDir);
        }

        // 创建临时目录
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
            logger.info("创建临时目录: {}", tempDir);
        }
    }

    /**
     * 存储文件分片
     */
    public Path storeChunk(String uploadId, int chunkIndex, byte[] data) throws IOException {
        Path chunkDir = tempDir.resolve(uploadId);
        if (!Files.exists(chunkDir)) {
            Files.createDirectories(chunkDir);
        }

        Path chunkPath = chunkDir.resolve(String.valueOf(chunkIndex));
        Files.write(chunkPath, data);

        logger.debug("存储分片: uploadId={}, chunkIndex={}, size={}", uploadId, chunkIndex, data.length);
        return chunkPath;
    }

    /**
     * 合并文件分片
     */
    public Path mergeChunks(String storagePath, List<com.im.backend.model.FileChunk> chunks) throws IOException {
        Path targetPath = storageDir.resolve(storagePath);
        Path parentDir = targetPath.getParent();

        // 创建目标目录
        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // 创建临时合并文件
        Path tempFile = tempDir.resolve(UUID.randomUUID().toString() + ".tmp");

        try {
            // 按顺序合并分片
            for (com.im.backend.model.FileChunk chunk : chunks) {
                Path chunkPath = Paths.get(chunk.getStoragePath());
                byte[] chunkData = Files.readAllBytes(chunkPath);
                Files.write(tempFile, chunkData, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }

            // 移动到最终位置
            Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("文件合并完成: {}", targetPath);
            return targetPath;

        } finally {
            // 清理临时文件
            if (Files.exists(tempFile)) {
                Files.deleteIfExists(tempFile);
            }
        }
    }

    /**
     * 删除分片
     */
    public void deleteChunk(String chunkPath) throws IOException {
        Files.deleteIfExists(Paths.get(chunkPath));
    }

    /**
     * 删除文件
     */
    public void deleteFile(String storagePath) throws IOException {
        Path filePath = storageDir.resolve(storagePath);
        Files.deleteIfExists(filePath);
        logger.info("删除文件: {}", filePath);
    }

    /**
     * 获取文件
     */
    public Path getFile(String storagePath) {
        return storageDir.resolve(storagePath);
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String storagePath) {
        return Files.exists(storageDir.resolve(storagePath));
    }

    /**
     * 获取文件访问URL
     */
    public String getFileUrl(String storagePath) {
        return baseUrl + "/" + storagePath;
    }

    /**
     * 获取文件大小
     */
    public long getFileSize(String storagePath) throws IOException {
        Path filePath = storageDir.resolve(storagePath);
        return Files.size(filePath);
    }

    /**
     * 复制文件
     */
    public String copyFile(String sourceStoragePath, String targetFileName) throws IOException {
        Path source = storageDir.resolve(sourceStoragePath);
        String newStoragePath = generateStoragePath(targetFileName);
        Path target = storageDir.resolve(newStoragePath);

        Path parentDir = target.getParent();
        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        logger.info("复制文件: {} -> {}", source, target);

        return newStoragePath;
    }

    /**
     * 移动文件
     */
    public String moveFile(String sourceStoragePath, String targetPath) throws IOException {
        Path source = storageDir.resolve(sourceStoragePath);
        String newStoragePath = targetPath + "/" + source.getFileName().toString();
        Path target = storageDir.resolve(newStoragePath);

        Path parentDir = target.getParent();
        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        logger.info("移动文件: {} -> {}", source, target);

        return newStoragePath;
    }

    /**
     * 清理临时目录
     */
    public void cleanupTemp() throws IOException {
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            logger.warn("删除临时文件失败: {}", path, e);
                        }
                    });
        }
    }

    private String generateStoragePath(String fileName) {
        String datePath = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
        return datePath + "/" + UUID.randomUUID().toString() + ext;
    }

    public Path getStorageDir() {
        return storageDir;
    }

    public Path getTempDir() {
        return tempDir;
    }
}
