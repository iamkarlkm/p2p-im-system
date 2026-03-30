package com.im.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.entity.GroupMember;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 群组成员数据访问层
 * 功能 #5: 群组管理基础模块 - 成员管理
 */
@Mapper
public interface GroupMemberMapper extends BaseMapper<GroupMember> {

    /**
     * 根据群组ID查询所有成员
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND deleted = 0")
    List<GroupMember> selectByGroupId(String groupId);

    /**
     * 查询群组的正常状态成员
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND status = 0 AND deleted = 0")
    List<GroupMember> selectActiveMembers(String groupId);

    /**
     * 根据群组ID和用户ID查询成员
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND user_id = #{userId} AND deleted = 0")
    GroupMember selectByGroupIdAndUserId(@Param("groupId") String groupId, @Param("userId") Long userId);

    /**
     * 查询用户加入的所有群组的成员记录
     */
    @Select("SELECT * FROM im_group_member WHERE user_id = #{userId} AND status = 0 AND deleted = 0")
    List<GroupMember> selectByUserId(Long userId);

    /**
     * 查询群组的管理员列表(包括群主)
     */
    @Select("SELECT * FROM im_group_member WHERE group_id = #{groupId} AND role IN (1, 2) AND status = 0 AND deleted = 0")
    List<GroupMember> selectAdmins(String groupId);

    /**
     * 更新成员角色
     */
    @Update("UPDATE im_group_member SET role = #{role}, updated_at = NOW() WHERE group_id = #{groupId} AND user_id = #{userId}")
    int updateRole(@Param("groupId") String groupId, @Param("userId") Long userId, @Param("role") Integer role);

    /**
     * 设置管理员
     */
    @Update("UPDATE im_group_member SET role = 1, updated_at = NOW() WHERE group_id = #{groupId} AND user_id = #{userId}")
    int setAdmin(@Param("groupId") String groupId, @Param("userId") Long userId);

    /**
     * 取消管理员
     */
    @Update("UPDATE im_group_member SET role = 0, updated_at = NOW() WHERE group_id = #{groupId} AND user_id = #{userId}")
    int unsetAdmin(@Param("groupId") String groupId, @Param("userId") Long userId);

    /**
     * 设置禁言
     */
    @Update("UPDATE im_group_member SET muted = 1, mute_until = #{muteUntil}, updated_at = NOW() " +
            "WHERE group_id = #{groupId} AND user_id = #{userId}")
    int setMute(@Param("groupId") String groupId, @Param("userId") Long userId, @Param("muteUntil") LocalDateTime muteUntil);

    /**
     * 解除禁言
     */
    @Update("UPDATE im_group_member SET muted = 0, mute_until = NULL, updated_at = NOW() " +
            "WHERE group_id = #{groupId} AND user_id = #{userId}")
    int unsetMute(@Param("groupId") String groupId, @Param("userId") Long userId);

    /**
     * 更新成员状态(退群/踢出)
     */
    @Update("UPDATE im_group_member SET status = #{status}, updated_at = NOW() WHERE group_id = #{groupId} AND user_id = #{userId}")
    int updateStatus(@Param("groupId") String groupId, @Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 踢出成员(更新状态为已踢出)
     */
    @Update("UPDATE im_group_member SET status = 2, updated_at = NOW() WHERE group_id = #{groupId} AND user_id = #{userId}")
    int kickMember(@Param("groupId") String groupId, @Param("userId") Long userId);

    /**
     * 成员退群
     */
    @Update("UPDATE im_group_member SET status = 1, updated_at = NOW() WHERE group_id = #{groupId} AND user_id = #{userId}")
    int quitGroup(@Param("groupId") String groupId, @Param("userId") Long userId);

    /**
     * 更新群昵称
     */
    @Update("UPDATE im_group_member SET group_nickname = #{nickname}, updated_at = NOW() WHERE group_id = #{groupId} AND user_id = #{userId}")
    int updateNickname(@Param("groupId") String groupId, @Param("userId") Long userId, @Param("nickname") String nickname);

    /**
     * 更新最后活跃时间
     */
    @Update("UPDATE im_group_member SET last_active_at = NOW() WHERE group_id = #{groupId} AND user_id = #{userId}")
    int updateLastActiveTime(@Param("groupId") String groupId, @Param("userId") Long userId);

    /**
     * 统计群组成员数
     */
    @Select("SELECT COUNT(*) FROM im_group_member WHERE group_id = #{groupId} AND status = 0 AND deleted = 0")
    Long countActiveMembers(String groupId);

    /**
     * 检查用户是否在群组中
     */
    @Select("SELECT COUNT(*) FROM im_group_member WHERE group_id = #{groupId} AND user_id = #{userId} AND status = 0 AND deleted = 0")
    Long checkUserInGroup(@Param("groupId") String groupId, @Param("userId") Long userId);

    /**
     * 删除群组的所有成员(解散群组时使用)
     */
    @Update("UPDATE im_group_member SET deleted = 1, status = 1, updated_at = NOW() WHERE group_id = #{groupId}")
    int deleteAllMembers(String groupId);
}
