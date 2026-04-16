package com.im.service.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.service.group.entity.Group;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 群组数据访问层
 * 提供群组相关的数据库操作
 *
 * @author IM System
 * @since 1.0.0
 */
@Mapper
public interface GroupRepository extends BaseMapper<Group> {

    // ==================== 基础查询 ====================

    /**
     * 根据ID查询群组（包含已解散的）
     *
     * @param id 群组ID
     * @return 群组信息
     */
    @Select("SELECT * FROM im_group WHERE id = #{id}")
    Group findByIdIncludeDissolved(Long id);

    /**
     * 查询用户创建的所有群组
     *
     * @param ownerId 群主ID
     * @return 群组列表
     */
    @Select("SELECT * FROM im_group WHERE owner_id = #{ownerId} AND status = 0 AND is_deleted = 0 ORDER BY create_time DESC")
    List<Group> findByOwnerId(Long ownerId);

    /**
     * 分页查询用户创建的群组
     *
     * @param page    分页参数
     * @param ownerId 群主ID
     * @return 分页结果
     */
    IPage<Group> findByOwnerIdPage(Page<Group> page, @Param("ownerId") Long ownerId);

    // ==================== 搜索查询 ====================

    /**
     * 根据群名称模糊搜索
     *
     * @param name 群名称关键字
     * @return 群组列表
     */
    @Select("SELECT * FROM im_group WHERE name LIKE CONCAT('%', #{name}, '%') AND status = 0 AND is_deleted = 0 ORDER BY member_count DESC")
    List<Group> searchByName(@Param("name") String name);

    /**
     * 分页搜索群组
     *
     * @param page 分页参数
     * @param name 群名称关键字
     * @return 分页结果
     */
    IPage<Group> searchByNamePage(Page<Group> page, @Param("name") String name);

    /**
     * 高级搜索群组
     *
     * @param page        分页参数
     * @param name        群名称关键字
     * @param type        群组类型
     * @param minMembers  最小成员数
     * @param maxMembers  最大成员数
     * @return 分页结果
     */
    IPage<Group> advancedSearch(Page<Group> page,
                                 @Param("name") String name,
                                 @Param("type") Integer type,
                                 @Param("minMembers") Integer minMembers,
                                 @Param("maxMembers") Integer maxMembers);

    // ==================== 状态查询 ====================

    /**
     * 查询所有正常状态的群组
     *
     * @return 群组列表
     */
    @Select("SELECT * FROM im_group WHERE status = 0 AND is_deleted = 0 ORDER BY create_time DESC")
    List<Group> findAllActive();

    /**
     * 查询已解散的群组
     *
     * @return 群组列表
     */
    @Select("SELECT * FROM im_group WHERE status = 1 AND is_deleted = 0 ORDER BY dissolve_time DESC")
    List<Group> findAllDissolved();

    /**
     * 查询某时间段内创建的群组
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 群组列表
     */
    @Select("SELECT * FROM im_group WHERE create_time BETWEEN #{startTime} AND #{endTime} AND is_deleted = 0 ORDER BY create_time DESC")
    List<Group> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // ==================== 成员数量查询 ====================

    /**
     * 查询成员数超过指定数量的群组
     *
     * @param count 成员数量
     * @return 群组列表
     */
    @Select("SELECT * FROM im_group WHERE member_count >= #{count} AND status = 0 AND is_deleted = 0 ORDER BY member_count DESC")
    List<Group> findByMemberCountGreaterThan(@Param("count") Integer count);

    /**
     * 查询成员数少于指定数量的群组
     *
     * @param count 成员数量
     * @return 群组列表
     */
    @Select("SELECT * FROM im_group WHERE member_count <= #{count} AND status = 0 AND is_deleted = 0 ORDER BY member_count ASC")
    List<Group> findByMemberCountLessThan(@Param("count") Integer count);

    // ==================== 更新操作 ====================

