package com.im.controller.geofence;

import com.im.common.Result;
import com.im.entity.geofence.GroupGeoFence;
import com.im.entity.geofence.MemberArrivalStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 群组围栏控制器
 */
@RestController
@RequestMapping("/api/v1/geofence/group")
public class GroupGeoFenceController {
    
    private final Map<String, GroupGeoFence> groupFenceStore = new ConcurrentHashMap<>();
    
    /**
     * 创建群组围栏
     */
    @PostMapping("/fences")
    public Result<GroupGeoFence> createGroupFence(@RequestBody GroupGeoFence groupFence) {
        groupFence.setGroupFenceId(UUID.randomUUID().toString());
        groupFence.setCreateTime(java.time.LocalDateTime.now());
        groupFence.setUpdateTime(java.time.LocalDateTime.now());
        groupFence.setActive(true);
        groupFence.setArrivedCount(0);
        groupFence.setPendingCount(0);
        groupFenceStore.put(groupFence.getGroupFenceId(), groupFence);
        return Result.success(groupFence);
    }
    
    /**
     * 获取群组围栏详情
     */
    @GetMapping("/fences/{groupFenceId}")
    public Result<GroupGeoFence> getGroupFence(@PathVariable String groupFenceId) {
        GroupGeoFence fence = groupFenceStore.get(groupFenceId);
        return Result.success(fence);
    }
    
    /**
     * 获取群组的所有围栏
     */
    @GetMapping("/groups/{groupId}/fences")
    public Result<List<GroupGeoFence>> getGroupFences(@PathVariable String groupId) {
        List<GroupGeoFence> fences = groupFenceStore.values().stream()
                .filter(f -> groupId.equals(f.getGroupId()))
                .collect(Collectors.toList());
        return Result.success(fences);
    }
    
    /**
     * 更新成员到达状态
     */
    @PostMapping("/fences/{groupFenceId}/members/{userId}/arrive")
    public Result<Void> updateMemberArrival(
            @PathVariable String groupFenceId,
            @PathVariable String userId,
            @RequestBody MemberArrivalStatus status) {
        
        GroupGeoFence groupFence = groupFenceStore.get(groupFenceId);
        if (groupFence == null) {
            return Result.error("群组围栏不存在");
        }
        
        List<MemberArrivalStatus> members = groupFence.getMemberStatuses();
        if (members == null) {
            members = new ArrayList<>();
            groupFence.setMemberStatuses(members);
        }
        
        // 查找或创建成员状态
        MemberArrivalStatus memberStatus = members.stream()
                .filter(m -> userId.equals(m.getUserId()))
                .findFirst()
                .orElse(null);
        
        if (memberStatus == null) {
            memberStatus = new MemberArrivalStatus();
            memberStatus.setUserId(userId);
            members.add(memberStatus);
        }
        
        memberStatus.setArrivalStatus("ARRIVED");
        memberStatus.setEnterTime(java.time.LocalDateTime.now());
        memberStatus.setLongitude(status.getLongitude());
        memberStatus.setLatitude(status.getLatitude());
        
        // 更新统计
        updateArrivalStats(groupFence);
        groupFence.setUpdateTime(java.time.LocalDateTime.now());
        
        return Result.success();
    }
    
    /**
     * 获取成员到达状态列表
     */
    @GetMapping("/fences/{groupFenceId}/members")
    public Result<Map<String, Object>> getMemberStatuses(@PathVariable String groupFenceId) {
        GroupGeoFence groupFence = groupFenceStore.get(groupFenceId);
        if (groupFence == null) {
            return Result.error("群组围栏不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("groupFenceId", groupFenceId);
        result.put("members", groupFence.getMemberStatuses());
        result.put("arrivedCount", groupFence.getArrivedCount());
        result.put("pendingCount", groupFence.getPendingCount());
        
        return Result.success(result);
    }
    
    /**
     * 更新到达统计
     */
    private void updateArrivalStats(GroupGeoFence groupFence) {
        List<MemberArrivalStatus> members = groupFence.getMemberStatuses();
        if (members == null) return;
        
        long arrived = members.stream()
                .filter(m -> "ARRIVED".equals(m.getArrivalStatus()))
                .count();
        long pending = members.stream()
                .filter(m -> !"ARRIVED".equals(m.getArrivalStatus()))
                .count();
        
        groupFence.setArrivedCount((int) arrived);
        groupFence.setPendingCount((int) pending);
    }
    
    /**
     * 解散群组围栏
     */
    @DeleteMapping("/fences/{groupFenceId}")
    public Result<Void> dissolveGroupFence(@PathVariable String groupFenceId) {
        groupFenceStore.remove(groupFenceId);
        return Result.success();
    }
}
