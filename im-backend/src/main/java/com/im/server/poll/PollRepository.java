package com.im.server.poll;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 投票仓储层（内存存储）
 * 生产环境需替换为MySQL/PostgreSQL存储
 */
public class PollRepository {

    // 投票存储: pollId -> Poll
    private final Map<String, Poll> pollStore = new ConcurrentHashMap<>();

    // 投票记录存储: pollId -> List<PollVote>
    private final Map<String, List<PollVote>> voteStore = new ConcurrentHashMap<>();

    // 群组投票索引: groupId -> List<pollId>
    private final Map<String, List<String>> groupPollIndex = new ConcurrentHashMap<>();

    // 唯一索引: pollId:userId:optionId -> voteId (防重复投票)
    private final Map<String, String> uniqueVoteIndex = new ConcurrentHashMap<>();

    // ==================== 投票 CRUD ====================

    /**
     * 保存投票
     */
    public Poll save(Poll poll) {
        pollStore.put(poll.getPollId(), poll);
        // 更新群组索引
        groupPollIndex.computeIfAbsent(poll.getGroupId(), k -> new ArrayList<>())
            .add(0, poll.getPollId()); // 最新在前
        return poll;
    }

    /**
     * 根据ID获取投票
     */
    public Optional<Poll> findById(String pollId) {
        return Optional.ofNullable(pollStore.get(pollId));
    }

    /**
     * 根据ID获取投票（不存在抛异常）
     */
    public Poll getById(String pollId) {
        Poll poll = pollStore.get(pollId);
        if (poll == null) {
            throw new PollException("Poll not found: " + pollId);
        }
        return poll;
    }

    /**
     * 更新投票
     */
    public Poll update(Poll poll) {
        if (!pollStore.containsKey(poll.getPollId())) {
            throw new PollException("Poll not found for update: " + poll.getPollId());
        }
        pollStore.put(poll.getPollId(), poll);
        return poll;
    }

    /**
     * 删除投票
     */
    public boolean deleteById(String pollId) {
        Poll poll = pollStore.remove(pollId);
        if (poll != null) {
            // 清理投票记录
            voteStore.remove(pollId);
            // 清理群组索引
            List<String> groupPolls = groupPollIndex.get(poll.getGroupId());
            if (groupPolls != null) {
                groupPolls.remove(pollId);
            }
            return true;
        }
        return false;
    }

    // ==================== 投票查询 ====================

    /**
     * 获取群组所有投票
     */
    public List<Poll> findByGroupId(String groupId) {
        List<String> pollIds = groupPollIndex.getOrDefault(groupId, new ArrayList<>());
        return pollIds.stream()
            .map(pollStore::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 获取群组进行中的投票
     */
    public List<Poll> findActiveByGroupId(String groupId) {
        return findByGroupId(groupId).stream()
            .filter(Poll::isActive)
            .collect(Collectors.toList());
    }

    /**
     * 获取用户创建的投票
     */
    public List<Poll> findByCreatorId(String creatorId) {
        return pollStore.values().stream()
            .filter(p -> creatorId.equals(p.getCreatorId()))
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(Collectors.toList());
    }

    /**
     * 获取用户参与的投票
     */
    public List<Poll> findVotedByUserId(String userId) {
        return pollStore.values().stream()
            .filter(p -> p.hasUserVoted(userId))
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(Collectors.toList());
    }

    /**
     * 统计群组投票数
     */
    public int countByGroupId(String groupId) {
        return groupPollIndex.getOrDefault(groupId, new ArrayList<>()).size();
    }

    // ==================== 投票记录 ====================

    /**
     * 保存投票记录
     * @return true 新增, false 已存在
     */
    public boolean saveVote(PollVote vote) {
        String key = vote.getUniqueKey();
        String existing = uniqueVoteIndex.putIfAbsent(key, vote.getVoteId());
        if (existing != null) {
            return false; // 重复投票
        }
        voteStore.computeIfAbsent(vote.getPollId(), k -> new ArrayList<>()).add(vote);
        return true;
    }

    /**
     * 删除投票记录
     */
    public boolean deleteVote(String pollId, String userId, String optionId) {
        String key = pollId + ":" + userId + ":" + optionId;
        String removed = uniqueVoteIndex.remove(key);
        if (removed != null) {
            List<PollVote> votes = voteStore.get(pollId);
            if (votes != null) {
                votes.removeIf(v -> v.getUserId().equals(userId) && v.getOptionId().equals(optionId));
            }
            return true;
        }
        return false;
    }

    /**
     * 检查用户是否对某选项投过票
     */
    public boolean hasUserVoted(String pollId, String userId, String optionId) {
        String key = pollId + ":" + userId + ":" + optionId;
        return uniqueVoteIndex.containsKey(key);
    }

    /**
     * 获取用户对某投票的所有投票记录
     */
    public List<PollVote> findVotesByUser(String pollId, String userId) {
        List<PollVote> votes = voteStore.getOrDefault(pollId, new ArrayList<>());
        return votes.stream()
            .filter(v -> userId.equals(v.getUserId()))
            .collect(Collectors.toList());
    }

    /**
     * 获取投票的所有记录
     */
    public List<PollVote> findVotesByPollId(String pollId) {
        return new ArrayList<>(voteStore.getOrDefault(pollId, new ArrayList<>()));
    }

    // ==================== 统计查询 ====================

    /**
     * 获取投票的选项投票数
     */
    public Map<String, Integer> getOptionVoteCounts(String pollId) {
        List<PollVote> votes = voteStore.getOrDefault(pollId, new ArrayList<>());
        Map<String, Integer> counts = new HashMap<>();
        for (PollVote vote : votes) {
            counts.merge(vote.getOptionId(), 1, Integer::sum);
        }
        return counts;
    }

    /**
     * 获取投票的总投票数和总参与人数
     */
    public int[] getVoteStats(String pollId) {
        List<PollVote> votes = voteStore.getOrDefault(pollId, new ArrayList<>());
        int totalVotes = votes.size();
        long totalParticipants = votes.stream()
            .map(PollVote::getUserId)
            .distinct()
            .count();
        return new int[]{totalVotes, (int) totalParticipants};
    }

    // ==================== 工具方法 ====================

    /**
     * 清理过期投票记录
     */
    public void cleanupExpiredVotes(int maxAgeHours) {
        long cutoff = System.currentTimeMillis() - (maxAgeHours * 3600L * 1000L);
        voteStore.values().forEach(votes -> {
            votes.removeIf(v -> {
                try {
                    long voteTime = v.getVotedAt().toInstant(java.time.ZoneOffset.of("+8"))
                        .toEpochMilli();
                    return voteTime < cutoff;
                } catch (Exception e) {
                    return true;
                }
            });
        });
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPolls", pollStore.size());
        stats.put("totalVotes", voteStore.values().stream().mapToInt(List::size).sum());
        stats.put("activePolls", pollStore.values().stream().filter(Poll::isActive).count());
        stats.put("groupCount", groupPollIndex.size());
        return stats;
    }

    /**
     * 清空所有数据（测试用）
     */
    public void clear() {
        pollStore.clear();
        voteStore.clear();
        groupPollIndex.clear();
        uniqueVoteIndex.clear();
    }
}