    /**
     * 更新群组信息
     *
     * @param id          群组ID
     * @param name        群组名称
     * @param avatar      群组头像
     * @param description 群组描述
     * @return 影响行数
     */
    @Update("UPDATE im_group SET name = #{name}, avatar = #{avatar}, description = #{description}, update_time = NOW() WHERE id = #{id}")
    int updateGroupInfo(@Param("id") Long id, @Param("name") String name, @Param("avatar") String avatar, @Param("description") String description);

    /**
     * 更新群公告
     *
     * @param id          群组ID
     * @param announcement 公告内容
     * @param publisherId 发布者ID
     * @return 影响行数
     */
    @Update("UPDATE im_group SET announcement = #{announcement}, announcement_publisher_id = #{publisherId}, announcement_time = NOW(), announcement_pinned = 1, update_time = NOW() WHERE id = #{id}")
    int updateAnnouncement(@Param("id") Long id, @Param("announcement") String announcement, @Param("publisherId") Long publisherId);

    /**
     * 清除群公告
     *
     * @param id 群组ID
     * @return 影响行数
     */
    @Update("UPDATE im_group SET announcement = NULL, announcement_publisher_id = NULL, announcement_time = NULL, announcement_pinned = 0, update_time = NOW() WHERE id = #{id}")
    int clearAnnouncement(@Param("id") Long id);

    /**
     * 更新群设置
     *
     * @param id                    群组ID
     * @param joinType              加入方式
     * @param speakPermission       发言权限
     * @param allowMemberInvite     允许成员邀请
     * @param allowMemberModifyName 允许成员修改群名
     * @param enableVerify          开启群验证
     * @return 影响行数
     */
    @Update("UPDATE im_group SET join_type = #{joinType}, speak_permission = #{speakPermission}, " +
            "allow_member_invite = #{allowMemberInvite}, allow_member_modify_name = #{allowMemberModifyName}, " +
            "enable_verify = #{enableVerify}, update_time = NOW() WHERE id = #{id}")
    int updateSettings(@Param("id") Long id,
                       @Param("joinType") Integer joinType,
                       @Param("speakPermission") Integer speakPermission,
                       @Param("allowMemberInvite") Boolean allowMemberInvite,
                       @Param("allowMemberModifyName") Boolean allowMemberModifyName,
                       @Param("enableVerify") Boolean enableVerify);

    /**
     * 更新成员数量
     *
     * @param id    群组ID
     * @param count 成员数量
     * @return 影响行数
     */
    @Update("UPDATE im_group SET member_count = #{count}, update_time = NOW() WHERE id = #{id}")
    int updateMemberCount(@Param("id") Long id, @Param("count") Integer count);

    /**
     * 增加成员数
     *
     * @param id 群组ID
     * @return 影响行数
     */
    @Update("UPDATE im_group SET member_count = member_count + 1, update_time = NOW() WHERE id = #{id}")
    int incrementMemberCount(@Param("id") Long id);

    /**
     * 减少成员数
     *
     * @param id 群组ID
     * @return 影响行数
     */
    @Update("UPDATE im_group SET member_count = member_count - 1, update_time = NOW() WHERE id = #{id} AND member_count > 0")
    int decrementMemberCount(@Param("id") Long id);

    /**
     * 全员禁言
     *
     * @param id              群组ID
     * @param muteEndTime     禁言结束时间
     * @return 影响行数
     */
    @Update("UPDATE im_group SET all_muted = 1, mute_end_time = #{muteEndTime}, update_time = NOW() WHERE id = #{id}")
    int muteAll(@Param("id") Long id, @Param("muteEndTime") LocalDateTime muteEndTime);

    /**
     * 取消全员禁言
     *
     * @param id 群组ID
     * @return 影响行数
     */
    @Update("UPDATE im_group SET all_muted = 0, mute_end_time = NULL, update_time = NOW() WHERE id = #{id}")
    int unmuteAll(@Param("id") Long id);

