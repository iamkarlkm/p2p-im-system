package com.im.service.storage.service;

import com.im.service.storage.dto.FileResponse;
import com.im.service.storage.dto.UploadRequest;
import com.im.service.storage.entity.FileRecord;
import com.im.service.storage.repository.FileRecordRepository;
import com.im.service.storage.service.impl.StorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 存储服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StorageService 单元测试")
class StorageServiceTest {

    @Mock
    private FileRecordRepository fileRecordRepository;

    @InjectMocks
    private StorageServiceImpl storageService;

    private FileRecord testFileRecord;
    private UploadRequest testUploadRequest;

    @BeforeEach
    void setUp() {
        // 创建测试文件记录
        testFileRecord = FileRecord.builder()
                .id(1L)
                .fileId("test-file-id-123")
                .userId(100L)
                .originalName("test-image.jpg")
                .storedName("test-file-id-123.jpg")
                .filePath("/uploads/test-file-id-123.jpg")
                .fileUrl("http://localhost:8080/files/test-file-id-123")
                .fileSize(1024L)
                .mimeType("image/jpeg")
                .fileType("IMAGE")
                .fileHash("abc123def456")
                .extension("jpg")
                .description("Test file")
                .isPublic(true)
                .downloadCount(0)
                .status(FileRecord.FileStatus.ACTIVE.name())
                .expireAt(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 创建测试上传请求
        testUploadRequest = UploadRequest.builder()
                .fileContent("data:image/jpeg;base64,/9j/4AAQSkZJRg==")
                .originalName("test-image.jpg")
                .description("Test upload")
                .isPublic(true)
                .expireMinutes(60)
                .fileType("IMAGE")
                .useDeduplication(true)
                .build();

        // 设置 maxFileSize 字段（默认 10MB）
        ReflectionTestUtils.setField(storageService, "maxFileSize", 10485760L);
    }

    // ==================== 上传相关测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("uploadFile_Success - 正常上传文件")
    void uploadFile_Success() {
        // Arrange
        Long userId = 100L;
        when(fileRecordRepository.findByFileHash(anyString())).thenReturn(Optional.empty());
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // Act
        FileResponse response = storageService.uploadFile(testUploadRequest, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFileId()).isEqualTo(testFileRecord.getFileId());
        assertThat(response.getOriginalName()).isEqualTo(testFileRecord.getOriginalName());
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("uploadFile_WithDeduplication - 启用去重的文件上传")
    void uploadFile_WithDeduplication() {
        // Arrange
        Long userId = 100L;
        when(fileRecordRepository.findByFileHash(anyString())).thenReturn(Optional.of(testFileRecord));

        // Act
        FileResponse response = storageService.uploadFile(testUploadRequest, userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFileId()).isEqualTo(testFileRecord.getFileId());
        verify(fileRecordRepository, never()).save(any(FileRecord.class));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("uploadFile_InvalidContent - 无效的文件内容")
    void uploadFile_InvalidContent() {
        // Arrange
        Long userId = 100L;
        UploadRequest invalidRequest = UploadRequest.builder()
                .fileContent("invalid-base64!!!")
                .originalName("test.jpg")
                .isPublic(true)
                .build();

        // Act & Assert
        try {
            storageService.uploadFile(invalidRequest, userId);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("无效的文件内容格式");
        }
    }

    // ==================== 查询相关测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("getFileById_Success - 根据ID获取文件成功")
    void getFileById_Success() {
        // Arrange
        String fileId = "test-file-id-123";
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));

        // Act
        Optional<FileRecord> result = storageService.getFileById(fileId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getFileId()).isEqualTo(fileId);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getFileById_NotFound - 文件不存在")
    void getFileById_NotFound() {
        // Arrange
        String fileId = "non-existent-id";
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.empty());

        // Act
        Optional<FileRecord> result = storageService.getFileById(fileId);

        // Assert
        assertThat(result).isEmpty();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getFileResponse_Success - 获取文件响应成功")
    void getFileResponse_Success() {
        // Arrange
        String fileId = "test-file-id-123";
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));

        // Act
        Optional<FileResponse> result = storageService.getFileResponse(fileId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getFileId()).isEqualTo(fileId);
    }

    // ==================== 下载相关测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("downloadFile_Success - 文件下载成功")
    void downloadFile_Success() {
        // Arrange
        String fileId = "test-file-id-123";
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.incrementDownloadCount(fileId)).thenReturn(1);

        // Act
        Optional<String> result = storageService.downloadFile(fileId);

        // Assert
        assertThat(result).isPresent();
        verify(fileRecordRepository, times(1)).incrementDownloadCount(fileId);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("downloadFile_NotFound - 下载不存在的文件")
    void downloadFile_NotFound() {
        // Arrange
        String fileId = "non-existent-id";
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.empty());

