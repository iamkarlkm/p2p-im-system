package com.im.server.poll;

import java.util.*;

/**
 * 投票控制器
 * REST API 端点
 */
public class PollController {

    private final PollService pollService;

    public PollController() {
        this.pollService = new PollService();
    }

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    // ==================== API 端点 ====================

    /**
     * POST /api/poll - 创建投票
     */
    public ApiResponse<PollService.PollResult> createPoll(CreatePollRequest request) {
        try {
            validateCreatePollRequest(request);
            PollService.PollResult result = pollService.createPoll(request);
            return ApiResponse.success(result, "Poll created successfully");
        } catch (PollException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to create poll: " + e.getMessage());
        }
    }

    /**
     * GET /api/poll/{pollId} - 获取投票详情
     */
    public ApiResponse<PollService.PollResult> getPoll(String pollId, String userId) {
        try {
            PollService.PollResult result = pollService.getPoll(pollId, userId);
            return ApiResponse.success(result);
        } catch (PollException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to get poll: " + e.getMessage());
        }
    }

    /**
     * POST /api/poll/{pollId}/vote - 投票
     */
    public ApiResponse<PollService.PollResult> vote(String pollId, String userId, List<String> optionIds) {
        try {
            if (optionIds == null || optionIds.isEmpty()) {
                return ApiResponse.error(400, "optionIds is required");
            }
            PollService.PollResult result = pollService.vote(pollId, userId, optionIds);
            return ApiResponse.success(result, "Vote recorded successfully");
        } catch (PollException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to vote: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/poll/{pollId}/vote - 取消投票
     */
    public ApiResponse<PollService.PollResult> cancelVote(String pollId, String userId) {
        try {
            PollService.PollResult result = pollService.cancelVote(pollId, userId);
            return ApiResponse.success(result, "Vote cancelled successfully");
        } catch (PollException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to cancel vote: " + e.getMessage());
        }
    }

    /**
     * POST /api/poll/{pollId}/close - 结束投票
     */
    public ApiResponse<PollService.PollResult> closePoll(String pollId, String userId) {
        try {
            PollService.PollResult result = pollService.closePoll(pollId, userId);
            return ApiResponse.success(result, "Poll closed successfully");
        } catch (PollException e) {
            return ApiResponse.error(403, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to close poll: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/poll/{pollId} - 删除投票
     */
    public ApiResponse<Boolean> deletePoll(String pollId, String userId) {
        try {
            boolean deleted = pollService.deletePoll(pollId, userId);
            return ApiResponse.success(deleted, "Poll deleted successfully");
        } catch (PollException e) {
            return ApiResponse.error(403, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to delete poll: " + e.getMessage());
        }
    }

    /**
     * POST /api/poll/{pollId}/options - 添加选项
     */
    public ApiResponse<PollService.PollResult> addOption(String pollId, String userId, String optionText) {
        try {
            PollService.PollResult result = pollService.addOption(pollId, userId, optionText);
            return ApiResponse.success(result, "Option added successfully");
        } catch (PollException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to add option: " + e.getMessage());
        }
    }

    /**
     * GET /api/poll/group/{groupId} - 获取群组所有投票
     */
    public ApiResponse<List<PollService.PollResult>> getGroupPolls(String groupId, String userId) {
        try {
            List<PollService.PollResult> results = pollService.getGroupPolls(groupId, userId);
            return ApiResponse.success(results);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to get group polls: " + e.getMessage());
        }
    }

    /**
     * GET /api/poll/group/{groupId}/active - 获取群组进行中的投票
     */
    public ApiResponse<List<PollService.PollResult>> getGroupActivePolls(String groupId, String userId) {
        try {
            List<PollService.PollResult> results = pollService.getGroupActivePolls(groupId, userId);
            return ApiResponse.success(results);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to get active polls: " + e.getMessage());
        }
    }

    /**
     * GET /api/poll/user/{userId}/voted - 获取用户参与的投票
     */
    public ApiResponse<List<PollService.PollResult>> getUserVotedPolls(String userId) {
        try {
            List<PollService.PollResult> results = pollService.getUserVotedPolls(userId);
            return ApiResponse.success(results);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to get voted polls: " + e.getMessage());
        }
    }

    /**
     * GET /api/poll/stats - 获取统计信息
     */
    public ApiResponse<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = pollService.getStats();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to get stats: " + e.getMessage());
        }
    }

    // ==================== 验证 ====================

    private void validateCreatePollRequest(CreatePollRequest request) {
        if (request.getCreatorId() == null || request.getCreatorId().trim().isEmpty()) {
            throw new PollException("creatorId is required");
        }
        if (request.getGroupId() == null || request.getGroupId().trim().isEmpty()) {
            throw new PollException("groupId is required");
        }
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            throw new PollException("question is required");
        }
        if (request.getOptionTexts() == null || request.getOptionTexts().size() < 2) {
            throw new PollException("At least 2 options required");
        }
    }

    // ==================== 请求/响应 DTO ====================

    public static class CreatePollRequest extends PollService.CreatePollRequest {
        // 继承自 PollService.CreatePollRequest
    }

    public static class VoteRequest {
        private String userId;
        private List<String> optionIds;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public List<String> getOptionIds() { return optionIds; }
        public void setOptionIds(List<String> optionIds) { this.optionIds = optionIds; }
    }

    public static class AddOptionRequest {
        private String userId;
        private String optionText;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getOptionText() { return optionText; }
        public void setOptionText(String optionText) { this.optionText = optionText; }
    }

    // ==================== API 响应 ====================

    public static class ApiResponse<T> {
        private boolean success;
        private int code;
        private String message;
        private T data;
        private long timestamp;

        public ApiResponse() {
            this.timestamp = System.currentTimeMillis();
        }

        public static <T> ApiResponse<T> success(T data) {
            ApiResponse<T> response = new ApiResponse<>();
            response.success = true;
            response.code = 200;
            response.data = data;
            return response;
        }

        public static <T> ApiResponse<T> success(T data, String message) {
            ApiResponse<T> response = new ApiResponse<>();
            response.success = true;
            response.code = 200;
            response.data = data;
            response.message = message;
            return response;
        }

        public static <T> ApiResponse<T> error(int code, String message) {
            ApiResponse<T> response = new ApiResponse<>();
            response.success = false;
            response.code = code;
            response.message = message;
            return response;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
