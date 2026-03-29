package com.im.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群组成员仓库
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    /**
     * 根据用户ID查询加入的群组ID列表
     */
    @Query("SELECT gm.groupId FROM GroupMember gm WHERE gm.userId = :userId AND gm.status = 'ACTIVE'")
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据群组ID查询成员ID列表
     */
    @Query("SELECT gm.userId FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.status = 'ACTIVE'")
    List<Long> findUserIdsByGroupId(@Param("groupId") Long groupId);

    /**
     * 计算用户在群组中的活跃度分数
     */
    @Query("SELECT COALESCE(SUM(gm.messageCount * 0.1 + gm.joinDays * 0.01), 0) FROM GroupMember gm WHERE gm.userId = :userId AND gm.groupId = :groupId")
    double calculateActivityScore(@Param("userId") Long userId, @Param("groupId") Long groupId);

    /**
     * 查询群组中的活跃用户
     */
    @Query(value = "SELECT user_id FROM group_members WHERE group_id IN :groupIds AND status = 'ACTIVE' " +
                   "ORDER BY (message_count * 0.1 + join_days * 0.01) DESC LIMIT :limit", nativeQuery = true)
    List<Long> findActiveUsersInGroups(@Param("groupIds") List<Long> groupIds, @Param("limit") int limit);

    /**
     * 统计群组成员数量
     */
    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.status = 'ACTIVE'")
    long countByGroupId(@Param("groupId") Long groupId);

    /**
     * 检查用户是否在群组中
     */
    @Query("SELECT COUNT(gm) > 0 FROM GroupMember gm WHERE gm.userId = :userId AND gm.groupId = :groupId AND gm.status = 'ACTIVE'")
    boolean existsByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
}

/**
 * 群组成员实体（简化定义）
 */
class GroupMember {
    private Long id;
    private Long groupId;
    private Long userId;
    private String status;
    private Integer messageCount;
    private Integer joinDays;
}
