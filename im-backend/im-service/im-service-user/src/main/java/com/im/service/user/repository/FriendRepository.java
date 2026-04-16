package com.im.service.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.im.service.user.entity.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 好友关系数据访问接口 - MyBatis-Plus
 * 对应数据库表: im_friend
 * 
 * @author IM Team
 * @version 1.0
 */
@Mapper
public interface FriendRepository extends BaseMapper<Friend> {

    // ========== 基础查询 ==========

    /**
     * 根据用户ID和好友ID查询关系
     */
    default Friend findByUserIdAndFriendId(Long userId, Long friendId) {
        return selectOne(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("friend_id", friendId)
                .eq("deleted", false));
    }

    /**
     * 查询双向好友关系
     */
    default Optional<Friend> findActiveFriendship(Long userId, Long friendId) {
        return Optional.ofNullable(selectOne(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("friend_id", friendId)
                .eq("status", "ACCEPTED")
                .eq("deleted", false)));
    }

    /**
     * 查询是否存在好友关系
     */
    default boolean existsFriendship(Long userId, Long friendId) {
        return selectCount(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("friend_id", friendId)
                .eq("deleted", false)) > 0;
    }

    /**
     * 查询是否为双向好友
     */
    default boolean areFriends(Long userId, Long friendId) {
        return selectCount(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("friend_id", friendId)
                .eq("status", "ACCEPTED")
                .eq("deleted", false)) > 0;
    }

    // ========== 好友列表查询 ==========

    /**
     * 查询用户的好友列表（已接受）
     */
    default List<Friend> findFriendsByUserId(Long userId) {
        return selectList(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("status", "ACCEPTED")
                .eq("deleted", false)
                .orderByDesc("became_friends_at"));
    }

    /**
     * 查询用户的好友列表（带星标优先排序）
     */
    default List<Friend> findFriendsWithStarPriority(Long userId) {
        return selectList(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("status", "ACCEPTED")
                .eq("deleted", false)
                .orderByDesc("starred")
                .orderByDesc("became_friends_at"));
    }

    /**
     * 查询置顶的好友
     */
    default List<Friend> findPinnedFriends(Long userId) {
        return selectList(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("pinned", true)
                .eq("status", "ACCEPTED")
                .eq("deleted", false)
                .orderByDesc("pinned_at"));
    }

    /**
     * 查询星标好友
     */
    default List<Friend> findStarredFriends(Long userId) {
        return selectList(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("starred", true)
                .eq("status", "ACCEPTED")
                .eq("deleted", false)
                .orderByDesc("starred_at"));
    }

    // ========== 待处理申请查询 ==========

    /**
     * 查询待处理的好友申请（我发出的）
     */
    default List<Friend> findPendingSentRequests(Long userId) {
        return selectList(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("status", "PENDING")
                .eq("deleted", false)
                .orderByDesc("created_at"));
    }

    /**
     * 查询待处理的好友申请（我收到的）
     */
    default List<Friend> findPendingReceivedRequests(Long userId) {
        return selectList(new QueryWrapper<Friend>()
                .eq("friend_id", userId)
                .eq("status", "PENDING")
                .eq("deleted", false)
                .orderByDesc("created_at"));
    }

    /**
     * 查询所有待处理申请（包括发出和收到）
     */
    @Select("SELECT * FROM im_friend WHERE (user_id = #{userId} OR friend_id = #{userId}) " +
            "AND status = 'PENDING' AND deleted = 0 ORDER BY created_at DESC")
    List<Friend> findAllPendingRequests(@Param("userId") Long userId);

    // ========== 黑名单查询 ==========

    /**
     * 查询已屏蔽的好友
     */
    default List<Friend> findBlockedFriends(Long userId) {
        return selectList(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("blocked", true)
                .eq("deleted", false)
                .orderByDesc("blocked_at"));
    }

    /**
     * 查询屏蔽我的用户
     */
    default List<Friend> findBlockedByOthers(Long userId) {
        return selectList(new QueryWrapper<Friend>()
                .eq("friend_id", userId)
                .eq("blocked", true)
                .eq("deleted", false));
    }

    /**
     * 检查是否被屏蔽
     */
    default boolean isBlocked(Long userId, Long friendId) {
        return selectCount(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("friend_id", friendId)
                .eq("blocked", true)
                .eq("deleted", false)) > 0;
    }

    // ========== 状态更新方法 ==========

