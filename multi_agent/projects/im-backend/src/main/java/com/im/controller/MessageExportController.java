package com.im.controller;

import com.im.entity.MessageExportEntity;
import com.im.service.MessageExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息批量导出 REST API 控制器
 */
@RestController
@RequestMapping("/api/v1/exports")
public class MessageExportController {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageExportController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private MessageExportService messageExportService;
    
    /**
     * 创建导出任务
     */
    @PostMapping("/create")
    public ResponseEntity<?> createExport(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> request) {
        
        try {
            // 解析请求参数
            String exportName = (String) request.get("exportName");
            String description = (String) request.get("description");
            String formatStr = (String) request.get("format");
            String sessionId = (String) request.get("sessionId");
            String sessionTypeStr = (String) request.get("sessionType");
            String startTimeStr = (String) request.get("startTime");
            String endTimeStr = (String) request.get("endTime");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> exportOptions = (Map<String, Object>) request.get("exportOptions");
            
            // 验证必填参数
            if (exportName == null || exportName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("导出名称不能为空"));
            }
            
            if (formatStr == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("导出格式不能为空"));
            }
            
            // 解析枚举值
            MessageExportEntity.ExportFormat exportFormat;
            try {
                exportFormat = MessageExportEntity.ExportFormat.valueOf(formatStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(createErrorResponse("不支持的导出格式: " + formatStr));
            }
            
            MessageExportEntity.SessionType sessionType = null;
            if (sessionTypeStr != null) {
                try {
                    sessionType = MessageExportEntity.SessionType.valueOf(sessionTypeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(createErrorResponse("不支持的会话类型: " + sessionTypeStr));
                }
            }
            
            // 解析时间
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;
            try {
                if (startTimeStr != null && !startTimeStr.isEmpty()) {
                    startTime = LocalDateTime.parse(startTimeStr);
                }
                if (endTimeStr != null && !endTimeStr.isEmpty()) {
                    endTime = LocalDateTime.parse(endTimeStr);
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(createErrorResponse("时间格式不正确，请使用 ISO 8601 格式"));
            }
            
            // 创建导出任务
            MessageExportEntity export = messageExportService.createExportTask(
                userId, exportName, description, exportFormat,
                sessionId, sessionType, startTime, endTime, exportOptions
            );
            
            logger.info("用户 {} 创建导出任务: id={}, name={}, format={}", 
                userId, export.getId(), exportName, exportFormat);
            
            return ResponseEntity.ok(createSuccessResponse("导出任务创建成功", export));
            
        } catch (Exception e) {
            logger.error("创建导出任务失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("创建导出任务失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取导出任务列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> listExports(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "format", required = false) String format,
            @RequestParam(value = "sessionId", required = false) String sessionId) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));
            Page<MessageExportEntity> exports;
            
            if (status != null) {
                try {
                    MessageExportEntity.ExportStatus exportStatus = MessageExportEntity.ExportStatus.valueOf(status.toUpperCase());
                    exports = messageExportService.getUserExportsByStatus(userId, exportStatus, pageable);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(createErrorResponse("无效的状态参数"));
                }
            } else if (sessionId != null) {
                // 这里简化处理，实际应该从服务层获取
                List<MessageExportEntity> exportList = messageExportService.getUserExports(userId, pageable).getContent()
                    .stream()
                    .filter(e -> sessionId.equals(e.getSessionId()))
                    .collect(java.util.stream.Collectors.toList());
                exports = new org.springframework.data.domain.PageImpl<>(exportList, pageable, exportList.size());
            } else {
                exports = messageExportService.getUserExports(userId, pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", exports.getContent());
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", exports.getTotalPages());
            response.put("totalElements", exports.getTotalElements());
            response.put("first", exports.isFirst());
            response.put("last", exports.isLast());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取导出列表失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("获取导出列表失败"));
        }
    }
    
