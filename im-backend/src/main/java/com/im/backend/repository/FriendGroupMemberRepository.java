package com.im.backend.repository;

import com.im.backend.model.FriendGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendGroupMemberRepository extends JpaRepository<FriendGroupMember, Long> {

    List<FriendGroupMember> findAllByGroupId(Long groupId);

    List<FriendGroupMember> findAllByGroupIdAndFriendIdIn(Long groupId, List<Long> friendIds);

    @Modifying
    @Query("DELETE FROM FriendGroupMember m WHERE m.groupId = :groupId AND m.friendId IN :friendIds")
    int deleteByGroupIdAndFriendIdIn(@Param("groupId") Long groupId, @Param("friendIds") List<Long> friendIds);

    long countByGroupId(Long groupId);

    boolean existsByGroupIdAndFriendId(Long groupId, Long friendId);

    @Modifying
    @Query("DELETE FROM FriendGroupMember m WHERE m.groupId = :groupId")
    void deleteAllByGroupId(@Param("groupId") Long groupId);
}
