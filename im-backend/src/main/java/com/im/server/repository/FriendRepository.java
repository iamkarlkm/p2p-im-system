package com.im.server.repository;

import com.im.server.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 好友Repository
 */
@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    
    /**
     * 查询用户的好友列表
     */
    List<Friend> findByUserIdAndStatus(Long userId, Integer status);
    
    /**
     * 查询两个用户之间的关系
     */
    Friend findByUserIdAndFriendId(Long userId, Long friendId);
    
    /**
     * 检查是否为好友
     */
    boolean existsByUserIdAndFriendIdAndStatus(Long userId, Long friendId, Integer status);
}
