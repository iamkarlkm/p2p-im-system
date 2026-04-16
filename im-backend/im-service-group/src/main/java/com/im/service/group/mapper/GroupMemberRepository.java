package com.im.service.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.service.group.entity.GroupMember;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 群组成员数据访问层
 * 提供群组成员相关的数据库操作
 *
 * @author IM System
 * @since 1.0.0
 */
@Mapper
public interface GroupMemberRepository extends BaseMapper<GroupMember> {

    // ==================== 基础查询 ====================

    /**
     * 根据群组ID和用户ID查询成员
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return 成员信息
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND user_id = #{userId} AND is_deleted = 0")
    GroupMember findByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 查询群组的正常状态成员
     *
     * @param groupId 群组ID
     * @return 成员列表
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND status = 0 AND is_deleted = 0 ORDER BY role DESC, create_time ASC")
    List<GroupMember> findActiveMembersByGroupId(@Param("groupId") Long groupId);

    /**
     * 分页查询群组成员
     *
     * @param page    分页参数
     * @param groupId 群组ID
     * @return 分页结果
     */
    IPage<GroupMember> findByGroupIdPage(Page<GroupMember> page, @Param("groupId") Long groupId);

    /**
     * 查询群组成员总数（包含已退出的）
     *
     * @param groupId 群组ID
     * @return 成员列表
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<GroupMember> findAllByGroupId(@Param("groupId") Long groupId);

    // ==================== 用户相关查询 ====================

    /**
     * 查询用户加入的所有群组
     *
     * @param userId 用户ID
     * @return 成员列表
     */
    @Select("SELECT * FROM im_group_member WHERE user_id = #{userId} AND status = 0 AND is_deleted = 0 ORDER BY create_time DESC")
    List<GroupMember> findByUserId(@Param("userId") Long userId);

