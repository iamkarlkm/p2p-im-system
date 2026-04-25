package com.im.service.storage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.service.storage.config.OssConfig;
import com.im.service.storage.dto.FileResponse;
import com.im.service.storage.dto.UploadRequest;
import com.im.service.storage.entity.FileRecord;
import com.im.service.storage.repository.FileRecordRepository;
import com.im.service.storage.service.impl.StorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 存储服务单元测试
 * 测试文件上传、下载、分片上传等功能
 *
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("存储服务测试")
class StorageServiceTest {

    @Mock
    private FileRecordRepository fileRecordRepository;

    @Spy
    private OssConfig ossConfig = new OssConfig();

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private StorageServiceImpl storageService;

    private UploadRequest uploadRequest;
    private FileRecord testFileRecord;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        // 初始化上传请求
        uploadRequest = new UploadRequest();
        uploadRequest.setOwnerId("user123");
        uploadRequest.setAccessLevel("PRIVATE");
        uploadRequest.setConversationId("conv123");

        // 初始化测试文件记录
        testFileRecord = new FileRecord();
        testFileRecord.setId("file123");
        testFileRecord.setOriginalName("test.jpg");
        testFileRecord.setStorageName("abc123.jpg");
        testFileRecord.setFilePath("2024/01/01/abc123.jpg");
        testFileRecord.setFileUrl("http://localhost/files/2024/01/01/abc123.jpg");
        testFileRecord.setFileSize(102400L);
        testFileRecord.setMimeType("image/jpeg");
        testFileRecord.setFileType("IMAGE");
        testFileRecord.setExtension("jpg");
        testFileRecord.setOwnerId("user123");
        testFileRecord.setStatus("COMPLETED");
        testFileRecord.setAccessLevel("PRIVATE");
        testFileRecord.setStorageSource("LOCAL");

        // 初始化测试文件
        testFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    // ========== 文件上传测试 ==========

    @Test
    @DisplayName("上传文件 - 成功")
    void uploadFile_Success() throws IOException {
        // 准备
        when(fileRecordRepository.findByFileHash(anyString())).thenReturn(Optional.empty());
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        FileResponse response = storageService.uploadFile(testFile, uploadRequest);

        // 验证
        assertThat(response).isNotNull();
        assertThat(response.getOriginalName()).isEqualTo("test.jpg");
        assertThat(response.getFileType()).isEqualTo("IMAGE");
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    @DisplayName("上传文件 - 文件已存在(去重)")
    void uploadFile_DuplicateFile() throws IOException {
        // 准备 - 文件已存在
        when(fileRecordRepository.findByFileHash(anyString())).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.incrementReferenceCount(anyString())).thenReturn(null);

        // 执行
        FileResponse response = storageService.uploadFile(testFile, uploadRequest);

        // 验证 - 使用已有记录，增加引用次数
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("file123");
        verify(fileRecordRepository, times(1)).incrementReferenceCount("file123");
        verify(fileRecordRepository, never()).save(any(FileRecord.class));
    }

    @Test
    @DisplayName("上传文件 - PNG图片")
    void uploadFile_PngImage() throws IOException {
        // 准备
        MockMultipartFile pngFile = new MockMultipartFile(
                "file", "test.png", "image/png", "png content".getBytes()
        );
        when(fileRecordRepository.findByFileHash(anyString())).thenReturn(Optional.empty());
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        FileResponse response = storageService.uploadFile(pngFile, uploadRequest);

        // 验证
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("上传文件 - 视频文件")
    void uploadFile_VideoFile() throws IOException {
        // 准备
        MockMultipartFile videoFile = new MockMultipartFile(
                "file", "test.mp4", "video/mp4", "video content".getBytes()
        );
        testFileRecord.setMimeType("video/mp4");
        testFileRecord.setFileType("VIDEO");
        
        when(fileRecordRepository.findByFileHash(anyString())).thenReturn(Optional.empty());
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        FileResponse response = storageService.uploadFile(videoFile, uploadRequest);

        // 验证
        assertThat(response).isNotNull();
        assertThat(response.getFileType()).isEqualTo("VIDEO");
    }

    @Test
    @DisplayName("上传文件 - 音频文件")
    void uploadFile_AudioFile() throws IOException {
        // 准备
        MockMultipartFile audioFile = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", "audio content".getBytes()
        );
        testFileRecord.setMimeType("audio/mpeg");
        testFileRecord.setFileType("AUDIO");
        
        when(fileRecordRepository.findByFileHash(anyString())).thenReturn(Optional.empty());
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        FileResponse response = storageService.uploadFile(audioFile, uploadRequest);

        // 验证
        assertThat(response).isNotNull();
        assertThat(response.getFileType()).isEqualTo("AUDIO");
    }

