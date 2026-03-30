package com.im.repository;

import com.im.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 好友申请数据访问接口
 * 功能ID: #5
 */
@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    List<FriendRequest> findByToUserIdAndStatus(String toUserId, Integer status);
    List<FriendRequest> findByFromUserId(String fromUserId);
    
    @Query("SELECT COUNT(fr) > 0 FROM FriendRequest fr WHERE " +
           "fr.fromUserId = :fromId AND fr.toUserId = :toId AND fr.status = 0")
    boolean existsPendingRequest(@Param("fromId") String fromId, @Param("toId") String toId);
}
