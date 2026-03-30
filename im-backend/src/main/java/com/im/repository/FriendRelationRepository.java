package com.im.repository;

import com.im.entity.FriendRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 好友关系数据访问接口
 * 功能ID: #5
 */
@Repository
public interface FriendRelationRepository extends JpaRepository<FriendRelation, String> {
    List<FriendRelation> findByUserId(String userId);
    boolean existsByUserIdAndFriendId(String userId, String friendId);
    void deleteByUserIdAndFriendId(String userId, String friendId);
}
