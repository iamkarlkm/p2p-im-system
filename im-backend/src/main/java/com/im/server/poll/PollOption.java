package com.im.server.poll;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 投票选项实体
 */
public class PollOption {

    private String optionId;        // 选项ID
    private int optionIndex;        // 选项索引（顺序）
    private String optionText;      // 选项文本
    private int voteCount;          // 投票数
    private List<String> voterIds;  // 投票者ID列表（匿名投票时不暴露）

    public PollOption() {
        this.voterIds = new ArrayList<>();
        this.voteCount = 0;
    }

    public PollOption(String optionId, String optionText, int optionIndex) {
        this();
        this.optionId = optionId;
        this.optionText = optionText;
        this.optionIndex = optionIndex;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PollOption option = new PollOption();

        public Builder optionId(String optionId) {
            option.optionId = optionId;
            return this;
        }

        public Builder optionIndex(int index) {
            option.optionIndex = index;
            return this;
        }

        public Builder optionText(String text) {
            option.optionText = text;
            return this;
        }

        public Builder voteCount(int count) {
            option.voteCount = count;
            return this;
        }

        public Builder voterIds(List<String> ids) {
            option.voterIds = new ArrayList<>(ids);
            return this;
        }

        public PollOption build() {
            if (option.optionId == null || option.optionId.isEmpty()) {
                option.optionId = "opt_" + System.currentTimeMillis();
            }
            return option;
        }
    }

    /**
     * 添加投票
     */
    public synchronized boolean addVote(String userId) {
        if (!voterIds.contains(userId)) {
            voterIds.add(userId);
            voteCount++;
            return true;
        }
        return false;
    }

    /**
     * 移除投票
     */
    public synchronized boolean removeVote(String userId) {
        if (voterIds.remove(userId)) {
            voteCount--;
            return true;
        }
        return false;
    }

    /**
     * 检查用户是否已投票
     */
    public boolean hasVoted(String userId) {
        return voterIds.contains(userId);
    }

    /**
     * 获取投票者ID列表的副本（保护性复制）
     */
    public List<String> getVoterIdsSnapshot() {
        return new ArrayList<>(voterIds);
    }

    // ==================== Getters & Setters ====================

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public int getOptionIndex() {
        return optionIndex;
    }

    public void setOptionIndex(int optionIndex) {
        this.optionIndex = optionIndex;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public List<String> getVoterIds() {
        return voterIds;
    }

    public void setVoterIds(List<String> voterIds) {
        this.voterIds = voterIds;
    }

    @Override
    public String toString() {
        return "PollOption{" +
            "optionId='" + optionId + '\'' +
            ", index=" + optionIndex +
            ", text='" + optionText + '\'' +
            ", votes=" + voteCount +
            '}';
    }
}