    /**
     * 解散群组
     *
     * @param id         群组ID
     * @param operatorId 操作者ID
     * @return 影响行数
     */
    @Update("UPDATE im_group SET status = 1, dissolve_time = NOW(), dissolve_by = #{operatorId}, update_time = NOW() WHERE id = #{id}")
    int dissolve(@Param("id") Long id, @Param("operatorId") Long operatorId);

    /**
     * 转让群主
     *
     * @param id       群组ID
     * @param newOwnerId 新群主ID
     * @return 影响行数
     */
    @Update("UPDATE im_group SET owner_id = #{newOwnerId}, update_time = NOW() WHERE id = #{id}")
    int transferOwnership(@Param("id") Long id, @Param("newOwnerId") Long newOwnerId);

    // ==================== 群主相关 ====================

    /**
     * 检查用户是否是群主
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return true-是群主，false-不是
     */
    @Select("SELECT COUNT(*) > 0 FROM im_group WHERE id = #{groupId} AND owner_id = #{userId} AND status = 0 AND is_deleted = 0")
    boolean isOwner(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 获取群主ID
     *
     * @param groupId 群组ID
     * @return 群主ID
     */
    @Select("SELECT owner_id FROM im_group WHERE id = #{groupId} AND is_deleted = 0")
    Long getOwnerId(@Param("groupId") Long groupId);

    // ==================== 统计方法 ====================

    /**
     * 统计群组总数
     *
     * @return 群组数量
     */
    @Select("SELECT COUNT(*) FROM im_group WHERE is_deleted = 0")
    Long countAll();

    /**
     * 统计正常状态的群组数量
     *
     * @return 群组数量
     */
    @Select("SELECT COUNT(*) FROM im_group WHERE status = 0 AND is_deleted = 0")
    Long countActive();

    /**
     * 统计已解散的群组数量
     *
     * @return 群组数量
     */
    @Select("SELECT COUNT(*) FROM im_group WHERE status = 1 AND is_deleted = 0")
    Long countDissolved();

    /**
     * 统计用户创建的群组数量
     *
     * @param ownerId 群主ID
     * @return 群组数量
     */
    @Select("SELECT COUNT(*) FROM im_group WHERE owner_id = #{ownerId} AND status = 0 AND is_deleted = 0")
    Long countByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * 统计某类型群组数量
     *
     * @param type 群组类型
     * @return 群组数量
     */
    @Select("SELECT COUNT(*) FROM im_group WHERE type = #{type} AND status = 0 AND is_deleted = 0")
    Long countByType(@Param("type") Integer type);

    /**
     * 统计总成员数
     *
     * @return 总成员数
     */
    @Select("SELECT COALESCE(SUM(member_count), 0) FROM im_group WHERE status = 0 AND is_deleted = 0")
    Long countTotalMembers();

    // ==================== 批量操作 ====================

    /**
     * 批量查询群组
     *
     * @param ids 群组ID列表
     * @return 群组列表
     */
    List<Group> findByIds(@Param("ids") List<Long> ids);

    /**
     * 查询用户加入的所有群组ID
     *
     * @param userId 用户ID
     * @return 群组ID列表
     */
    @Select("SELECT g.id FROM im_group g INNER JOIN im_group_member m ON g.id = m.group_id " +
            "WHERE m.user_id = #{userId} AND m.status = 0 AND g.status = 0 AND g.is_deleted = 0 AND m.is_deleted = 0")
    List<Long> findGroupIdsByMemberId(@Param("userId") Long userId);

    /**
     * 查询用户的群组列表（包含成员角色信息）
     *
     * @param userId 用户ID
     * @return 群组列表
     */
    @Select("SELECT g.*, m.role as member_role FROM im_group g INNER JOIN im_group_member m ON g.id = m.group_id " +
            "WHERE m.user_id = #{userId} AND m.status = 0 AND g.status = 0 AND g.is_deleted = 0 AND m.is_deleted = 0 " +
            "ORDER BY m.create_time DESC")
    List<Group> findGroupsByMemberId(@Param("userId") Long userId);
}
