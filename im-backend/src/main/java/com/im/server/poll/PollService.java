package com.im.server.poll;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 投票服务
 * 核心业务逻辑：创建投票、投票、查询、统计
 */
public class PollService {

    private final PollRepository repository;
    private final ScheduledExecutorService scheduler;
    private final Set<String> processedExpiredPolls;

    // 实时广播处理器（由WebSocket层注入）
    private PollBroadcastHandler broadcastHandler;

    public PollService() {
        this(new PollRepository());
    }

    public PollService(PollRepository repository) {
        this.repository = repository;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "poll-scheduler");
            t.setDaemon(true);
            return t;
        });
        this.processedExpiredPolls = ConcurrentHashMap.newKeySet();
        startExpirationChecker();
    }

    // ==================== 投票创建 ====================

    /**
     * 创建投票
     */
    public Poll createPoll(CreatePollRequest request) {
        validateCreateRequest(request);

        // 构建投票选项
        List<PollOption> options = new ArrayList<>();
        for (int i = 0; i < request.getOptionTexts().size(); i++) {
            PollOption option = PollOption.builder()
                .optionId("opt_" + System.currentTimeMillis() + "_" + i)
                .optionText(request.getOptionTexts().get(i))
                .optionIndex(i)
                .voteCount(0)
                .voterIds(new ArrayList<>())
                .build();
            options.add(option);
        }

        // 构建投票
        Poll poll = Poll.builder()
            .pollId("poll_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8))
            .creatorId(request.getCreatorId())
            .groupId(request.getGroupId())
            .conversationId(request.getConversationId())
            .question(request.getQuestion())
            .options(options)
            .anonymous(request.isAnonymous())
            .multiSelect(request.isMultiSelect())
            .deadline(request.getDeadline())
            .allowOptionAdd(request.isAllowOptionAdd())
            .messageId(request.getMessageId())
            .build();

        repository.save(poll);

        // 广播创建事件
        broadcast("poll.created", poll, null);

        return poll;
    }

    /**
     * 验证创建请求
     */
    private void validateCreateRequest(CreatePollRequest request) {
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            throw new PollException("Question cannot be empty");
        }
        if (request.getQuestion().length() > 500) {
            throw new PollException("Question too long (max 500 characters)");
        }
        if (request.getOptionTexts() == null || request.getOptionTexts().size() < 2) {
            throw new PollException("At least 2 options required");
        }
        if (request.getOptionTexts().size() > 10) {
            throw new PollException("Maximum 10 options allowed");
        }
        for (String text : request.getOptionTexts()) {
            if (text == null || text.trim().isEmpty()) {
                throw new PollException("Option text cannot be empty");
            }
            if (text.length() > 200) {
                throw new PollException("Option text too long (max 200 characters)");
            }
        }
    }

    // ==================== 投票操作 ====================

    /**
     * 投票（单选或多选）
     */
    public PollResult vote(String pollId, String userId, List<String> optionIds) {
        Poll poll = repository.getById(pollId);

        if (!poll.isActive()) {
            throw new PollException("Poll is not active");
        }

        // 验证选项
        if (optionIds == null || optionIds.isEmpty()) {
            throw new PollException("At least one option required");
        }

        if (!poll.isMultiSelect() && optionIds.size() > 1) {
            throw new PollException("Single select poll: only one option allowed");
        }

        if (optionIds.size() > 10) {
            throw new PollException("Maximum 10 options per vote");
        }

        // 验证所有选项存在
        Set<String> validOptionIds = poll.getOptions().stream()
            .map(PollOption::getOptionId)
            .collect(Collectors.toSet());
        for (String optId : optionIds) {
            if (!validOptionIds.contains(optId)) {
                throw new PollException("Invalid option: " + optId);
            }
        }

        // 记录投票
        List<String> previousVoteOptions = poll.getVotedOptionIds(userId);
        List<String> newVoteOptions = new ArrayList<>();
        List<String> removedOptions = new ArrayList<>();

        for (String optId : optionIds) {
            boolean alreadyVoted = repository.hasUserVoted(pollId, userId, optId);
            if (!alreadyVoted) {
                PollVote vote = PollVote.builder()
                    .pollId(pollId)
                    .optionId(optId)
                    .userId(userId)
                    .build();
                repository.saveVote(vote);
                poll.addVoteToOption(optId, userId);
                newVoteOptions.add(optId);
            }
        }

        // 记录被移除的投票（用于前端更新）
        for (String prevOptId : previousVoteOptions) {
            if (!optionIds.contains(prevOptId)) {
                repository.deleteVote(pollId, userId, prevOptId);
                poll.removeVoteFromOption(prevOptId, userId);
                removedOptions.add(prevOptId);
            }
        }

        repository.update(poll);

        // 广播更新事件
        PollResult result = buildPollResult(poll, userId);
        broadcast("poll.updated", poll, userId);

        return result;
    }

    /**
     * 取消投票
     */
    public PollResult cancelVote(String pollId, String userId) {
        Poll poll = repository.getById(pollId);

        if (!poll.isActive()) {
            throw new PollException("Poll is not active");
        }

        List<String> votedOptions = poll.getVotedOptionIds(userId);
        if (votedOptions.isEmpty()) {
            throw new PollException("No votes to cancel");
        }

        for (String optId : votedOptions) {
            repository.deleteVote(pollId, userId, optId);
            poll.removeVoteFromOption(optId, userId);
        }

        repository.update(poll);

        // 广播更新事件
        PollResult result = buildPollResult(poll, userId);
        broadcast("poll.updated", poll, userId);

        return result;
    }

    // ==================== 投票管理 ====================

    /**
     * 结束投票
     */
    public PollResult closePoll(String pollId, String userId) {
        Poll poll = repository.getById(pollId);

        if (!userId.equals(poll.getCreatorId())) {
            throw new PollException("Only the creator can close the poll");
        }

        poll.close();
        repository.update(poll);

        // 广播结束事件
        PollResult result = buildPollResult(poll, userId);
        broadcast("poll.closed", poll, null);

        return result;
    }

    /**
     * 删除投票
     */
    public boolean deletePoll(String pollId, String userId) {
        Poll poll = repository.getById(pollId);

        if (!userId.equals(poll.getCreatorId())) {
            throw new PollException("Only the creator can delete the poll");
        }

        boolean deleted = repository.deleteById(pollId);

        if (deleted) {
            broadcast("poll.deleted", poll, null);
        }

        return deleted;
    }

    /**
     * 添加新选项（仅创建者可操作）
     */
    public PollResult addOption(String pollId, String userId, String optionText) {
        Poll poll = repository.getById(pollId);

        if (!userId.equals(poll.getCreatorId())) {
            throw new PollException("Only the creator can add options");
        }
        if (!poll.isAllowOptionAdd()) {
            throw new PollException("Adding options is not allowed for this poll");
        }
        if (!poll.isActive()) {
            throw new PollException("Poll is not active");
        }
        if (optionText == null || optionText.trim().isEmpty()) {
            throw new PollException("Option text cannot be empty");
        }
        if (optionText.length() > 200) {
            throw new PollException("Option text too long (max 200 characters)");
        }
        if (poll.getOptions().size() >= 10) {
            throw new PollException("Maximum 10 options reached");
        }

        poll.addOption(optionText);
        repository.update(poll);

        // 广播更新事件
        PollResult result = buildPollResult(poll, userId);
        broadcast("poll.updated", poll, null);

        return result;
    }

    // ==================== 查询 ====================

    /**
     * 获取投票详情
     */
    public PollResult getPoll(String pollId, String userId) {
        Poll poll = repository.getById(pollId);
        return buildPollResult(poll, userId);
    }

    /**
     * 获取群组所有投票
     */
    public List<PollResult> getGroupPolls(String groupId, String userId) {
        return repository.findByGroupId(groupId).stream()
            .map(p -> buildPollResult(p, userId))
            .collect(Collectors.toList());
    }

    /**
     * 获取群组进行中的投票
     */
    public List<PollResult> getGroupActivePolls(String groupId, String userId) {
        return repository.findActiveByGroupId(groupId).stream()
            .map(p -> buildPollResult(p, userId))
            .collect(Collectors.toList());
    }

    /**
     * 获取用户参与的投票
     */
    public List<PollResult> getUserVotedPolls(String userId) {
        return repository.findVotedByUserId(userId).stream()
            .map(p -> buildPollResult(p, userId))
            .collect(Collectors.toList());
    }

    // ==================== 统计 ====================

    /**
     * 获取投票结果
     */
    public PollResult buildPollResult(Poll poll, String userId) {
        PollResult result = new PollResult();
        result.setPollId(poll.getPollId());
        result.setCreatorId(poll.getCreatorId());
        result.setGroupId(poll.getGroupId());
        result.setConversationId(poll.getConversationId());
        result.setQuestion(poll.getQuestion());
        result.setAnonymous(poll.isAnonymous());
        result.setMultiSelect(poll.isMultiSelect());
        result.setDeadline(poll.getDeadline());
        result.setStatus(poll.getStatus().name());
        result.setTotalVotes(poll.getTotalVotes());
        result.setTotalParticipants(poll.getTotalParticipants());
        result.setCreatedAt(poll.getCreatedAt());
        result.setUpdatedAt(poll.getUpdatedAt());
        result.setAllowOptionAdd(poll.isAllowOptionAdd());
        result.setMessageId(poll.getMessageId());

        // 构建选项结果
        List<PollOptionResult> optionResults = new ArrayList<>();
        for (PollOption opt : poll.getOptions()) {
            PollOptionResult optResult = new PollOptionResult();
            optResult.setOptionId(opt.getOptionId());
            optResult.setOptionText(opt.getOptionText());
            optResult.setVoteCount(opt.getVoteCount());
            optResult.setPercentage(poll.getPercentage(opt.getOptionId()));
            optResult.setHasVoted(opt.hasVoted(userId));
            // 匿名投票不暴露投票者列表
            if (!poll.isAnonymous()) {
                optResult.setVoterIds(opt.getVoterIdsSnapshot());
            }
            optionResults.add(optResult);
        }
        result.setOptions(optionResults);

        // 用户投票状态
        result.setHasVoted(poll.hasUserVoted(userId));
        result.setVotedOptionIds(poll.getVotedOptionIds(userId));

        // 剩余时间
        if (poll.getDeadline() != null) {
            result.setRemainingSeconds(ChronoUnit.SECONDS.between(LocalDateTime.now(), poll.getDeadline()));
        }

        return result;
    }

    // ==================== 过期检查 ====================

    private void startExpirationChecker() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                for (Poll poll : repository.findByGroupId("*")) {
                    // 遍历所有投票检查过期
                }
                // 简单策略：定期检查所有活跃投票
                for (Poll poll : new ArrayList<>(repository.findByGroupId("*"))) {
                    if (poll.isActive() && poll.isExpired()) {
                        if (!processedExpiredPolls.contains(poll.getPollId())) {
                            processedExpiredPolls.add(poll.getPollId());
                            poll.close();
                            repository.update(poll);
                            broadcast("poll.closed", poll, null);
                        }
                    }
                }
            } catch (Exception e) {
                // 日志记录
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    // ==================== 广播 ====================

    public void setBroadcastHandler(PollBroadcastHandler handler) {
        this.broadcastHandler = handler;
    }

    private void broadcast(String event, Poll poll, String excludeUserId) {
        if (broadcastHandler != null) {
            broadcastHandler.broadcast(event, poll, excludeUserId);
        }
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStats() {
        return repository.getStats();
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    // ==================== DTO ====================

    public static class CreatePollRequest {
        private String creatorId;
        private String groupId;
        private String conversationId;
        private String question;
        private List<String> optionTexts;
        private boolean anonymous;
        private boolean multiSelect;
        private LocalDateTime deadline;
        private boolean allowOptionAdd = true;
        private String messageId;

        // Getters and Setters
        public String getCreatorId() { return creatorId; }
        public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public List<String> getOptionTexts() { return optionTexts; }
        public void setOptionTexts(List<String> optionTexts) { this.optionTexts = optionTexts; }
        public boolean isAnonymous() { return anonymous; }
        public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
        public boolean isMultiSelect() { return multiSelect; }
        public void setMultiSelect(boolean multiSelect) { this.multiSelect = multiSelect; }
        public LocalDateTime getDeadline() { return deadline; }
        public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
        public boolean isAllowOptionAdd() { return allowOptionAdd; }
        public void setAllowOptionAdd(boolean allowOptionAdd) { this.allowOptionAdd = allowOptionAdd; }
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
    }

    public static class PollResult {
        private String pollId;
        private String creatorId;
        private String groupId;
        private String conversationId;
        private String question;
        private List<PollOptionResult> options;
        private boolean anonymous;
        private boolean multiSelect;
        private LocalDateTime deadline;
        private String status;
        private int totalVotes;
        private int totalParticipants;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean allowOptionAdd;
        private String messageId;
        private boolean hasVoted;
        private List<String> votedOptionIds;
        private Long remainingSeconds;

        // Getters and Setters
        public String getPollId() { return pollId; }
        public void setPollId(String pollId) { this.pollId = pollId; }
        public String getCreatorId() { return creatorId; }
        public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public List<PollOptionResult> getOptions() { return options; }
        public void setOptions(List<PollOptionResult> options) { this.options = options; }
        public boolean isAnonymous() { return anonymous; }
        public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
        public boolean isMultiSelect() { return multiSelect; }
        public void setMultiSelect(boolean multiSelect) { this.multiSelect = multiSelect; }
        public LocalDateTime getDeadline() { return deadline; }
        public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getTotalVotes() { return totalVotes; }
        public void setTotalVotes(int totalVotes) { this.totalVotes = totalVotes; }
        public int getTotalParticipants() { return totalParticipants; }
        public void setTotalParticipants(int totalParticipants) { this.totalParticipants = totalParticipants; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public boolean isAllowOptionAdd() { return allowOptionAdd; }
        public void setAllowOptionAdd(boolean allowOptionAdd) { this.allowOptionAdd = allowOptionAdd; }
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public boolean isHasVoted() { return hasVoted; }
        public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }
        public List<String> getVotedOptionIds() { return votedOptionIds; }
        public void setVotedOptionIds(List<String> votedOptionIds) { this.votedOptionIds = votedOptionIds; }
        public Long getRemainingSeconds() { return remainingSeconds; }
        public void setRemainingSeconds(Long remainingSeconds) { this.remainingSeconds = remainingSeconds; }
    }

    public static class PollOptionResult {
        private String optionId;
        private String optionText;
        private int voteCount;
        private double percentage;
        private boolean hasVoted;
        private List<String> voterIds;

        // Getters and Setters
        public String getOptionId() { return optionId; }
        public void setOptionId(String optionId) { this.optionId = optionId; }
        public String getOptionText() { return optionText; }
        public void setOptionText(String optionText) { this.optionText = optionText; }
        public int getVoteCount() { return voteCount; }
        public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
        public boolean isHasVoted() { return hasVoted; }
        public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }
        public List<String> getVoterIds() { return voterIds; }
        public void setVoterIds(List<String> voterIds) { this.voterIds = voterIds; }
    }

    public interface PollBroadcastHandler {
        void broadcast(String event, Poll poll, String excludeUserId);
    }
}