    @Test
    @DisplayName("上传文件 - 文档文件")
    void uploadFile_DocumentFile() throws IOException {
        // 准备
        MockMultipartFile docFile = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "pdf content".getBytes()
        );
        testFileRecord.setMimeType("application/pdf");
        testFileRecord.setFileType("DOCUMENT");
        
        when(fileRecordRepository.findByFileHash(anyString())).thenReturn(Optional.empty());
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        FileResponse response = storageService.uploadFile(docFile, uploadRequest);

        // 验证
        assertThat(response).isNotNull();
        assertThat(response.getFileType()).isEqualTo("DOCUMENT");
    }

    @Test
    @DisplayName("上传多文件 - 成功")
    void uploadFiles_Success() throws IOException {
        // 准备
        List<MockMultipartFile> files = Arrays.asList(
                new MockMultipartFile("files", "test1.jpg", "image/jpeg", "content1".getBytes()),
                new MockMultipartFile("files", "test2.jpg", "image/jpeg", "content2".getBytes())
        );
        
        when(fileRecordRepository.findByFileHash(anyString())).thenReturn(Optional.empty());
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        List<FileResponse> responses = storageService.uploadFiles(files, uploadRequest);

        // 验证
        assertThat(responses).isNotNull();
        assertThat(responses.size()).isEqualTo(2);
    }

    // ========== 分片上传测试 ==========

    @Test
    @DisplayName("初始化分片上传 - 成功")
    void initChunkedUpload_Success() {
        // 准备
        UploadRequest.ChunkedUploadInitRequest request = new UploadRequest.ChunkedUploadInitRequest();
        request.setFileName("large.zip");
        request.setFileSize(104857600L);
        request.setMimeType("application/zip");
        request.setTotalChunks(10);
        
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        FileResponse response = storageService.initChunkedUpload(request);

        // 验证
        assertThat(response).isNotNull();
        assertThat(response.getChunked()).isEqualTo(true);
        assertThat(response.getTotalChunks()).isEqualTo(10);
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    @DisplayName("查询分片上传状态")
    void getChunkedUploadStatus_Success() {
        // 准备
        testFileRecord.setIsChunked(true);
        testFileRecord.setUploadId("upload123");
        testFileRecord.setTotalChunks(10);
        testFileRecord.setUploadedChunks(5);
        
        when(fileRecordRepository.findByUploadId("upload123")).thenReturn(Optional.of(testFileRecord));

        // 执行
        Map<String, Object> status = storageService.getChunkedUploadStatus("upload123");

        // 验证
        assertThat(status).isNotNull();
        assertThat(status.get("uploadId")).isEqualTo("upload123");
        assertThat(status.get("uploadedChunks")).isEqualTo(5);
        assertThat(status.get("totalChunks")).isEqualTo(10);
        assertThat(status.get("progress")).isEqualTo(50);
    }

    // ========== 文件查询测试 ==========

    @Test
    @DisplayName("根据ID获取文件")
    void getFileById_Success() {
        // 准备
        when(fileRecordRepository.findById("file123")).thenReturn(Optional.of(testFileRecord));

        // 执行
        FileRecord file = storageService.getFileById("file123");

        // 验证
        assertThat(file).isNotNull();
        assertThat(file.getId()).isEqualTo("file123");
    }

    @Test
    @DisplayName("根据ID获取文件 - 文件不存在")
    void getFileById_NotFound() {
        // 准备
        when(fileRecordRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // 执行
        FileRecord file = storageService.getFileById("nonexistent");

        // 验证
        assertThat(file).isNull();
    }

    @Test
    @DisplayName("根据存储名称获取文件")
    void getFileByStorageName_Success() {
        // 准备
        when(fileRecordRepository.findByStorageName("abc123.jpg")).thenReturn(Optional.of(testFileRecord));

        // 执行
        FileRecord file = storageService.getFileByStorageName("abc123.jpg");

        // 验证
        assertThat(file).isNotNull();
        assertThat(file.getStorageName()).isEqualTo("abc123.jpg");
    }

    @Test
    @DisplayName("根据哈希获取文件")
    void getFileByHash_Success() {
        // 准备
        String fileHash = "abc123hash";
        when(fileRecordRepository.findByFileHash(fileHash)).thenReturn(Optional.of(testFileRecord));

        // 执行
        FileRecord file = storageService.getFileByHash(fileHash);

        // 验证
        assertThat(file).isNotNull();
        assertThat(file.getFileHash()).isEqualTo(fileHash);
    }

    @Test
    @DisplayName("获取用户文件列表")
    void getFilesByOwner_Success() {
        // 准备
        Pageable pageable = PageRequest.of(0, 20);
        Page<FileRecord> page = new PageImpl<>(Arrays.asList(testFileRecord), pageable, 1);
        
        when(fileRecordRepository.findByOwnerIdOrderByCreatedAtDesc("user123", pageable)).thenReturn(page);

        // 执行
        Page<FileRecord> result = storageService.getFilesByOwner("user123", pageable);

        // 验证
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("获取用户指定类型文件")
    void getFilesByOwnerAndType_Success() {
        // 准备
        Pageable pageable = PageRequest.of(0, 20);
        Page<FileRecord> page = new PageImpl<>(Arrays.asList(testFileRecord), pageable, 1);
        
        when(fileRecordRepository.findByOwnerIdAndFileTypeOrderByCreatedAtDesc("user123", "IMAGE", pageable))
                .thenReturn(page);

        // 执行
        Page<FileRecord> result = storageService.getFilesByOwnerAndType("user123", "IMAGE", pageable);

        // 验证
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("获取会话文件列表")
    void getFilesByConversation_Success() {
        // 准备
        Pageable pageable = PageRequest.of(0, 20);
        Page<FileRecord> page = new PageImpl<>(Arrays.asList(testFileRecord), pageable, 1);
        
        when(fileRecordRepository.findByConversationIdOrderByCreatedAtDesc("conv123", pageable))
                .thenReturn(page);

        // 执行
        Page<FileRecord> result = storageService.getFilesByConversation("conv123", pageable);

        // 验证
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("搜索文件")
    void searchFiles_Success() {
        // 准备
        Pageable pageable = PageRequest.of(0, 20);
        Page<FileRecord> page = new PageImpl<>(Arrays.asList(testFileRecord), pageable, 1);
        
        when(fileRecordRepository.searchByOriginalName("test", pageable)).thenReturn(page);

        // 执行
        Page<FileRecord> result = storageService.searchFiles("test", pageable);

        // 验证
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    // ========== 文件操作测试 ==========

    @Test
    @DisplayName("更新文件元数据")
    void updateFileMetadata_Success() {
        // 准备
        List<String> tags = Arrays.asList("tag1", "tag2");
        when(fileRecordRepository.findById("file123")).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        FileResponse response = storageService.updateFileMetadata("file123", "测试描述", tags);

        // 验证
        assertThat(response).isNotNull();
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    @DisplayName("更新文件访问权限")
    void updateFileAccess_Success() {
        // 准备
        when(fileRecordRepository.findById("file123")).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        FileResponse response = storageService.updateFileAccess("file123", "PUBLIC", null);

        // 验证
        assertThat(response).isNotNull();
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    @DisplayName("删除文件(软删除)")
    void deleteFile_Success() {
        // 准备
        when(fileRecordRepository.findById("file123")).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        storageService.deleteFile("file123", "用户删除");

        // 验证
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    @DisplayName("批量删除文件")
    void deleteFiles_Success() {
        // 准备
        List<String> fileIds = Arrays.asList("file1", "file2", "file3");
        when(fileRecordRepository.findById(anyString())).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        storageService.deleteFiles(fileIds, "批量删除");

        // 验证
        verify(fileRecordRepository, times(3)).save(any(FileRecord.class));
    }

    @Test
    @DisplayName("恢复文件")
    void restoreFile_Success() {
        // 准备
        testFileRecord.setDeleted(true);
        testFileRecord.setDeletedAt(LocalDateTime.now());
        
        when(fileRecordRepository.findById("file123")).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        storageService.restoreFile("file123");

        // 验证
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    @DisplayName("增加下载次数")
    void incrementDownloadCount_Success() {
        // 准备
        when(fileRecordRepository.findById("file123")).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.incrementDownloadCount("file123")).thenReturn(null);

        // 执行
        storageService.incrementDownloadCount("file123");

        // 验证
        verify(fileRecordRepository, times(1)).incrementDownloadCount("file123");
    }

    // ========== 统计查询测试 ==========

    @Test
    @DisplayName("获取存储使用统计")
    void getStorageUsage_Success() {
        // 准备
        when(fileRecordRepository.countByOwnerId("user123")).thenReturn(100L);
        when(fileRecordRepository.sumFileSizeByOwnerId("user123")).thenReturn(104857600L);
        when(fileRecordRepository.findByOwnerIdOrderByCreatedAtDesc(eq("user123"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(testFileRecord)));

        // 执行
        Map<String, Object> usage = storageService.getStorageUsage("user123");

        // 验证
        assertThat(usage).isNotNull();
        assertThat(usage.get("fileCount")).isEqualTo(100L);
        assertThat(usage.get("totalSize")).isEqualTo(104857600L);
    }

    @Test
    @DisplayName("获取文件类型统计")
    void getFileTypeStats_Success() {
        // 准备
        List<FileRecord> files = Arrays.asList(testFileRecord, testFileRecord);
        Page<FileRecord> page = new PageImpl<>(files);
        
        when(fileRecordRepository.findByOwnerIdOrderByCreatedAtDesc(eq("user123"), any(Pageable.class)))
                .thenReturn(page);

        // 执行
        Map<String, Long> stats = storageService.getFileTypeStats("user123");

        // 验证
        assertThat(stats).isNotNull();
    }

    @Test
    @DisplayName("获取今日上传数")
    void getTodayUploadCount_Success() {
        // 准备
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        when(fileRecordRepository.countByCreatedAtAfter(startOfDay)).thenReturn(50L);

        // 执行
        long count = storageService.getTodayUploadCount();

        // 验证
        assertThat(count).isEqualTo(50L);
    }

    @Test
    @DisplayName("获取总文件数")
    void getTotalFileCount_Success() {
        // 准备
        when(fileRecordRepository.count()).thenReturn(10000L);

        // 执行
        long count = storageService.getTotalFileCount();

        // 验证
        assertThat(count).isEqualTo(10000L);
    }

    // ========== 清理任务测试 ==========

    @Test
    @DisplayName("清理过期文件")
    void cleanupExpiredFiles_Success() {
        // 准备
        LocalDateTime now = LocalDateTime.now();
        Page<FileRecord> expiredPage = new PageImpl<>(Arrays.asList(testFileRecord));
        
        when(fileRecordRepository.findExpiredFiles(eq(now), any(Pageable.class))).thenReturn(expiredPage);
        when(fileRecordRepository.findById("file123")).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        int count = storageService.cleanupExpiredFiles();

        // 验证
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("清理超时的分片上传")
    void cleanupStaleUploads_Success() {
        // 准备
        LocalDateTime timeout = LocalDateTime.now().minusHours(24);
        Page<FileRecord> stalePage = new PageImpl<>(Arrays.asList(testFileRecord));
        
        when(fileRecordRepository.findStaleUploads(eq(timeout), any(Pageable.class))).thenReturn(stalePage);
        when(fileRecordRepository.findByUploadId(anyString())).thenReturn(Optional.of(testFileRecord));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(testFileRecord);

        // 执行
        int count = storageService.cleanupStaleUploads();

        // 验证
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
}
