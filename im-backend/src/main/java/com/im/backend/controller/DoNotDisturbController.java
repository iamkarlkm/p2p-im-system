package com.im.backend.controller;

import com.im.backend.dto.DoNotDisturbPeriodDTO;
import com.im.backend.dto.ApiResponse;
import com.im.backend.service.DoNotDisturbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/do-not-disturb")
public class DoNotDisturbController {

    @Autowired
    private DoNotDisturbService doNotDisturbService;

    @GetMapping("/periods")
    public ResponseEntity<ApiResponse<List<DoNotDisturbPeriodDTO>>> getPeriods(
            @RequestAttribute("userId") String userId) {
        List<DoNotDisturbPeriodDTO> periods = doNotDisturbService.getUserPeriods(userId);
        return ResponseEntity.ok(ApiResponse.success(periods));
    }

    @PostMapping("/periods")
    public ResponseEntity<ApiResponse<DoNotDisturbPeriodDTO>> createPeriod(
            @RequestAttribute("userId") String userId,
            @RequestBody DoNotDisturbPeriodDTO dto) {
        DoNotDisturbPeriodDTO created = doNotDisturbService.createPeriod(userId, dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @PutMapping("/periods/{periodId}")
    public ResponseEntity<ApiResponse<DoNotDisturbPeriodDTO>> updatePeriod(
            @RequestAttribute("userId") String userId,
            @PathVariable String periodId,
            @RequestBody DoNotDisturbPeriodDTO dto) {
        DoNotDisturbPeriodDTO updated = doNotDisturbService.updatePeriod(userId, periodId, dto);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/periods/{periodId}")
    public ResponseEntity<ApiResponse<Void>> deletePeriod(
            @RequestAttribute("userId") String userId,
            @PathVariable String periodId) {
        doNotDisturbService.deletePeriod(userId, periodId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/periods/{periodId}/toggle")
    public ResponseEntity<ApiResponse<DoNotDisturbPeriodDTO>> togglePeriod(
            @RequestAttribute("userId") String userId,
            @PathVariable String periodId,
            @RequestParam Boolean enabled) {
        DoNotDisturbPeriodDTO updated = doNotDisturbService.togglePeriod(userId, periodId, enabled);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus(
            @RequestAttribute("userId") String userId) {
        Map<String, Object> status = new HashMap<>();
        status.put("isInDoNotDisturbMode", doNotDisturbService.isInDoNotDisturbMode(userId));
        status.put("shouldAllowCalls", doNotDisturbService.shouldAllowCalls(userId));
        status.put("shouldAllowMentions", doNotDisturbService.shouldAllowMentions(userId));
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    @PatchMapping("/settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateSettings(
            @RequestAttribute("userId") String userId,
            @RequestBody Map<String, Object> settings) {
        return ResponseEntity.ok(ApiResponse.success(settings));
    }
}
