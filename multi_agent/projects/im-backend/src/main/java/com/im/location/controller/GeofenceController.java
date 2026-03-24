package com.im.location.controller;

import com.im.location.entity.GeofenceEntity;
import com.im.location.service.GeofenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/geofence")
public class GeofenceController {
    
    @Autowired
    private GeofenceService geofenceService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createGeofence(@RequestBody GeofenceEntity geofence) {
        try {
            GeofenceEntity result = geofenceService.createGeofence(geofence);
            return ResponseEntity.ok(Map.of("success", true, "data", result, "message", "Geofence created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateGeofence(
            @PathVariable UUID id,
            @RequestBody GeofenceEntity updates) {
        try {
            GeofenceEntity result = geofenceService.updateGeofence(id, updates);
            if (result != null) {
                return ResponseEntity.ok(Map.of("success", true, "data", result));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Geofence not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteGeofence(@PathVariable UUID id) {
        try {
            geofenceService.deleteGeofence(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Geofence deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserGeofences(@PathVariable UUID userId) {
        try {
            List<GeofenceEntity> geofences = geofenceService.getUserGeofences(userId);
            return ResponseEntity.ok(Map.of("success", true, "data", geofences));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<Map<String, Object>> getActiveGeofences(@PathVariable UUID userId) {
        try {
            List<GeofenceEntity> geofences = geofenceService.getActiveGeofences(userId);
            return ResponseEntity.ok(Map.of("success", true, "data", geofences));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGeofence(@PathVariable UUID id) {
        try {
            return geofenceService.getGeofenceById(id)
                    .map(g -> ResponseEntity.ok(Map.of("success", true, "data", g)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("success", false, "message", "Geofence not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateGeofence(@PathVariable UUID id) {
        try {
            geofenceService.deactivateGeofence(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Geofence deactivated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<Map<String, Object>> activateGeofence(@PathVariable UUID id) {
        try {
            geofenceService.activateGeofence(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Geofence activated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/trigger")
    public ResponseEntity<Map<String, Object>> triggerGeofence(@PathVariable UUID id) {
        try {
            geofenceService.triggerGeofence(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Geofence triggered"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> countGeofences(@PathVariable UUID userId) {
        try {
            long count = geofenceService.countActiveGeofences(userId);
            return ResponseEntity.ok(Map.of("success", true, "data", Map.of("count", count)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> deleteAllUserGeofences(@PathVariable UUID userId) {
        try {
            geofenceService.deleteAllUserGeofences(userId);
            return ResponseEntity.ok(Map.of("success", true, "message", "All geofences deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}