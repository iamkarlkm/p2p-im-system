package com.im.local.scheduler.controller;

import com.im.local.scheduler.dto.*;
import com.im.local.scheduler.service.ISmartDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能调度控制器
 */
@RestController
@RequestMapping("/api/v1/scheduler/dispatch")
@RequiredArgsConstructor
public class DispatchController {
    
    private final ISmartDispatchService dispatchService;
    
    @PostMapping("/order")
    public ResponseEntity<DispatchResultResponse> dispatchOrder(@RequestBody DispatchOrderRequest request) {
        return ResponseEntity.ok(dispatchService.dispatchOrder(request));
    }
    
    @PostMapping("/batch")
    public ResponseEntity<List<DispatchResultResponse>> batchDispatch(@RequestBody List<DispatchOrderRequest> requests) {
        return ResponseEntity.ok(dispatchService.batchDispatchOrders(requests));
    }
    
    @GetMapping("/geofence/{geofenceId}/aggregate")
    public ResponseEntity<List<Long>> aggregateOrders(@PathVariable Long geofenceId) {
        return ResponseEntity.ok(dispatchService.aggregateOrdersInGeofence(geofenceId));
    }
    
    @PostMapping("/route/plan")
    public ResponseEntity<List<DispatchResultResponse.RouteNode>> planRoute(@RequestBody RoutePlanRequest request) {
        return ResponseEntity.ok(dispatchService.calculateOptimalRoute(request));
    }
    
    @PostMapping("/borrow")
    public ResponseEntity<Boolean> borrowStaff(
            @RequestParam Long targetGeofenceId,
            @RequestParam Integer staffCount) {
        return ResponseEntity.ok(dispatchService.borrowStaffFromNearbyGeofence(targetGeofenceId, staffCount));
    }
    
    @PostMapping("/batch/{batchId}/reassign")
    public ResponseEntity<DispatchResultResponse> reassignBatch(
            @PathVariable Long batchId,
            @RequestParam Long newStaffId) {
        return ResponseEntity.ok(dispatchService.reassignBatch(batchId, newStaffId));
    }
    
    @GetMapping("/geofence/{geofenceId}/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@PathVariable Long geofenceId) {
        return ResponseEntity.ok(dispatchService.getDispatchSuggestions(geofenceId));
    }
}
