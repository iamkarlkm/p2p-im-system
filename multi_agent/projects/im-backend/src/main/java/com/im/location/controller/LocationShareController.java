package com.im.location.controller;

import com.im.location.entity.LocationShareEntity;
import com.im.location.service.LocationShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/location")
public class LocationShareController {
    
    @Autowired
    private LocationShareService locationShareService;
    
    @PostMapping("/share/start")
    public ResponseEntity<Map<String, Object>> startShare(@RequestBody LocationShareEntity share) {
        try {
            LocationShareEntity result = locationShareService.startLocationShare(share);
            return ResponseEntity.ok(Map.of("success", true, "data", result, "message", "Location sharing started"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PutMapping("/share/{id}/update")
    public ResponseEntity<Map<String, Object>> updateLocation(
            @PathVariable UUID id,
            @RequestBody Map<String, Double> location) {
        try {
            LocationShareEntity result = locationShareService.updateLocation(
                    id, location.get("latitude"), location.get("longitude"));
            return ResponseEntity.ok(Map.of("success", true, "data", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PostMapping("/share/{id}/stop")
    public ResponseEntity<Map<String, Object>> stopShare(@PathVariable UUID id) {
        try {
            locationShareService.stopLocationShare(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Location sharing stopped"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/share/user/{userId}/active")
    public ResponseEntity<Map<String, Object>> getActiveShares(@PathVariable UUID userId) {
        try {
            List<LocationShareEntity> shares = locationShareService.getActiveSharesByUser(userId);
            return ResponseEntity.ok(Map.of("success", true, "data", shares));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/share/recipient/{recipientId}/active")
    public ResponseEntity<Map<String, Object>> getSharesForRecipient(@PathVariable UUID recipientId) {
        try {
            List<LocationShareEntity> shares = locationShareService.getActiveSharesForRecipient(recipientId);
            return ResponseEntity.ok(Map.of("success", true, "data", shares));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/share/{id}")
    public ResponseEntity<Map<String, Object>> getShare(@PathVariable UUID id) {
        try {
            return locationShareService.getShareById(id)
                    .map(share -> ResponseEntity.ok(Map.of("success", true, "data", share)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("success", false, "message", "Share not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PostMapping("/share/user/{userId}/stop-all")
    public ResponseEntity<Map<String, Object>> stopAllShares(@PathVariable UUID userId) {
        try {
            locationShareService.stopAllUserShares(userId);
            return ResponseEntity.ok(Map.of("success", true, "message", "All shares stopped"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/share/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> countShares(@PathVariable UUID userId) {
        try {
            long count = locationShareService.countActiveShares(userId);
            return ResponseEntity.ok(Map.of("success", true, "data", Map.of("count", count)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}