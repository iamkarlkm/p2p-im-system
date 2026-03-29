package com.im.system.controller;

import com.im.system.entity.SensitiveWordEntity;
import com.im.system.service.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 敏感词管理 REST API 控制器
 */
@RestController
@RequestMapping("/api/v1/sensitive-words")
@CrossOrigin(origins = "*")
public class SensitiveWordController {
    
    @Autowired
    private SensitiveWordService sensitiveWordService;
    
    // ==================== CRUD 接口 ====================
    
    /**
     * 创建敏感词
     * POST /api/v1/sensitive-words
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody SensitiveWordEntity entity) {
        try {
            SensitiveWordEntity created = sensitiveWordService.create(entity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", created);
            response.put("message", "创建成功");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("创建失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 批量创建敏感词
     * POST /api/v1/sensitive-words/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchCreate(@RequestBody List<SensitiveWordEntity> entities) {
        try {
            List<SensitiveWordEntity> created = sensitiveWordService.batchCreate(entities);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", created);
            response.put("count", created.size());
            response.put("message", "批量创建成功");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return buildErrorResponse("批量创建失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 导入敏感词（文本）
     * POST /api/v1/sensitive-words/import
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importWords(
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = "default") String category,
            @RequestParam(required = false, defaultValue = "medium") String level) {
        try {
            List<SensitiveWordEntity> created = sensitiveWordService.importWords(text, category, level);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", created);
            response.put("count", created.size());
            response.put("message", "导入成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("导入失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 导入敏感词（文件）
     * POST /api/v1/sensitive-words/import-file
     */
    @PostMapping("/import-file")
    public ResponseEntity<Map<String, Object>> importFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "default") String category,
            @RequestParam(required = false, defaultValue = "medium") String level) {
        try {
            String text = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            
            List<SensitiveWordEntity> created = sensitiveWordService.importWords(text, category, level);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", created);
            response.put("count", created.size());
            response.put("message", "文件导入成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("文件导入失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 更新敏感词
     * PUT /api/v1/sensitive-words/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @RequestBody SensitiveWordEntity entity) {
        try {
            SensitiveWordEntity updated = sensitiveWordService.update(id, entity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updated);
            response.put("message", "更新成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("更新失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 删除敏感词
     * DELETE /api/v1/sensitive-words/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        try {
            boolean deleted = sensitiveWordService.delete(id);
            
            if (!deleted) {
                return buildErrorResponse("敏感词不存在：" + id, HttpStatus.NOT_FOUND);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("删除失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 批量删除
     * DELETE /api/v1/sensitive-words/batch
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDelete(@RequestBody List<Long> ids) {
        try {
            int count = sensitiveWordService.batchDelete(ids);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            response.put("message", "批量删除成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("批量删除失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 启用/禁用敏感词
     * PATCH /api/v1/sensitive-words/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggleEnabled(
            @PathVariable Long id,
            @RequestParam Boolean enabled) {
        try {
            SensitiveWordEntity updated = sensitiveWordService.toggleEnabled(id, enabled);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updated);
            response.put("message", enabled ? "启用成功" : "禁用成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("操作失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 批量启用/禁用
     * PATCH /api/v1/sensitive-words/batch/toggle
     */
    @PatchMapping("/batch/toggle")
    public ResponseEntity<Map<String, Object>> batchToggle(
            @RequestBody List<Long> ids,
            @RequestParam Boolean enabled) {
        try {
            int count = sensitiveWordService.batchToggle(ids, enabled);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            response.put("message", enabled ? "批量启用成功" : "批量禁用成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("批量操作失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ==================== 查询接口 ====================
    
    /**
     * 根据 ID 查询
     * GET /api/v1/sensitive-words/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        try {
            Optional<SensitiveWordEntity> entity = sensitiveWordService.getById(id);
            
            if (entity.isEmpty()) {
                return buildErrorResponse("敏感词不存在：" + id, HttpStatus.NOT_FOUND);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", entity.get());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("查询失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 根据词查询
     * GET /api/v1/sensitive-words/word/{word}
     */
    @GetMapping("/word/{word}")
    public ResponseEntity<Map<String, Object>> getByWord(@PathVariable String word) {
        try {
            Optional<SensitiveWordEntity> entity = sensitiveWordService.getByWord(word);
            
            if (entity.isEmpty()) {
                return buildErrorResponse("敏感词不存在：" + word, HttpStatus.NOT_FOUND);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", entity.get());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("查询失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 分页查询
     * GET /api/v1/sensitive-words?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        try {
            Page<SensitiveWordEntity> results = sensitiveWordService.getAll(page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("查询失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 按启用状态查询
     * GET /api/v1/sensitive-words/enabled?enabled=true&page=0&size=20
     */
    @GetMapping("/enabled")
    public ResponseEntity<Map<String, Object>> getByEnabled(
            @RequestParam Boolean enabled,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        try {
            Page<SensitiveWordEntity> results = sensitiveWordService.getByEnabled(enabled, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("enabled", enabled);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("查询失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 按分类查询
     * GET /api/v1/sensitive-words/category/{category}?page=0&size=20
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getByCategory(
            @PathVariable String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        try {
            Page<SensitiveWordEntity> results = sensitiveWordService.getByCategory(category, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("category", category);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("查询失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 按等级查询
     * GET /api/v1/sensitive-words/level/{level}?page=0&size=20
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<Map<String, Object>> getByLevel(
            @PathVariable String level,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        try {
            Page<SensitiveWordEntity> results = sensitiveWordService.getByLevel(level, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("level", level);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("查询失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 搜索敏感词
     * GET /api/v1/sensitive-words/search?keyword=xxx&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        try {
            Page<SensitiveWordEntity> results = sensitiveWordService.search(keyword, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("keyword", keyword);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取所有启用的敏感词
     * GET /api/v1/sensitive-words/enabled-list
     */
    @GetMapping("/enabled-list")
    public ResponseEntity<Map<String, Object>> getAllEnabled() {
        try {
            List<SensitiveWordEntity> results = sensitiveWordService.getAllEnabled();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("count", results.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("查询失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ==================== 统计接口 ====================
    
    /**
     * 统计总数
     * GET /api/v1/sensitive-words/stats/count
     */
    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Object>> getCount() {
        try {
            long count = sensitiveWordService.count();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 统计启用/禁用数量
     * GET /api/v1/sensitive-words/stats/status
     */
    @GetMapping("/stats/status")
    public ResponseEntity<Map<String, Object>> getStatusStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("enabled", sensitiveWordService.countEnabled());
            stats.put("disabled", sensitiveWordService.countDisabled());
            stats.put("total", sensitiveWordService.count());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 按分类统计
     * GET /api/v1/sensitive-words/stats/by-category
     */
    @GetMapping("/stats/by-category")
    public ResponseEntity<Map<String, Object>> getStatsByCategory() {
        try {
            Map<String, Long> stats = sensitiveWordService.countByCategory();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 按等级统计
     * GET /api/v1/sensitive-words/stats/by-level
     */
    @GetMapping("/stats/by-level")
    public ResponseEntity<Map<String, Object>> getStatsByLevel() {
        try {
            Map<String, Long> stats = sensitiveWordService.countByLevel();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ==================== 检测接口 ====================
    
    /**
     * 检测敏感词
     * POST /api/v1/sensitive-words/detect
     */
    @PostMapping("/detect")
    public ResponseEntity<Map<String, Object>> detect(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            Map<String, Object> result = sensitiveWordService.detectSensitiveWords(text);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("检测失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 过滤敏感词
     * POST /api/v1/sensitive-words/filter
     */
    @PostMapping("/filter")
    public ResponseEntity<Map<String, Object>> filter(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            String filtered = sensitiveWordService.filterSensitiveWords(text);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", filtered);
            response.put("original", text);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("过滤失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private Map<String, Object> buildPaginationInfo(Page<?> page) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("currentPage", page.getNumber());
        pagination.put("pageSize", page.getSize());
        pagination.put("numberOfElements", page.getNumberOfElements());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        return pagination;
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(response);
    }
}