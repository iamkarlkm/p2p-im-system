package com.im.backend.repository;

import com.im.backend.model.UserFriendGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户好友分组成员Repository
 */
@Repository
public interface UserFriendGroupMemberRepository extends JpaRepository<UserFriendGroupMember, Long> {

    /**
     * 根据分组ID查询所有成员，按排序顺序返回
     */
    List<UserFriendGroupMember> findByGroupIdOrderBySortOrderAscAddedAtAsc(Long groupId);

    /**
     * 根据用户ID和好友ID查询所有分组关系
     */
    List<UserFriendGroupMember> findByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * 根据分组ID和好友ID查询
     */
    Optional<UserFriendGroupMember> findByGroupIdAndFriendId(Long groupId, Long friendId);

    /**
     * 统计分组内成员数量
     */
    long countByGroupId(Long groupId);

    /**
     * 检查好友是否在指定分组中
     */
    boolean existsByGroupIdAndFriendId(Long groupId, Long friendId);

    /**
     * 删除分组内的指定好友
     */
    @Modifying
    @Query("DELETE FROM UserFriendGroupMember m WHERE m.group.id = :groupId AND m.friendId = :friendId")
    void deleteByGroupIdAndFriendId(@Param("groupId") Long groupId, @Param("friendId") Long friendId);

    /**
     * 删除分组的所有成员
     */
    @Modifying
    @Query("DELETE FROM UserFriendGroupMember m WHERE m.group.id = :groupId")
    void deleteAllByGroupId(@Param("groupId") Long groupId);

    /**
     * 将好友从所有分组中移除
     */
    @Modifying
    @Query("DELETE FROM UserFriendGroupMember m WHERE m.userId = :userId AND m.friendId = :friendId")
    void deleteAllByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 更新成员排序
     */
    @Modifying
    @Query("UPDATE UserFriendGroupMember m SET m.sortOrder = :sortOrder WHERE m.id = :memberId")
    void updateSortOrder(@Param("memberId") Long memberId, @Param("sortOrder") Integer sortOrder);

    /**
     * 移动好友到新分组
     */
    @Modifying
    @Query("UPDATE UserFriendGroupMember m SET m.group.id = :newGroupId, m.sortOrder = :sortOrder WHERE m.id = :memberId")
    void moveToGroup(@Param("memberId") Long memberId, @Param("newGroupId") Long newGroupId, @Param("sortOrder") Integer sortOrder);
}
