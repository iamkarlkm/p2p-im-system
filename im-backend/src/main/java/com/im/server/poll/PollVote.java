package com.im.server.poll;

import java.time.LocalDateTime;

/**
 * 投票记录实体
 * 记录用户对某个投票的投票行为
 */
public class PollVote {

    private String voteId;          // 投票记录ID
    private String pollId;          // 投票ID
    private String optionId;        // 选项ID
    private String userId;          // 投票用户ID
    private LocalDateTime votedAt;  // 投票时间

    public PollVote() {
        this.votedAt = LocalDateTime.now();
    }

    public PollVote(String pollId, String optionId, String userId) {
        this();
        this.voteId = "vote_" + System.currentTimeMillis() + "_" + hashCode();
        this.pollId = pollId;
        this.optionId = optionId;
        this.userId = userId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PollVote vote = new PollVote();

        public Builder voteId(String voteId) {
            vote.voteId = voteId;
            return this;
        }

        public Builder pollId(String pollId) {
            vote.pollId = pollId;
            return this;
        }

        public Builder optionId(String optionId) {
            vote.optionId = optionId;
            return this;
        }

        public Builder userId(String userId) {
            vote.userId = userId;
            return this;
        }

        public Builder votedAt(LocalDateTime votedAt) {
            vote.votedAt = votedAt;
            return this;
        }

        public PollVote build() {
            if (vote.voteId == null || vote.voteId.isEmpty()) {
                vote.voteId = "vote_" + System.currentTimeMillis() + "_" + Math.abs(vote.hashCode());
            }
            return vote;
        }
    }

    // ==================== Getters & Setters ====================

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }

    /**
     * 复合唯一键：同一用户对同一投票的同一选项只能有一条记录
     */
    public String getUniqueKey() {
        return pollId + ":" + userId + ":" + optionId;
    }

    @Override
    public String toString() {
        return "PollVote{" +
            "voteId='" + voteId + '\'' +
            ", pollId='" + pollId + '\'' +
            ", optionId='" + optionId + '\'' +
            ", userId='" + userId + '\'' +
            ", votedAt=" + votedAt +
            '}';
    }
}