    /**
     * 接受好友申请
     */
    @Update("UPDATE im_friend SET status = 'ACCEPTED', became_friends_at = NOW(), updated_at = NOW() " +
            "WHERE id = #{id}")
    int acceptFriendRequest(@Param("id") Long id);

    /**
     * 拒绝好友申请
     */
    @Update("UPDATE im_friend SET status = 'REJECTED', reject_reason = #{reason}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int rejectFriendRequest(@Param("id") Long id, @Param("reason") String reason);

    /**
     * 更新好友备注
     */
    @Update("UPDATE im_friend SET remark = #{remark}, updated_at = NOW() WHERE id = #{id}")
    int updateRemark(@Param("id") Long id, @Param("remark") String remark);

    /**
     * 星标好友
     */
    @Update("UPDATE im_friend SET starred = 1, starred_at = NOW(), updated_at = NOW() WHERE id = #{id}")
    int starFriend(@Param("id") Long id);

    /**
     * 取消星标
     */
    @Update("UPDATE im_friend SET starred = 0, starred_at = NULL, updated_at = NOW() WHERE id = #{id}")
    int unstarFriend(@Param("id") Long id);

    /**
     * 置顶聊天
     */
    @Update("UPDATE im_friend SET pinned = 1, pinned_at = NOW(), updated_at = NOW() WHERE id = #{id}")
    int pinChat(@Param("id") Long id);

    /**
     * 取消置顶
     */
    @Update("UPDATE im_friend SET pinned = 0, pinned_at = NULL, updated_at = NOW() WHERE id = #{id}")
    int unpinChat(@Param("id") Long id);

    /**
     * 屏蔽好友
     */
    @Update("UPDATE im_friend SET blocked = 1, blocked_at = NOW(), status = 'BLOCKED', updated_at = NOW() WHERE id = #{id}")
    int blockFriend(@Param("id") Long id);

    /**
     * 取消屏蔽
     */
    @Update("UPDATE im_friend SET blocked = 0, blocked_at = NULL, status = 'ACCEPTED', updated_at = NOW() WHERE id = #{id}")
    int unblockFriend(@Param("id") Long id);

    /**
     * 更新最后聊天时间
     */
    @Update("UPDATE im_friend SET last_chat_at = NOW(), updated_at = NOW() WHERE id = #{id}")
    int updateLastChatAt(@Param("id") Long id);

    /**
     * 切换消息免打扰
     */
    @Update("UPDATE im_friend SET mute_notifications = #{mute}, updated_at = NOW() WHERE id = #{id}")
    int updateMuteNotifications(@Param("id") Long id, @Param("mute") Boolean mute);

    /**
     * 更新好友标签
     */
    @Update("UPDATE im_friend SET tags = #{tags}, updated_at = NOW() WHERE id = #{id}")
    int updateTags(@Param("id") Long id, @Param("tags") String tags);

    // ========== 删除相关 ==========

    /**
     * 逻辑删除好友关系
     */
    default int softDelete(Long id) {
        Friend friend = new Friend();
        friend.setStatus("DELETED");
        friend.setDeleted(true);
        return updateById(friend);
    }

    /**
     * 物理删除好友关系
     */
    default int physicalDelete(Long userId, Long friendId) {
        return delete(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("friend_id", friendId));
    }

    // ========== 统计方法 ==========

    /**
     * 统计用户好友数量
     */
    default long countFriends(Long userId) {
        return selectCount(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("status", "ACCEPTED")
                .eq("deleted", false));
    }

    /**
     * 统计待处理申请数量（收到）
     */
    default long countPendingReceived(Long userId) {
        return selectCount(new QueryWrapper<Friend>()
                .eq("friend_id", userId)
                .eq("status", "PENDING")
                .eq("deleted", false));
    }

    /**
     * 统计星标好友数量
     */
    default long countStarredFriends(Long userId) {
        return selectCount(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("starred", true)
                .eq("deleted", false));
    }

    /**
     * 统计屏蔽数量
     */
    default long countBlocked(Long userId) {
        return selectCount(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .eq("blocked", true)
                .eq("deleted", false));
    }

    // ========== 批量操作 ==========

    /**
     * 根据好友ID列表查询关系
     */
    default List<Friend> findByFriendIds(Long userId, List<Long> friendIds) {
        if (friendIds == null || friendIds.isEmpty()) {
            return List.of();
        }
        return selectList(new QueryWrapper<Friend>()
                .eq("user_id", userId)
                .in("friend_id", friendIds)
                .eq("status", "ACCEPTED")
                .eq("deleted", false));
    }
}
