package com.im.backend.controller;

import com.im.backend.dto.MessageEditDTO;
import com.im.backend.dto.ApiResponse;
import com.im.backend.model.MessageEditHistory;
import com.im.backend.service.MessageEditService;
import com.im.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息编辑API控制器
 */
@RestController
@RequestMapping("/api/v1/messages/edit")
@Tag(name = "消息编辑", description = "消息编辑相关接口")
@SecurityRequirement(name = "bearerAuth")
public class MessageEditController {

    private static final Logger logger = LoggerFactory.getLogger(MessageEditController.class);

    @Autowired
    private MessageEditService editService;

    @Autowired
    private AuthService authService;

    /**
     * 编辑消息
     */
    @PutMapping("/{messageId}")
    @Operation(summary = "编辑消息", description = "编辑指定消息的内容")
    public ResponseEntity<ApiResponse<MessageEditDTO>> editMessage(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @RequestBody @Valid EditMessageRequest request,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        logger.info("User {} editing message {}", userId, messageId);

        MessageEditDTO result = editService.editMessage(
            messageId, userId, request.getNewContent(), request.getEditReason());

        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result, "消息编辑成功"));
        } else {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(result.getErrorCode(), result.getErrorMessage()));
        }
    }

    /**
     * 获取消息编辑历史
     */
    @GetMapping("/{messageId}/history")
    @Operation(summary = "获取编辑历史", description = "获取指定消息的编辑历史记录")
    public ResponseEntity<ApiResponse<List<MessageEditHistory>>> getEditHistory(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @RequestHeader("Authorization") String token) {

        authService.validateToken(token);
        List<MessageEditHistory> history = editService.getEditHistory(messageId);

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 获取消息编辑历史（分页）
     */
    @GetMapping("/{messageId}/history/paged")
    @Operation(summary = "获取编辑历史(分页)", description = "分页获取消息的编辑历史")
    public ResponseEntity<ApiResponse<Page<MessageEditHistory>>> getEditHistoryPaged(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token) {

        authService.validateToken(token);
        Page<MessageEditHistory> history = editService.getEditHistory(messageId, page, size);

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 获取用户编辑历史
     */
    @GetMapping("/history/user")
    @Operation(summary = "获取用户编辑历史", description = "获取当前用户的所有编辑记录")
    public ResponseEntity<ApiResponse<Page<MessageEditHistory>>> getUserEditHistory(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        Page<MessageEditHistory> history = editService.getUserEditHistory(userId, page, size);

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 恢复到指定版本
     */
    @PostMapping("/{messageId}/revert/{version}")
    @Operation(summary = "恢复消息版本", description = "将消息恢复到指定的历史版本")
    public ResponseEntity<ApiResponse<MessageEditDTO>> revertToVersion(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "目标版本号") @PathVariable int version,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        logger.info("User {} reverting message {} to version {}", userId, messageId, version);

        MessageEditDTO result = editService.revertToVersion(messageId, userId, version);

        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result, "消息已恢复到版本 " + version));
        } else {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(result.getErrorCode(), result.getErrorMessage()));
        }
    }

    /**
     * 比较版本差异
     */
    @GetMapping("/{messageId}/compare")
    @Operation(summary = "比较版本差异", description = "比较两个版本的差异")
    public ResponseEntity<ApiResponse<Map<String, Object>>> compareVersions(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "版本1") @RequestParam int version1,
            @Parameter(description = "版本2") @RequestParam int version2,
            @RequestHeader("Authorization") String token) {

        authService.validateToken(token);
        String diff = editService.compareVersions(messageId, version1, version2);

        Map<String, Object> result = new HashMap<>();
        result.put("messageId", messageId);
        result.put("version1", version1);
        result.put("version2", version2);
        result.put("diff", diff);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取编辑配置
     */
    @GetMapping("/config")
    @Operation(summary = "获取编辑配置", description = "获取当前消息编辑的配置参数")
    public ResponseEntity<ApiResponse<MessageEditDTO.EditConfig>> getEditConfig(
            @RequestHeader("Authorization") String token) {

        authService.validateToken(token);
        MessageEditDTO.EditConfig config = editService.getEditConfig();

        return ResponseEntity.ok(ApiResponse.success(config));
    }

    /**
     * 更新编辑配置（仅管理员）
     */
    @PutMapping("/config")
    @Operation(summary = "更新编辑配置", description = "更新消息编辑的配置参数（仅管理员可用）")
    public ResponseEntity<ApiResponse<MessageEditDTO.EditConfig>> updateEditConfig(
            @RequestBody @Valid UpdateConfigRequest request,
            @RequestHeader("Authorization") String token) {

        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.error("FORBIDDEN", "需要管理员权限"));
        }

        try {
            editService.updateEditConfig(request.getTimeLimitSeconds(), request.getMaxEditCount());
            MessageEditDTO.EditConfig config = editService.getEditConfig();

            return ResponseEntity.ok(ApiResponse.success(config, "编辑配置已更新"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_PARAMETER", e.getMessage()));
        }
    }

    // ========== 内部请求类 ==========

    public static class EditMessageRequest {
        @NotBlank(message = "新内容不能为空")
        private String newContent;

        private String editReason;

        public String getNewContent() {
            return newContent;
        }

        public void setNewContent(String newContent) {
            this.newContent = newContent;
        }

        public String getEditReason() {
            return editReason;
        }

        public void setEditReason(String editReason) {
            this.editReason = editReason;
        }
    }

    public static class UpdateConfigRequest {
        @NotNull(message = "时间限制不能为空")
        private Integer timeLimitSeconds;

        @NotNull(message = "编辑次数限制不能为空")
        private Integer maxEditCount;

        public Integer getTimeLimitSeconds() {
            return timeLimitSeconds;
        }

        public void setTimeLimitSeconds(Integer timeLimitSeconds) {
            this.timeLimitSeconds = timeLimitSeconds;
        }

        public Integer getMaxEditCount() {
            return maxEditCount;
        }

        public void setMaxEditCount(Integer maxEditCount) {
            this.maxEditCount = maxEditCount;
        }
    }
}
