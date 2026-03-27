package com.im.backend.repository;

import com.im.backend.model.UserFriendGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户好友分组Repository
 */
@Repository
public interface UserFriendGroupRepository extends JpaRepository<UserFriendGroup, Long> {

    /**
     * 根据用户ID查询所有分组，按排序顺序返回
     */
    List<UserFriendGroup> findByUserIdOrderBySortOrderAsc(Long userId);

    /**
     * 根据用户ID和分组名称查询
     */
    Optional<UserFriendGroup> findByUserIdAndGroupName(Long userId, String groupName);

    /**
     * 根据用户ID查询默认分组
     */
    Optional<UserFriendGroup> findByUserIdAndIsDefaultTrue(Long userId);

    /**
     * 统计用户的分组数量
     */
    long countByUserId(Long userId);

    /**
     * 检查用户是否有指定名称的分组
     */
    boolean existsByUserIdAndGroupName(Long userId, String groupName);

    /**
     * 删除用户的指定分组
     */
    @Modifying
    @Query("DELETE FROM UserFriendGroup g WHERE g.userId = :userId AND g.id = :groupId")
    void deleteByUserIdAndId(@Param("userId") Long userId, @Param("groupId") Long groupId);

    /**
     * 更新分组排序
     */
    @Modifying
    @Query("UPDATE UserFriendGroup g SET g.sortOrder = :sortOrder WHERE g.id = :groupId AND g.userId = :userId")
    void updateSortOrder(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("sortOrder") Integer sortOrder);

    /**
     * 批量更新排序
     */
    @Modifying
    @Query("UPDATE UserFriendGroup g SET g.sortOrder = :sortOrder WHERE g.id = :groupId")
    void updateSortOrderById(@Param("groupId") Long groupId, @Param("sortOrder") Integer sortOrder);

    /**
     * 更新成员数量
     */
    @Modifying
    @Query(value = "UPDATE user_friend_groups SET member_count = (SELECT COUNT(*) FROM user_friend_group_members WHERE group_id = :groupId) WHERE id = :groupId", nativeQuery = true)
    void updateMemberCount(@Param("groupId") Long groupId);
}
