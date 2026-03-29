package com.im.local.scheduler.controller;

import com.im.local.scheduler.dto.*;
import com.im.local.scheduler.service.IGeofenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 围栏管理控制器
 */
@RestController
@RequestMapping("/api/v1/scheduler/geofence")
@RequiredArgsConstructor
public class GeofenceController {
    
    private final IGeofenceService geofenceService;
    
    @PostMapping("/create")
    public ResponseEntity<GeofenceResponse> createGeofence(@RequestBody CreateGeofenceRequest request) {
        return ResponseEntity.ok(geofenceService.createGeofence(request));
    }
    
    @GetMapping("/{geofenceId}")
    public ResponseEntity<GeofenceResponse> getGeofence(@PathVariable Long geofenceId) {
        return ResponseEntity.ok(geofenceService.getGeofenceById(geofenceId));
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<GeofenceResponse>> listAllGeofences() {
        return ResponseEntity.ok(geofenceService.listAllGeofences());
    }
    
    @GetMapping("/city/{cityCode}")
    public ResponseEntity<List<GeofenceResponse>> listByCity(@PathVariable String cityCode) {
        return ResponseEntity.ok(geofenceService.listGeofencesByCity(cityCode));
    }
    
    @PostMapping("/{geofenceId}/update-boundary")
    public ResponseEntity<Boolean> updateBoundary(@PathVariable Long geofenceId) {
        return ResponseEntity.ok(geofenceService.updateDynamicBoundary(geofenceId));
    }
    
    @PostMapping("/batch-update-boundaries")
    public ResponseEntity<Void> batchUpdateBoundaries() {
        geofenceService.batchUpdateDynamicBoundaries();
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{geofenceId}/heatmap")
    public ResponseEntity<GeofenceHeatmapResponse> getHeatmap(@PathVariable Long geofenceId) {
        return ResponseEntity.ok(geofenceService.getHeatmapData(geofenceId));
    }
    
    @GetMapping("/{geofenceId}/saturation/check")
    public ResponseEntity<Boolean> checkSaturation(@PathVariable Long geofenceId) {
        return ResponseEntity.ok(geofenceService.checkSaturation(geofenceId));
    }
    
    @DeleteMapping("/{geofenceId}")
    public ResponseEntity<Boolean> deleteGeofence(@PathVariable Long geofenceId) {
        return ResponseEntity.ok(geofenceService.deleteGeofence(geofenceId));
    }
    
    @GetMapping("/location/find")
    public ResponseEntity<List<GeofenceResponse>> findByLocation(
            @RequestParam Double lng,
            @RequestParam Double lat) {
        return ResponseEntity.ok(geofenceService.findGeofencesByLocation(lng, lat));
    }
}
