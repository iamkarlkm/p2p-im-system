package com.im.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.entity.Group;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 群组数据访问层
 * 功能 #5: 群组管理基础模块
 */
@Mapper
public interface GroupMapper extends BaseMapper<Group> {

    /**
     * 根据群组ID查询群组
     */
    @Select("SELECT * FROM im_group WHERE group_id = #{groupId} AND deleted = 0")
    Group selectByGroupId(String groupId);

    /**
     * 查询用户创建的所有群组
     */
    @Select("SELECT * FROM im_group WHERE owner_id = #{ownerId} AND deleted = 0 ORDER BY created_at DESC")
    List<Group> selectByOwnerId(Long ownerId);

    /**
     * 查询用户加入的所有群组(通过关联表)
     */
    @Select("SELECT g.* FROM im_group g " +
            "INNER JOIN im_group_member m ON g.group_id = m.group_id " +
            "WHERE m.user_id = #{userId} AND m.status = 0 AND g.deleted = 0 AND m.deleted = 0 " +
            "ORDER BY g.created_at DESC")
    List<Group> selectJoinedGroups(Long userId);

    /**
     * 根据群组名称搜索群组
     */
    @Select("SELECT * FROM im_group WHERE name LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0 LIMIT #{limit}")
    List<Group> searchByName(@Param("keyword") String keyword, @Param("limit") Integer limit);

    /**
     * 更新群组成员数
     */
    @Update("UPDATE im_group SET member_count = #{count}, updated_at = NOW() WHERE group_id = #{groupId}")
    int updateMemberCount(@Param("groupId") String groupId, @Param("count") Integer count);

    /**
     * 增加成员数
     */
    @Update("UPDATE im_group SET member_count = member_count + 1, updated_at = NOW() WHERE group_id = #{groupId}")
    int incrementMemberCount(String groupId);

    /**
     * 减少成员数
     */
    @Update("UPDATE im_group SET member_count = member_count - 1, updated_at = NOW() WHERE group_id = #{groupId}")
    int decrementMemberCount(String groupId);

    /**
     * 更新群组公告
     */
    @Update("UPDATE im_group SET announcement = #{announcement}, updated_at = NOW() WHERE group_id = #{groupId}")
    int updateAnnouncement(@Param("groupId") String groupId, @Param("announcement") String announcement);

    /**
     * 更新群组信息
     */
    @Update("UPDATE im_group SET name = #{name}, description = #{description}, avatar = #{avatar}, " +
            "join_type = #{joinType}, all_muted = #{allMuted}, updated_at = NOW() WHERE group_id = #{groupId}")
    int updateGroupInfo(Group group);

    /**
     * 解散群组(软删除)
     */
    @Update("UPDATE im_group SET status = 1, deleted = 1, updated_at = NOW() WHERE group_id = #{groupId}")
    int dissolveGroup(String groupId);

    /**
     * 转让群主
     */
    @Update("UPDATE im_group SET owner_id = #{newOwnerId}, updated_at = NOW() WHERE group_id = #{groupId}")
    int transferOwnership(@Param("groupId") String groupId, @Param("newOwnerId") Long newOwnerId);

    /**
     * 统计群组总数
     */
    @Select("SELECT COUNT(*) FROM im_group WHERE deleted = 0")
    Long countTotalGroups();

    /**
     * 统计用户创建的群组数
     */
    @Select("SELECT COUNT(*) FROM im_group WHERE owner_id = #{userId} AND deleted = 0")
    Long countGroupsByOwner(Long userId);

    /**
     * 获取热门群组(按成员数排序)
     */
    @Select("SELECT * FROM im_group WHERE deleted = 0 ORDER BY member_count DESC LIMIT #{limit}")
    List<Group> selectPopularGroups(@Param("limit") Integer limit);

    /**
     * 检查群组名称是否存在
     */
    @Select("SELECT COUNT(*) FROM im_group WHERE name = #{name} AND deleted = 0")
    Long countByName(String name);
}
