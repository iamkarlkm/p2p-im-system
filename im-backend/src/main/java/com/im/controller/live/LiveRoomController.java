package com.im.controller.live;

import com.im.common.Result;
import com.im.common.PageResult;
import com.im.dto.live.*;
import com.im.service.live.LiveRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 直播控制器
 * 小程序直播与本地电商 - 直播间管理API
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@RestController
@RequestMapping("/api/v1/live")
@RequiredArgsConstructor
@Tag(name = "直播间管理", description = "小程序直播相关接口")
public class LiveRoomController {

    private final LiveRoomService liveRoomService;

    @PostMapping("/rooms")
    @Operation(summary = "创建直播间", description = "主播创建新的直播间")
    public Result<LiveRoomDetailDTO> createLiveRoom(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Validated CreateLiveRoomRequestDTO request) {
        return Result.success(liveRoomService.createLiveRoom(userId, request));
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "获取直播间详情")
    public Result<LiveRoomDetailDTO> getLiveRoomDetail(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId) {
        return Result.success(liveRoomService.getLiveRoomDetail(roomId, userId));
    }

    @PostMapping("/rooms/{roomId}/start")
    @Operation(summary = "开始直播")
    public Result<LiveRoomDetailDTO> startLive(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId) {
        return Result.success(liveRoomService.startLive(roomId, userId));
    }

    @PostMapping("/rooms/{roomId}/end")
    @Operation(summary = "结束直播")
    public Result<Void> endLive(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId) {
        liveRoomService.endLive(roomId, userId);
        return Result.success();
    }

    @PostMapping("/rooms/{roomId}/pause")
    @Operation(summary = "暂停直播")
    public Result<Void> pauseLive(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId) {
        liveRoomService.pauseLive(roomId, userId);
        return Result.success();
    }

    @PostMapping("/rooms/{roomId}/resume")
    @Operation(summary = "恢复直播")
    public Result<Void> resumeLive(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId) {
        liveRoomService.resumeLive(roomId, userId);
        return Result.success();
    }

    @GetMapping("/rooms")
    @Operation(summary = "获取直播间列表")
    public Result<PageResult<LiveRoomListDTO>> listLiveRooms(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer liveType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        return Result.success(liveRoomService.listLiveRooms(page, size, status, liveType, 
                keyword, latitude, longitude));
    }

    @GetMapping("/rooms/recommended")
    @Operation(summary = "获取推荐直播间")
    public Result<List<LiveRoomListDTO>> getRecommendedRooms(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(liveRoomService.getRecommendedRooms(limit));
    }

    @GetMapping("/rooms/nearby")
    @Operation(summary = "获取附近直播间")
    public Result<List<LiveRoomListDTO>> getNearbyRooms(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(liveRoomService.getNearbyRooms(latitude, longitude, radius, limit));
    }

    @PostMapping("/rooms/{roomId}/enter")
    @Operation(summary = "进入直播间")
    public Result<Void> enterRoom(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId) {
        liveRoomService.enterRoom(roomId, userId);
        return Result.success();
    }

    @PostMapping("/rooms/{roomId}/leave")
    @Operation(summary = "离开直播间")
    public Result<Void> leaveRoom(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId) {
        liveRoomService.leaveRoom(roomId, userId);
        return Result.success();
    }

    @PostMapping("/rooms/{roomId}/comments")
    @Operation(summary = "发送弹幕/评论")
    public Result<LiveCommentDTO> sendComment(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId,
            @RequestParam String content) {
        return Result.success(liveRoomService.sendComment(roomId, userId, content));
    }

    @PostMapping("/rooms/{roomId}/like")
    @Operation(summary = "点赞")
    public Result<Void> likeLive(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer count) {
        liveRoomService.likeLive(roomId, userId, count);
        return Result.success();
    }

    @PostMapping("/rooms/{roomId}/share")
    @Operation(summary = "分享直播间")
    public Result<Void> shareLive(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId) {
        liveRoomService.shareLive(roomId, userId);
        return Result.success();
    }

    @GetMapping("/rooms/{roomId}/viewers")
    @Operation(summary = "获取在线观众列表")
    public Result<PageResult<LiveViewerDTO>> getOnlineViewers(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer size) {
        return Result.success(liveRoomService.getOnlineViewers(roomId, page, size));
    }

    @GetMapping("/replays")
    @Operation(summary = "获取直播回放列表")
    public Result<PageResult<LiveReplayDTO>> getReplayList(
            @RequestParam Long anchorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(liveRoomService.getReplayList(anchorId, page, size));
    }
}
