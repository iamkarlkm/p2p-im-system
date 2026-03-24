package com.im.server.poll;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 投票实体
 * 群聊投票功能核心数据模型
 */
public class Poll {

    private String pollId;                    // 投票ID
    private String creatorId;                  // 创建者用户ID
    private String groupId;                    // 群组ID
    private String conversationId;             // 会话ID
    private String question;                  // 投票问题
    private List<PollOption> options;         // 投票选项
    private boolean anonymous;                 // 是否匿名投票
    private boolean multiSelect;               // 是否多选
    private LocalDateTime deadline;            // 截止时间（null=无截止）
    private PollStatus status;                 // 投票状态
    private int totalVotes;                    // 总投票数
    private int totalParticipants;             // 总参与人数
    private LocalDateTime createdAt;           // 创建时间
    private LocalDateTime updatedAt;           // 更新时间
    private boolean allowOptionAdd;            // 是否允许创建者添加新选项
    private String messageId;                  // 关联的消息ID

    public enum PollStatus {
        ACTIVE,    // 进行中
        CLOSED,    // 已结束
        CANCELLED  // 已取消
    }

    public Poll() {
        this.options = new ArrayList<>();
        this.status = PollStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.totalVotes = 0;
        this.totalParticipants = 0;
        this.anonymous = false;
        this.multiSelect = false;
        this.allowOptionAdd = true;
    }

    public Poll(String pollId, String creatorId, String groupId, String question) {
        this();
        this.pollId = pollId;
        this.creatorId = creatorId;
        this.groupId = groupId;
        this.conversationId = "conv_" + groupId;
        this.question = question;
    }

    // ==================== Builder ====================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Poll poll = new Poll();

        public Builder pollId(String pollId) {
            poll.pollId = pollId;
            return this;
        }

        public Builder creatorId(String creatorId) {
            poll.creatorId = creatorId;
            return this;
        }

        public Builder groupId(String groupId) {
            poll.groupId = groupId;
            return this;
        }

        public Builder conversationId(String conversationId) {
            poll.conversationId = conversationId;
            return this;
        }

        public Builder question(String question) {
            poll.question = question;
            return this;
        }

        public Builder options(List<PollOption> options) {
            poll.options = new ArrayList<>(options);
            return this;
        }

        public Builder addOption(PollOption option) {
            poll.options.add(option);
            return this;
        }

        public Builder anonymous(boolean anonymous) {
            poll.anonymous = anonymous;
            return this;
        }

        public Builder multiSelect(boolean multiSelect) {
            poll.multiSelect = multiSelect;
            return this;
        }

        public Builder deadline(LocalDateTime deadline) {
            poll.deadline = deadline;
            return this;
        }

        public Builder deadlineMinutes(long minutes) {
            poll.deadline = LocalDateTime.now().plusMinutes(minutes);
            return this;
        }

        public Builder allowOptionAdd(boolean allow) {
            poll.allowOptionAdd = allow;
            return this;
        }

        public Builder messageId(String messageId) {
            poll.messageId = messageId;
            return this;
        }

        public Poll build() {
            if (poll.pollId == null || poll.pollId.isEmpty()) {
                poll.pollId = "poll_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
            }
            if (poll.options == null) {
                poll.options = new ArrayList<>();
            }
            // 设置选项索引
            for (int i = 0; i < poll.options.size(); i++) {
                poll.options.get(i).setOptionIndex(i);
            }
            return poll;
        }
    }

    // ==================== 选项操作 ====================

    public void addOption(String optionText) {
        PollOption option = PollOption.builder()
            .optionId("opt_" + System.currentTimeMillis() + "_" + options.size())
            .optionText(optionText)
            .optionIndex(options.size())
            .voteCount(0)
            .voterIds(new ArrayList<>())
            .build();
        options.add(option);
        updatedAt = LocalDateTime.now();
    }

    public void addVoteToOption(String optionId, String userId) {
        for (PollOption opt : options) {
            if (opt.getOptionId().equals(optionId)) {
                opt.addVote(userId);
                totalVotes++;
                if (!opt.getVoterIds().contains(userId)) {
                    totalParticipants++;
                }
                updatedAt = LocalDateTime.now();
                return;
            }
        }
    }

    public void removeVoteFromOption(String optionId, String userId) {
        for (PollOption opt : options) {
            if (opt.getOptionId().equals(optionId)) {
                if (opt.removeVote(userId)) {
                    totalVotes--;
                    // 检查用户是否还有其他投票
                    boolean stillVoting = options.stream().anyMatch(o -> o.getVoterIds().contains(userId));
                    if (!stillVoting) {
                        totalParticipants--;
                    }
                    updatedAt = LocalDateTime.now();
                }
                return;
            }
        }
    }

    public boolean hasUserVoted(String userId) {
        return options.stream().anyMatch(o -> o.getVoterIds().contains(userId));
    }

    public List<String> getVotedOptionIds(String userId) {
        return options.stream()
            .filter(o -> o.getVoterIds().contains(userId))
            .map(PollOption::getOptionId)
            .collect(Collectors.toList());
    }

    public PollOption getOptionById(String optionId) {
        return options.stream()
            .filter(o -> o.getOptionId().equals(optionId))
            .findFirst()
            .orElse(null);
    }

    // ==================== 状态操作 ====================

    public void close() {
        this.status = PollStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = PollStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        if (deadline == null) return false;
        return LocalDateTime.now().isAfter(deadline);
    }

    public boolean isActive() {
        return status == PollStatus.ACTIVE && !isExpired();
    }

    // ==================== 统计 ====================

    public double getPercentage(String optionId) {
        if (totalVotes == 0) return 0.0;
        PollOption opt = getOptionById(optionId);
        if (opt == null) return 0.0;
        return (double) opt.getVoteCount() / totalVotes * 100.0;
    }

    public String getTopOptionId() {
        return options.stream()
            .max((a, b) -> Integer.compare(a.getVoteCount(), b.getVoteCount()))
            .map(PollOption::getOptionId)
            .orElse(null);
    }

    public List<PollOption> getSortedOptionsByVotes() {
        return options.stream()
            .sorted((a, b) -> Integer.compare(b.getVoteCount(), a.getVoteCount()))
            .collect(Collectors.toList());
    }

    // ==================== Getters & Setters ====================

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public PollStatus getStatus() {
        return status;
    }

    public void setStatus(PollStatus status) {
        this.status = status;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public int getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(int totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isAllowOptionAdd() {
        return allowOptionAdd;
    }

    public void setAllowOptionAdd(boolean allowOptionAdd) {
        this.allowOptionAdd = allowOptionAdd;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "Poll{" +
            "pollId='" + pollId + '\'' +
            ", creatorId='" + creatorId + '\'' +
            ", groupId='" + groupId + '\'' +
            ", question='" + question + '\'' +
            ", options=" + options.size() + " options" +
            ", anonymous=" + anonymous +
            ", multiSelect=" + multiSelect +
            ", deadline=" + deadline +
            ", status=" + status +
            ", totalVotes=" + totalVotes +
            ", totalParticipants=" + totalParticipants +
            '}';
    }
}