    /**
     * 获取导出任务详情
     */
    @GetMapping("/{exportId}")
    public ResponseEntity<?> getExport(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long exportId) {
        
        try {
            MessageExportEntity export = messageExportService.getExport(exportId);
            
            // 权限检查：只能查看自己的导出任务
            if (!export.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("无权访问该导出任务"));
            }
            
            return ResponseEntity.ok(createSuccessResponse("获取成功", export));
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("导出任务不存在"));
            }
            logger.error("获取导出详情失败: userId={}, exportId={}", userId, exportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("获取导出详情失败"));
        }
    }
    
    /**
     * 下载导出文件
     */
    @GetMapping("/{exportId}/download")
    public ResponseEntity<Resource> downloadExport(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long exportId,
            HttpServletRequest request) {
        
        try {
            MessageExportEntity export = messageExportService.getExport(exportId);
            
            // 权限检查
            if (!export.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // 状态检查
            if (export.getStatus() != MessageExportEntity.ExportStatus.COMPLETED) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            
            File file = messageExportService.getExportFile(exportId);
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 确定内容类型
            String contentType = determineContentType(export.getExportFormat());
            
            // 设置响应头
            String filename = generateDownloadFilename(export);
            String headerValue = "attachment; filename=\"" + filename + "\"";
            
            logger.info("用户 {} 下载导出文件: exportId={}, filename={}", userId, exportId, filename);
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
            
        } catch (RuntimeException e) {
            logger.error("下载导出文件失败: userId={}, exportId={}", userId, exportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 取消导出任务
     */
    @PostMapping("/{exportId}/cancel")
    public ResponseEntity<?> cancelExport(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long exportId) {
        
        try {
            MessageExportEntity export = messageExportService.getExport(exportId);
            
            // 权限检查
            if (!export.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("无权操作该导出任务"));
            }
            
            // 状态检查：只能取消待处理或处理中的任务
            if (export.getStatus() != MessageExportEntity.ExportStatus.PENDING &&
                export.getStatus() != MessageExportEntity.ExportStatus.PROCESSING) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("只能取消待处理或处理中的任务"));
            }
            
            boolean cancelled = messageExportService.cancelExport(exportId);
            if (cancelled) {
                logger.info("用户 {} 取消导出任务: exportId={}", userId, exportId);
                return ResponseEntity.ok(createSuccessResponse("取消成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("取消失败，任务可能已无法取消"));
            }
            
        } catch (RuntimeException e) {
            logger.error("取消导出任务失败: userId={}, exportId={}", userId, exportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("取消导出任务失败"));
        }
    }
    
    /**
     * 删除导出任务
     */
    @DeleteMapping("/{exportId}")
    public ResponseEntity<?> deleteExport(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long exportId) {
        
        try {
            MessageExportEntity export = messageExportService.getExport(exportId);
            
            // 权限检查
            if (!export.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("无权删除该导出任务"));
            }
            
            boolean deleted = messageExportService.deleteExport(exportId);
            if (deleted) {
                logger.info("用户 {} 删除导出任务: exportId={}", userId, exportId);
                return ResponseEntity.ok(createSuccessResponse("删除成功"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("导出任务不存在"));
            }
            
        } catch (RuntimeException e) {
            logger.error("删除导出任务失败: userId={}, exportId={}", userId, exportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("删除导出任务失败"));
        }
    }
    
    /**
     * 批量导出多个会话
     */
    @PostMapping("/batch")
    public ResponseEntity<?> batchExport(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> request) {
        
        try {
            @SuppressWarnings("unchecked")
            List<String> sessionIds = (List<String>) request.get("sessionIds");
            String formatStr = (String) request.get("format");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> exportOptions = (Map<String, Object>) request.get("exportOptions");
            
            // 验证参数
            if (sessionIds == null || sessionIds.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("会话ID列表不能为空"));
            }
            
            if (formatStr == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("导出格式不能为空"));
            }
            
            // 解析格式
            MessageExportEntity.ExportFormat exportFormat;
            try {
                exportFormat = MessageExportEntity.ExportFormat.valueOf(formatStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(createErrorResponse("不支持的导出格式"));
            }
            
            // 限制批量导出数量
            if (sessionIds.size() > 10) {
                return ResponseEntity.badRequest().body(createErrorResponse("一次最多导出10个会话"));
            }
            
            // 执行批量导出
            List<MessageExportEntity> exports = messageExportService.batchExportSessions(
                userId, sessionIds, exportFormat, exportOptions
            );
            
            logger.info("用户 {} 批量导出会话: count={}", userId, sessionIds.size());
            
            return ResponseEntity.ok(createSuccessResponse("批量导出任务创建成功", exports));
            
        } catch (Exception e) {
            logger.error("批量导出失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("批量导出失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取导出统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("X-User-Id") Long userId) {
        
        try {
            Map<String, Object> stats = messageExportService.getExportStats(userId);
            return ResponseEntity.ok(createSuccessResponse("获取统计成功", stats));
            
        } catch (Exception e) {
            logger.error("获取导出统计失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("获取导出统计失败"));
        }
    }
    
    /**
     * 清理过期导出记录
     */
    @PostMapping("/cleanup")
    public ResponseEntity<?> cleanupExports(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(value = "days", defaultValue = "30") int days) {
        
        try {
            // 只允许管理员清理（简化处理，实际应该检查用户角色）
            // 这里假设只有用户ID为1的是管理员
            if (!userId.equals(1L)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("无权执行清理操作"));
            }
            
            int cleaned = messageExportService.cleanupOldExports(days);
            
            logger.info("清理过期导出记录: userId={}, days={}, cleaned={}", userId, days, cleaned);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "清理完成");
            response.put("cleanedCount", cleaned);
            response.put("daysToKeep", days);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("清理导出记录失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("清理失败"));
        }
    }
    
    /**
     * 验证导出选项
     */
    @PostMapping("/validate-options")
    public ResponseEntity<?> validateOptions(@RequestBody Map<String, Object> options) {
        
        try {
            Map<String, Object> validated = messageExportService.validateExportOptions(options);
            return ResponseEntity.ok(createSuccessResponse("验证成功", validated));
            
        } catch (Exception e) {
            logger.error("验证导出选项失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("验证失败"));
        }
    }
    
    /**
     * 获取支持的导出格式
     */
    @GetMapping("/formats")
    public ResponseEntity<?> getSupportedFormats() {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        
        Map<String, String> formats = new HashMap<>();
        formats.put("JSON", "JSON格式，适合程序处理");
        formats.put("CSV", "CSV格式，适合Excel导入");
        formats.put("TXT", "文本格式，适合阅读");
        formats.put("PDF", "PDF格式，适合打印和分享");
        
        response.put("formats", formats);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 上传导出模板（示例）
     */
    @PostMapping("/upload-template")
    public ResponseEntity<?> uploadTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("templateType") String templateType) {
        
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("上传文件不能为空"));
            }
            
            // 检查文件大小（限制为5MB）
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return ResponseEntity.badRequest().body(createErrorResponse("文件大小不能超过5MB"));
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (!"application/json".equals(contentType) && 
                !"text/csv".equals(contentType) && 
                !"text/plain".equals(contentType)) {
                return ResponseEntity.badRequest().body(createErrorResponse("只支持JSON、CSV和TXT格式"));
            }
            
            // 这里应该将文件保存到服务器并记录
            // 简化处理，只返回成功响应
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "模板上传成功");
            response.put("filename", file.getOriginalFilename());
            response.put("size", file.getSize());
            response.put("contentType", contentType);
            response.put("templateType", templateType);
            
            logger.info("上传导出模板: filename={}, size={}, type={}", 
                file.getOriginalFilename(), file.getSize(), templateType);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("上传导出模板失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("上传失败: " + e.getMessage()));
        }
    }
    
    /**
     * 生成下载文件名
     */
    private String generateDownloadFilename(MessageExportEntity export) {
        String name = export.getExportName().replaceAll("[^a-zA-Z0-9_-]", "_");
        String timestamp = export.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = export.getExportFormat().name().toLowerCase();
        
        return String.format("%s_%s.%s", name, timestamp, extension);
    }
    
    /**
     * 确定内容类型
     */
    private String determineContentType(MessageExportEntity.ExportFormat format) {
        switch (format) {
            case JSON:
                return "application/json";
            case CSV:
                return "text/csv";
            case TXT:
                return "text/plain";
            case PDF:
                return "application/pdf";
            default:
                return "application/octet-stream";
        }
    }
    
    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
        return response;
    }
    
    /**
     * 创建成功响应（带数据）
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = createSuccessResponse(message);
        response.put("data", data);
        return response;
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
        return response;
    }
}