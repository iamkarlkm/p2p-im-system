package com.im.service.admin.service;

import com.im.service.admin.entity.AdminLog;
import com.im.service.admin.repository.AdminLogRepository;
import com.im.service.admin.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 管理服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AdminService 单元测试")
class AdminServiceTest {

    @Mock
    private AdminLogRepository adminLogRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private AdminLog testLog;

    @BeforeEach
    void setUp() {
        // 创建测试日志
        testLog = AdminLog.builder()
                .id(1L)
                .adminId(100L)
                .adminUsername("admin")
                .adminRealName("管理员")
                .operationType(AdminLog.OperationType.CREATE.name())
                .module(AdminLog.Module.USER.name())
                .targetType("User")
                .targetId("user-123")
                .description("创建用户")
                .requestMethod("POST")
                .requestUrl("/api/users")
                .result(AdminLog.Result.SUCCESS.name())
                .ipAddress("192.168.1.1")
                .duration(100L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ==================== 日志记录测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("logOperation_Success - 记录操作日志成功")
    void logOperation_Success() {
        // Arrange
        when(adminLogRepository.save(any(AdminLog.class))).thenReturn(testLog);

        // Act
        AdminLog result = adminService.logOperation(testLog);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAdminId()).isEqualTo(100L);
        verify(adminLogRepository, times(1)).save(any(AdminLog.class));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("logOperation_ConvenienceMethod - 使用便捷方法记录日志")
    void logOperation_ConvenienceMethod() {
        // Arrange
        when(adminLogRepository.save(any(AdminLog.class))).thenReturn(testLog);

        // Act
        AdminLog result = adminService.logOperation(
                100L, "admin", "CREATE", "USER", "创建用户");

        // Assert
        assertThat(result).isNotNull();
        verify(adminLogRepository, times(1)).save(any(AdminLog.class));
    }

    // ==================== 日志查询测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("getLogById_Success - 根据ID获取日志成功")
    void getLogById_Success() {
        // Arrange
        Long logId = 1L;
        when(adminLogRepository.findById(logId)).thenReturn(Optional.of(testLog));

        // Act
        Optional<AdminLog> result = adminService.getLogById(logId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(logId);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getLogById_NotFound - 日志不存在")
    void getLogById_NotFound() {
        // Arrange
        Long logId = 999L;
        when(adminLogRepository.findById(logId)).thenReturn(Optional.empty());

        // Act
        Optional<AdminLog> result = adminService.getLogById(logId);

        // Assert
        assertThat(result).isEmpty();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getAdminLogs_Success - 获取管理员日志成功")
    void getAdminLogs_Success() {
        // Arrange
        Long adminId = 100L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<AdminLog> page = new PageImpl<>(Arrays.asList(testLog));
        when(adminLogRepository.findByAdminId(adminId, pageable)).thenReturn(page);

        // Act
        Page<AdminLog> result = adminService.getAdminLogs(adminId, pageable);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getRecentLogs_Success - 获取最近日志成功")
    void getRecentLogs_Success() {
        // Arrange
        int limit = 10;
        when(adminLogRepository.findRecentOperations(any(Pageable.class)))
                .thenReturn(Arrays.asList(testLog));

        // Act
        List<AdminLog> result = adminService.getRecentLogs(limit);

        // Assert
        assertThat(result).hasSize(1);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getRecentLogins_Success - 获取最近登录成功")
    void getRecentLogins_Success() {
        // Arrange
        int limit = 10;
        when(adminLogRepository.findRecentLogins(any(Pageable.class)))
                .thenReturn(Arrays.asList(testLog));

        // Act
        List<AdminLog> result = adminService.getRecentLogins(limit);

        // Assert
        assertThat(result).hasSize(1);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getFailedOperations_Success - 获取失败操作成功")
    void getFailedOperations_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<AdminLog> logs = Arrays.asList(testLog);
        when(adminLogRepository.findFailedOperations(pageable)).thenReturn(logs);

        // Act
        List<AdminLog> result = adminService.getFailedOperations(pageable);

        // Assert
        assertThat(result).hasSize(1);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getLastLogin_Success - 获取最后登录成功")
    void getLastLogin_Success() {
        // Arrange
        Long adminId = 100L;
        when(adminLogRepository.findLastLogin(adminId)).thenReturn(Optional.of(testLog));

        // Act
        Optional<AdminLog> result = adminService.getLastLogin(adminId);

        // Assert
        assertThat(result).isPresent();
    }

    // ==================== 统计相关测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("getOperationCountByModule_Success - 按模块统计成功")
    void getOperationCountByModule_Success() {
        // Arrange
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<Object[]> mockResult = Arrays.asList(
                new Object[]{"USER", 100L},
                new Object[]{"GROUP", 50L}
        );
        when(adminLogRepository.countByModule(since)).thenReturn(mockResult);

        // Act
        Map<String, Long> result = adminService.getOperationCountByModule(since);

        // Assert
        assertThat(result).containsKey("USER");
        assertThat(result.get("USER")).isEqualTo(100L);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getOperationCountByType_Success - 按类型统计成功")
    void getOperationCountByType_Success() {
        // Arrange
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<Object[]> mockResult = Arrays.asList(
                new Object[]{"CREATE", 80L},
                new Object[]{"UPDATE", 70L}
        );
        when(adminLogRepository.countByOperationType(since)).thenReturn(mockResult);

        // Act
        Map<String, Long> result = adminService.getOperationCountByType(since);

        // Assert
        assertThat(result).containsKey("CREATE");
        assertThat(result.get("CREATE")).isEqualTo(80L);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getOperationResultCount_Success - 按结果统计成功")
    void getOperationResultCount_Success() {
        // Arrange
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<Object[]> mockResult = Arrays.asList(
                new Object[]{"SUCCESS", 150L},
                new Object[]{"FAILURE", 10L}
        );
        when(adminLogRepository.countByResult(since)).thenReturn(mockResult);

        // Act
        Map<String, Long> result = adminService.getOperationResultCount(since);

        // Assert
        assertThat(result).containsKey("SUCCESS");
        assertThat(result.get("SUCCESS")).isEqualTo(150L);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getAverageDuration_Success - 获取平均操作耗时成功")
    void getAverageDuration_Success() {
        // Arrange
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        when(adminLogRepository.getAverageDuration(since)).thenReturn(150.5);

        // Act
        Double result = adminService.getAverageDuration(since);

        // Assert
        assertThat(result).isEqualTo(150.5);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("getAverageDuration_Null - 无数据时返回null")
    void getAverageDuration_Null() {
        // Arrange
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        when(adminLogRepository.getAverageDuration(since)).thenReturn(null);

        // Act
        Double result = adminService.getAverageDuration(since);

        // Assert
        assertThat(result).isNull();
    }

    // ==================== 管理员统计测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("getAdminStatistics_Success - 获取管理员统计成功")
    void getAdminStatistics_Success() {
        // Arrange
        Long adminId = 100L;
        when(adminLogRepository.countByAdminId(adminId)).thenReturn(50L);
        
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        doReturn(Arrays.asList(
                new Object[]{"SUCCESS", 45L},
                new Object[]{"FAILURE", 5L}
        )).when(adminLogRepository).countByResult(since);
        doReturn(Arrays.asList(
                new Object[]{"USER", 30L}
        )).when(adminLogRepository).countByModule(since);
        doReturn(Arrays.asList(
                new Object[]{"CREATE", 25L}
        )).when(adminLogRepository).countByOperationType(since);
        when(adminLogRepository.getAverageDuration(since)).thenReturn(100.0);
        when(adminLogRepository.findByAdminId(eq(adminId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(testLog)));

        // Act
        AdminService.AdminStatistics result = adminService.getAdminStatistics(adminId);

        // Assert
        assertThat(result.getTotalOperations()).isEqualTo(50);
        assertThat(result.getSuccessCount()).isGreaterThanOrEqualTo(0);
    }

    // ==================== 日志清理测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("deleteOldLogs_Success - 删除旧日志成功")
    void deleteOldLogs_Success() {
        // Arrange
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(90);
        when(adminLogRepository.deleteOldLogs(beforeTime)).thenReturn(100);

        // Act
        int result = adminService.deleteOldLogs(beforeTime);

        // Assert
        assertThat(result).isEqualTo(100);
        verify(adminLogRepository, times(1)).deleteOldLogs(beforeTime);
    }

    // ==================== 系统统计测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("getSystemStatistics_Success - 获取系统统计成功")
    void getSystemStatistics_Success() {
        // Arrange
        when(adminLogRepository.count()).thenReturn(1000L);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        when(adminLogRepository.findByTimeRange(todayStart, now))
                .thenReturn(Arrays.asList(testLog));
        
        when(adminLogRepository.countByResultEquals("FAILURE")).thenReturn(50L);
        
        LocalDateTime since = now.minusDays(30);
        @SuppressWarnings("unchecked")
        List<Object[]> moduleStats = (List<Object[]>) (List<?>) Arrays.asList(new Object[]{"USER", 300L});
        when(adminLogRepository.countByModule(since)).thenReturn(moduleStats);
        
        @SuppressWarnings("unchecked")
        List<Object[]> operationStats = (List<Object[]>) (List<?>) Arrays.asList(new Object[]{"CREATE", 200L});
        when(adminLogRepository.countByOperationType(since)).thenReturn(operationStats);
        
        @SuppressWarnings("unchecked")
        List<Object[]> resultStats = (List<Object[]>) (List<?>) Arrays.asList(
                new Object[]{"SUCCESS", 800L},
                new Object[]{"FAILURE", 200L}
        );
        when(adminLogRepository.countByResult(since)).thenReturn(resultStats);
        when(adminLogRepository.getAverageDuration(since)).thenReturn(120.0);
        
        when(adminLogRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(testLog)));
        
        when(adminLogRepository.findRecentOperations(any(Pageable.class)))
                .thenReturn(Arrays.asList(testLog));

        // Act
        AdminService.SystemStatistics result = adminService.getSystemStatistics();

        // Assert
        assertThat(result.getTotalLogs()).isEqualTo(1000);
        assertThat(result.getFailedOperations()).isEqualTo(50);
    }

    // ==================== 复杂查询测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("getLogs_WithConditions - 条件查询日志")
    void getLogs_WithConditions() {
        // Arrange
        Long adminId = 100L;
        String module = "USER";
        String operationType = "CREATE";
        String result = "SUCCESS";
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<AdminLog> page = new PageImpl<>(Arrays.asList(testLog));
        when(adminLogRepository.findByConditions(adminId, module, operationType, 
                result, startTime, endTime, pageable)).thenReturn(page);

        // Act
        Page<AdminLog> resultPage = adminService.getLogs(
                adminId, module, operationType, result, startTime, endTime, pageable);

        // Assert
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
    }

    // ==================== 便捷方法测试 ====================

    @org.junit.jupiter.api.Test
    @DisplayName("AdminLog_isSuccess - 判断操作成功")
    void adminLog_isSuccess() {
        // Arrange
        AdminLog successLog = AdminLog.builder()
                .result(AdminLog.Result.SUCCESS.name())
                .build();
        
        AdminLog failureLog = AdminLog.builder()
                .result(AdminLog.Result.FAILURE.name())
                .build();

        // Act & Assert
        assertThat(successLog.isSuccess()).isTrue();
        assertThat(successLog.isFailure()).isFalse();
        assertThat(failureLog.isSuccess()).isFalse();
        assertThat(failureLog.isFailure()).isTrue();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("AdminLog_markAsSuccessFailure - 设置成功失败状态")
    void adminLog_markAsSuccessFailure() {
        // Arrange
        AdminLog log = AdminLog.builder().build();

        // Act
        log.markAsSuccess();
        
        // Assert
        assertThat(log.isSuccess()).isTrue();
        
        // Act
        log.markAsFailure("Test error");
        
        // Assert
        assertThat(log.isFailure()).isTrue();
        assertThat(log.getErrorMessage()).isEqualTo("Test error");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("AdminLog_isDataModification - 判断是否数据修改操作")
    void adminLog_isDataModification() {
        // Arrange
        AdminLog createLog = AdminLog.builder()
                .operationType(AdminLog.OperationType.CREATE.name())
                .build();
        
        AdminLog queryLog = AdminLog.builder()
                .operationType(AdminLog.OperationType.QUERY.name())
                .build();

        // Act & Assert
        assertThat(createLog.isDataModification()).isTrue();
        assertThat(queryLog.isDataModification()).isFalse();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("AdminLog_isLoginOperation - 判断是否登录操作")
    void adminLog_isLoginOperation() {
        // Arrange
        AdminLog loginLog = AdminLog.builder()
                .operationType(AdminLog.OperationType.LOGIN.name())
                .build();
        
        AdminLog otherLog = AdminLog.builder()
                .operationType(AdminLog.OperationType.CREATE.name())
                .build();

        // Act & Assert
        assertThat(loginLog.isLoginOperation()).isTrue();
        assertThat(otherLog.isLoginOperation()).isFalse();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("AdminLog_markAsPartial - 设置部分成功状态")
    void adminLog_markAsPartial() {
        // Arrange
        AdminLog log = AdminLog.builder().build();

        // Act
        log.markAsPartial("Partial failure reason");

        // Assert
        assertThat(log.getResult()).isEqualTo("PARTIAL");
        assertThat(log.getErrorMessage()).isEqualTo("Partial failure reason");
    }
}
