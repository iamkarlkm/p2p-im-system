package com.im.local.scheduler.controller;

import com.im.local.scheduler.dto.*;
import com.im.local.scheduler.service.IDeliveryStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 骑手管理控制器
 */
@RestController
@RequestMapping("/api/v1/scheduler/staff")
@RequiredArgsConstructor
public class StaffController {
    
    private final IDeliveryStaffService staffService;
    
    @PostMapping("/register")
    public ResponseEntity<StaffResponse> registerStaff(@RequestBody RegisterStaffRequest request) {
        return ResponseEntity.ok(staffService.registerStaff(request));
    }
    
    @GetMapping("/{staffId}")
    public ResponseEntity<StaffResponse> getStaff(@PathVariable Long staffId) {
        return ResponseEntity.ok(staffService.getStaffById(staffId));
    }
    
    @PostMapping("/location/update")
    public ResponseEntity<Boolean> updateLocation(@RequestBody UpdateStaffLocationRequest request) {
        return ResponseEntity.ok(staffService.updateStaffLocation(request));
    }
    
    @PostMapping("/{staffId}/status/{status}")
    public ResponseEntity<Boolean> updateStatus(@PathVariable Long staffId, @PathVariable Integer status) {
        return ResponseEntity.ok(staffService.updateStaffStatus(staffId, status));
    }
    
    @GetMapping("/geofence/{geofenceId}/idle")
    public ResponseEntity<List<StaffResponse>> getIdleStaffInGeofence(@PathVariable Long geofenceId) {
        return ResponseEntity.ok(staffService.findIdleStaffInGeofence(geofenceId));
    }
    
    @GetMapping("/nearby/available")
    public ResponseEntity<List<StaffResponse>> getNearbyAvailableStaff(
            @RequestParam Double lng,
            @RequestParam Double lat,
            @RequestParam(defaultValue = "5000") Integer radius) {
        return ResponseEntity.ok(staffService.findAvailableStaffNearby(lng, lat, radius));
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<StaffResponse>> listAllStaff() {
        return ResponseEntity.ok(staffService.listAllStaff());
    }
}