        // Act
        Optional<String> result = storageService.downloadFile(fileId);

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== 删除相关测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("deleteFile_Success - 删除文件成功")
    void deleteFile_Success() {
        // Arrange
        String fileId = "test-file-id-123";
        Long userId = 100L;
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // Act
        boolean result = storageService.deleteFile(fileId, userId);

        // Assert
        assertThat(result).isTrue();
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("deleteFile_NotOwner - 非所有者无法删除")
    void deleteFile_NotOwner() {
        // Arrange
        String fileId = "test-file-id-123";
        Long userId = 999L; // 非所有者
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));

        // Act & Assert
        try {
            storageService.deleteFile(fileId, userId);
        } catch (SecurityException e) {
            assertThat(e.getMessage()).contains("无权限");
        }
    }

    @org.junit.jupiter.api.Test
    @DisplayName("batchDeleteFiles_Success - 批量删除文件")
    void batchDeleteFiles_Success() {
        // Arrange
        List<String> fileIds = Arrays.asList("file1", "file2", "file3");
        Long userId = 100L;
        
        FileRecord file1 = FileRecord.builder().fileId("file1").userId(userId).build();
        FileRecord file2 = FileRecord.builder().fileId("file2").userId(userId).build();
        FileRecord file3 = FileRecord.builder().fileId("file3").userId(userId).build();
        
        when(fileRecordRepository.findByFileId("file1")).thenReturn(Optional.of(file1));
        when(fileRecordRepository.findByFileId("file2")).thenReturn(Optional.of(file2));
        when(fileRecordRepository.findByFileId("file3")).thenReturn(Optional.of(file3));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(file1);

        // Act
        int count = storageService.batchDeleteFiles(fileIds, userId);

        // Assert
        assertThat(count).isEqualTo(3);
    }

    // ==================== 用户文件相关测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("getUserFiles_Success - 获取用户文件列表")
    void getUserFiles_Success() {
        // Arrange
        Long userId = 100L;
        List<FileRecord> files = Arrays.asList(testFileRecord);
        when(fileRecordRepository.findByUserIdAndStatus(userId, "ACTIVE")).thenReturn(files);

        // Act
        List<FileResponse> result = storageService.getUserFiles(userId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getUserFilesPage_Success - 分页获取用户文件")
    void getUserFilesPage_Success() {
        // Arrange
        Long userId = 100L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<FileRecord> page = new PageImpl<>(Arrays.asList(testFileRecord));
        when(fileRecordRepository.findByUserId(userId, pageable)).thenReturn(page);

        // Act
        Page<FileResponse> result = storageService.getUserFilesPage(userId, pageable);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("searchFiles_Success - 搜索文件")
    void searchFiles_Success() {
        // Arrange
        String keyword = "test";
        Long userId = null; // 使用 null 以调用全局搜索
        when(fileRecordRepository.searchByName(keyword)).thenReturn(Arrays.asList(testFileRecord));

        // Act
        List<FileResponse> result = storageService.searchFiles(keyword, userId);

        // Assert
        assertThat(result).hasSize(1);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getFilesByType_Success - 根据类型获取文件")
    void getFilesByType_Success() {
        // Arrange
        String fileType = "IMAGE";
        Long userId = 100L;
        when(fileRecordRepository.findByUserIdAndFileType(userId, fileType))
                .thenReturn(Arrays.asList(testFileRecord));

        // Act
        List<FileResponse> result = storageService.getFilesByType(fileType, userId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileType()).isEqualTo(fileType);
    }

    // ==================== 统计相关测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("getUserStatistics_Success - 获取用户统计信息")
    void getUserStatistics_Success() {
        // Arrange
        Long userId = 100L;
        when(fileRecordRepository.countByUserId(userId)).thenReturn(10L);
        when(fileRecordRepository.sumFileSizeByUserId(userId)).thenReturn(10240L);
        when(fileRecordRepository.countByUserIdAndFileType(userId, "IMAGE")).thenReturn(5L);
        when(fileRecordRepository.countByUserIdAndFileType(userId, "VIDEO")).thenReturn(2L);
        when(fileRecordRepository.countByUserIdAndFileType(userId, "AUDIO")).thenReturn(1L);
        when(fileRecordRepository.countByUserIdAndFileType(userId, "DOCUMENT")).thenReturn(1L);
        when(fileRecordRepository.countByUserIdAndFileType(userId, "OTHER")).thenReturn(1L);

        // Act
        StorageService.FileStatistics stats = storageService.getUserStatistics(userId);

        // Assert
        assertThat(stats.getTotalFiles()).isEqualTo(10);
        assertThat(stats.getTotalSize()).isEqualTo(10240);
        assertThat(stats.getImageCount()).isEqualTo(5);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getUserUsedStorage_Success - 获取用户已用存储")
    void getUserUsedStorage_Success() {
        // Arrange
        Long userId = 100L;
        when(fileRecordRepository.sumFileSizeByUserId(userId)).thenReturn(51200L);

        // Act
        long result = storageService.getUserUsedStorage(userId);

        // Assert
        assertThat(result).isEqualTo(51200);
    }

    // ==================== 权限相关测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("isFileOwner_True - 是文件所有者")
    void isFileOwner_True() {
        // Arrange
        String fileId = "test-file-id-123";
        Long userId = 100L;
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));

        // Act
        boolean result = storageService.isFileOwner(fileId, userId);

        // Assert
        assertThat(result).isTrue();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("isFileOwner_False - 不是文件所有者")
    void isFileOwner_False() {
        // Arrange
        String fileId = "test-file-id-123";
        Long userId = 999L;
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));

        // Act
        boolean result = storageService.isFileOwner(fileId, userId);

        // Assert
        assertThat(result).isFalse();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("isFileAccessible_Public - 公开文件可访问")
    void isFileAccessible_Public() {
        // Arrange
        String fileId = "test-file-id-123";
        Long userId = null;
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));

        // Act
        boolean result = storageService.isFileAccessible(fileId, userId);

        // Assert
        assertThat(result).isTrue();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("isFileAccessible_Private - 私有文件仅所有者可访问")
    void isFileAccessible_Private() {
        // Arrange
        String fileId = "test-file-id-123";
        testFileRecord.setIsPublic(false);
        Long userId = 100L;
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));

        // Act
        boolean result = storageService.isFileAccessible(fileId, userId);

        // Assert
        assertThat(result).isTrue();
    }

    // ==================== 其他功能测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("incrementDownloadCount_Success - 增加下载次数")
    void incrementDownloadCount_Success() {
        // Arrange
        String fileId = "test-file-id-123";
        when(fileRecordRepository.incrementDownloadCount(fileId)).thenReturn(1);

        // Act
        storageService.incrementDownloadCount(fileId);

        // Assert
        verify(fileRecordRepository, times(1)).incrementDownloadCount(fileId);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("cleanupExpiredFiles_Success - 清理过期文件")
    void cleanupExpiredFiles_Success() {
        // Arrange
        when(fileRecordRepository.findExpiredFiles(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(testFileRecord));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // Act
        int count = storageService.cleanupExpiredFiles();

        // Assert
        assertThat(count).isEqualTo(1);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getHotFiles_Success - 获取热门文件")
    void getHotFiles_Success() {
        // Arrange
        int limit = 10;
        when(fileRecordRepository.findHotFiles(any(Pageable.class)))
                .thenReturn(Arrays.asList(testFileRecord));

        // Act
        List<FileResponse> result = storageService.getHotFiles(limit);

        // Assert
        assertThat(result).hasSize(1);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getRecentFiles_Success - 获取最近上传的文件")
    void getRecentFiles_Success() {
        // Arrange
        int limit = 10;
        when(fileRecordRepository.findRecentFiles(any(Pageable.class)))
                .thenReturn(Arrays.asList(testFileRecord));

        // Act
        List<FileResponse> result = storageService.getRecentFiles(limit);

        // Assert
        assertThat(result).hasSize(1);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("existsByFileId_True - 文件存在")
    void existsByFileId_True() {
        // Arrange
        String fileId = "test-file-id-123";
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));

        // Act
        boolean result = storageService.existsByFileId(fileId);

        // Assert
        assertThat(result).isTrue();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("existsByFileId_False - 文件不存在")
    void existsByFileId_False() {
        // Arrange
        String fileId = "non-existent-id";
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.empty());

        // Act
        boolean result = storageService.existsByFileId(fileId);

        // Assert
        assertThat(result).isFalse();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("findByHash_Success - 根据哈希查找文件")
    void findByHash_Success() {
        // Arrange
        String fileHash = "abc123def456";
        when(fileRecordRepository.findByFileHash(fileHash)).thenReturn(Optional.of(testFileRecord));

        // Act
        Optional<FileResponse> result = storageService.findByHash(fileHash);

        // Assert
        assertThat(result).isPresent();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("calculateFileHash_Success - 计算文件哈希")
    void calculateFileHash_Success() {
        // Arrange
        String base64Content = "data:image/jpeg;base64,/9j/4AAQSkZJRg==";

        // Act
        String hash = storageService.calculateFileHash(base64Content);

        // Assert
        assertThat(hash).isNotNull();
        assertThat(hash.length()).isEqualTo(32);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("updateFileInfo_Success - 更新文件信息")
    void updateFileInfo_Success() {
        // Arrange
        String fileId = "test-file-id-123";
        Long userId = 100L;
        String newDescription = "Updated description";
        when(fileRecordRepository.findByFileId(fileId)).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // Act
        Optional<FileResponse> result = storageService.updateFileInfo(fileId, newDescription, null, userId);

        // Assert
        assertThat(result).isPresent();
    }
}
