package com.im.backend.service;

import com.im.backend.dto.FileInfoDTO;
import com.im.backend.dto.FileUploadRequest;
import com.im.backend.dto.FileUploadResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件存储服务接口
 * 功能#17: 文件上传下载
 */
public interface FileStorageService {
    
    /**
     * 上传文件
     */
    FileUploadResponse uploadFile(MultipartFile file, FileUploadRequest request, Long ownerId) throws IOException;
    
    /**
     * 下载文件
     */
    Resource downloadFile(String storedName) throws IOException;
    
    /**
     * 获取文件信息
     */
    FileInfoDTO getFileInfo(Long fileId);
    
    /**
     * 删除文件
     */
    void deleteFile(Long fileId, Long operatorId);
    
    /**
     * 获取用户文件列表
     */
    Page<FileInfoDTO> getUserFiles(Long ownerId, Pageable pageable);
    
    /**
     * 搜索文件
     */
    Page<FileInfoDTO> searchFiles(String keyword, Long ownerId, Pageable pageable);
    
    /**
     * 获取公开文件列表
     */
    Page<FileInfoDTO> getPublicFiles(Pageable pageable);
    
    /**
     * 增加下载计数
     */
    void incrementDownloadCount(Long fileId);
    
    /**
     * 获取用户存储空间使用量(字节)
     */
    Long getUserStorageUsage(Long ownerId);
}
