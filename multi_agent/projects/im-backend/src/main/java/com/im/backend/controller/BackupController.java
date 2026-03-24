package com.im.backend.controller;

import com.im.backend.dto.BackupRequest;
import com.im.backend.dto.BackupResponse;
import com.im.backend.service.BackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 备份管理控制器
 */
@RestController
@RequestMapping("/api/backup")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    // ==================== 策略管理 ====================

    @GetMapping("/strategies")
    public ResponseEntity<List<BackupResponse>> getAllStrategies() {
        return ResponseEntity.ok(backupService.getAllStrategies());
    }

    @GetMapping("/strategies/{id}")
    public ResponseEntity<BackupResponse> getStrategy(@PathVariable Long id) {
        return ResponseEntity.ok(backupService.getStrategy(id));
    }

    @PostMapping("/strategies")
    public ResponseEntity<BackupResponse> createStrategy(@RequestBody BackupRequest request) {
        return ResponseEntity.ok(backupService.createStrategy(request));
    }

    @PutMapping("/strategies/{id}")
    public ResponseEntity<BackupResponse> updateStrategy(@PathVariable Long id,
                                                          @RequestBody BackupRequest request) {
        return ResponseEntity.ok(backupService.updateStrategy(id, request));
    }

    @DeleteMapping("/strategies/{id}")
    public ResponseEntity<Void> deleteStrategy(@PathVariable Long id) {
        backupService.deleteStrategy(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 备份操作 ====================

    @PostMapping("/strategies/{id}/trigger")
    public ResponseEntity<BackupResponse> triggerBackup(@PathVariable Long id) {
        return ResponseEntity.ok(backupService.triggerBackup(id));
    }

    @GetMapping("/records")
    public ResponseEntity<List<BackupResponse>> getBackupRecords(
            @RequestParam(required = false) Long strategyId) {
        return ResponseEntity.ok(backupService.getBackupRecords(strategyId));
    }

    @GetMapping("/records/{id}")
    public ResponseEntity<BackupResponse> getBackupRecord(@PathVariable Long id) {
        return ResponseEntity.ok(backupService.getBackupRecord(id));
    }

    @DeleteMapping("/records/{id}")
    public ResponseEntity<Void> deleteBackupRecord(@PathVariable Long id) {
        backupService.deleteBackupRecord(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 统计信息 ====================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getBackupStats() {
        return ResponseEntity.ok(backupService.getBackupStats());
    }
}