    /**
     * 分页查询用户加入的群组
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<GroupMember> findByUserIdPage(Page<GroupMember> page, @Param("userId") Long userId);

    /**
     * 查询用户加入的所有群组ID
     *
     * @param userId 用户ID
     * @return 群组ID列表
     */
    @Select("SELECT group_id FROM im_group_member WHERE user_id = #{userId} AND status = 0 AND is_deleted = 0")
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);

    // ==================== 角色相关查询 ====================

    /**
     * 查询群组的管理员和群主
     *
     * @param groupId 群组ID
     * @return 成员列表
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND role IN (1, 2) AND status = 0 AND is_deleted = 0 ORDER BY role DESC")
    List<GroupMember> findAdminsByGroupId(@Param("groupId") Long groupId);

    /**
     * 查询群组的群主
     *
     * @param groupId 群组ID
     * @return 群主信息
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND role = 2 AND status = 0 AND is_deleted = 0")
    GroupMember findOwnerByGroupId(@Param("groupId") Long groupId);

    /**
     * 查询所有管理员（包括群主）
     *
     * @param groupId 群组ID
     * @return 成员列表
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND role IN (1, 2) AND status = 0 AND is_deleted = 0")
    List<GroupMember> findAllAdmins(@Param("groupId") Long groupId);

    /**
     * 查询普通成员
     *
     * @param groupId 群组ID
     * @return 成员列表
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND role = 0 AND status = 0 AND is_deleted = 0 ORDER BY create_time ASC")
    List<GroupMember> findNormalMembers(@Param("groupId") Long groupId);

    // ==================== 禁言相关查询 ====================

    /**
     * 查询被禁言的成员
     *
     * @param groupId 群组ID
     * @return 成员列表
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND is_muted = 1 AND status = 0 AND is_deleted = 0")
    List<GroupMember> findMutedMembers(@Param("groupId") Long groupId);

    /**
     * 查询禁言已过期的成员
     *
     * @param now 当前时间
     * @return 成员列表
     */
    @Select("SELECT * FROM im_group_member WHERE is_muted = 1 AND mute_end_time IS NOT NULL AND mute_end_time <= #{now} AND is_deleted = 0")
    List<GroupMember> findExpiredMutedMembers(@Param("now") LocalDateTime now);

    // ==================== 存在性检查 ====================

    /**
     * 检查用户是否在群中（正常状态）
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return true-在群中，false-不在
     */
    @Select("SELECT COUNT(*) > 0 FROM im_group_member WHERE group_id = #{groupId} AND user_id = #{userId} AND status = 0 AND is_deleted = 0")
    boolean existsByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 检查用户是否是群主
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return true-是群主，false-不是
     */
    @Select("SELECT COUNT(*) > 0 FROM im_group_member WHERE group_id = #{groupId} AND user_id = #{userId} AND role = 2 AND status = 0 AND is_deleted = 0")
    boolean isOwner(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 检查用户是否是管理员或以上
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return true-是管理员或群主，false-不是
     */
    @Select("SELECT COUNT(*) > 0 FROM im_group_member WHERE group_id = #{groupId} AND user_id = #{userId} AND role IN (1, 2) AND status = 0 AND is_deleted = 0")
    boolean isAdminOrAbove(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 检查用户是否被禁言
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return true-被禁言，false-未被禁言
     */
    @Select("SELECT COUNT(*) > 0 FROM im_group_member WHERE group_id = #{groupId} AND user_id = #{userId} AND is_muted = 1 AND status = 0 AND is_deleted = 0 " +
            "AND (mute_end_time IS NULL OR mute_end_time > NOW())")
    boolean isMuted(@Param("groupId") Long groupId, @Param("userId") Long userId);

    // ==================== 更新操作 ====================

    /**
     * 更新成员角色
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @param role    角色：0-成员，1-管理员，2-群主
     * @return 影响行数
     */
    @Update("UPDATE im_group_member SET role = #{role}, update_time = NOW() WHERE group_id = #{groupId} AND user_id = #{userId} AND is_deleted = 0")
    int updateRole(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("role") Integer role);

    /**
     * 更新群内昵称
     *
     * @param groupId  群组ID
     * @param userId   用户ID
     * @param nickname 昵称
     * @return 影响行数
     */
    @Update("UPDATE im_group_member SET nickname = #{nickname}, update_time = NOW() WHERE group_id = #{groupId} AND user_id = #{userId} AND is_deleted = 0")
    int updateNickname(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("nickname") String nickname);

    /**
     * 禁言成员
     *
     * @param groupId       群组ID
     * @param userId        用户ID
     * @param muteEndTime   禁言结束时间
     * @param reason        禁言原因
     * @param operatorId    操作者ID
     * @return 影响行数
     */
    @Update("UPDATE im_group_member SET is_muted = 1, mute_end_time = #{muteEndTime}, mute_reason = #{reason}, muted_by = #{operatorId}, update_time = NOW() " +
            "WHERE group_id = #{groupId} AND user_id = #{userId} AND is_deleted = 0")
    int muteMember(@Param("groupId") Long groupId, @Param("userId") Long userId,
                   @Param("muteEndTime") LocalDateTime muteEndTime,
                   @Param("reason") String reason, @Param("operatorId") Long operatorId);

    /**
     * 解除禁言
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return 影响行数
     */
    @Update("UPDATE im_group_member SET is_muted = 0, mute_end_time = NULL, mute_reason = NULL, muted_by = NULL, update_time = NOW() " +
            "WHERE group_id = #{groupId} AND user_id = #{userId} AND is_deleted = 0")
    int unmuteMember(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 设置消息免打扰
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @param mute    true-开启免打扰，false-关闭免打扰
     * @return 影响行数
     */
    @Update("UPDATE im_group_member SET mute_notifications = #{mute}, update_time = NOW() WHERE group_id = #{groupId} AND user_id = #{userId} AND is_deleted = 0")
    int setMuteNotifications(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("mute") Boolean mute);

    /**
     * 置顶/取消置顶群聊
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @param pinned  true-置顶，false-取消置顶
     * @return 影响行数
     */
    @Update("UPDATE im_group_member SET is_pinned = #{pinned}, update_time = NOW() WHERE group_id = #{groupId} AND user_id = #{userId} AND is_deleted = 0")
    int setPinned(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("pinned") Boolean pinned);

    /**
     * 标记成员为退出状态
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return 影响行数
     */
    @Update("UPDATE im_group_member SET status = 1, leave_time = NOW(), update_time = NOW() WHERE group_id = #{groupId} AND user_id = #{userId} AND is_deleted = 0")
    int markAsLeft(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 标记成员为被移除状态
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return 影响行数
     */
    @Update("UPDATE im_group_member SET status = 2, leave_time = NOW(), update_time = NOW() WHERE group_id = #{groupId} AND user_id = #{userId} AND is_deleted = 0")
    int markAsRemoved(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 更新最后发言时间
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return 影响行数
     */
    @Update("UPDATE im_group_member SET last_speak_time = NOW(), update_time = NOW() WHERE group_id = #{groupId} AND user_id = #{userId} AND is_deleted = 0")
    int updateLastSpeakTime(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 批量解除禁言（用于清理过期禁言）
     *
     * @param now 当前时间
     * @return 影响行数
     */
    @Update("UPDATE im_group_member SET is_muted = 0, mute_end_time = NULL, mute_reason = NULL, muted_by = NULL, update_time = NOW() " +
            "WHERE is_muted = 1 AND mute_end_time IS NOT NULL AND mute_end_time <= #{now}")
    int batchUnmuteExpired(@Param("now") LocalDateTime now);

    // ==================== 统计方法 ====================

    /**
     * 统计群组成员数量
     *
     * @param groupId 群组ID
     * @return 成员数量
     */
    @Select("SELECT COUNT(*) FROM im_group_member WHERE group_id = #{groupId} AND status = 0 AND is_deleted = 0")
    Long countActiveMembers(@Param("groupId") Long groupId);

    /**
     * 统计群组管理员数量
     *
     * @param groupId 群组ID
     * @return 管理员数量
     */
    @Select("SELECT COUNT(*) FROM im_group_member WHERE group_id = #{groupId} AND role = 1 AND status = 0 AND is_deleted = 0")
    Long countAdmins(@Param("groupId") Long groupId);

    /**
     * 统计被禁言的成员数量
     *
     * @param groupId 群组ID
     * @return 禁言成员数量
     */
    @Select("SELECT COUNT(*) FROM im_group_member WHERE group_id = #{groupId} AND is_muted = 1 AND status = 0 AND is_deleted = 0 " +
            "AND (mute_end_time IS NULL OR mute_end_time > NOW())")
    Long countMutedMembers(@Param("groupId") Long groupId);

    /**
     * 统计用户加入的群组数量
     *
     * @param userId 用户ID
     * @return 群组数量
     */
    @Select("SELECT COUNT(*) FROM im_group_member WHERE user_id = #{userId} AND status = 0 AND is_deleted = 0")
    Long countGroupsByUserId(@Param("userId") Long userId);

    // ==================== 批量操作 ====================

    /**
     * 批量查询成员
     *
     * @param groupId 群组ID
     * @param userIds 用户ID列表
     * @return 成员列表
     */
    List<GroupMember> findByGroupIdAndUserIds(@Param("groupId") Long groupId, @Param("userIds") List<Long> userIds);

    /**
     * 批量移除成员
     *
     * @param groupId 群组ID
     * @param userIds 用户ID列表
     * @return 影响行数
     */
    int batchRemoveMembers(@Param("groupId") Long groupId, @Param("userIds") List<Long> userIds);

    /**
     * 查询在线成员（需要根据用户在线状态表关联查询）
     *
     * @param groupId 群组ID
     * @return 成员列表
     */
    @Select("SELECT m.* FROM im_group_member m INNER JOIN im_user_online_status u ON m.user_id = u.user_id " +
            "WHERE m.group_id = #{groupId} AND m.status = 0 AND m.is_deleted = 0 AND u.is_online = 1")
    List<GroupMember> findOnlineMembers(@Param("groupId") Long groupId);
}
