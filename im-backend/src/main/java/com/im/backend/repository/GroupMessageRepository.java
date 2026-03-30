package com.im.backend.repository;

import com.im.backend.entity.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 群组消息数据访问接口
 * 对应功能 #15 - 群聊功能
 */
@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    
    /**
     * 根据群组ID分页查询消息
     */
    Page<GroupMessage> findByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);
    
    /**
     * 根据群组ID查询最新消息
     */
    List<GroupMessage> findTop20ByGroupIdOrderByCreatedAtDesc(Long groupId);
    
    /**
     * 查询群组消息总数
     */
    long countByGroupId(Long groupId);
    
    /**
     * 撤回消息
     */
    @Modifying
    @Query("UPDATE GroupMessage gm SET gm.status = 'RECALLED', gm.content = '消息已撤回', gm.recalledAt = :now WHERE gm.id = :id AND gm.senderId = :senderId")
    int recallMessage(@Param("id") Long id, @Param("senderId") Long senderId, @Param("now") LocalDateTime now);
    
    /**
     * 查询某时间之后的消息
     */
    @Query("SELECT gm FROM GroupMessage gm WHERE gm.groupId = :groupId AND gm.createdAt > :after")
    List<GroupMessage> findByGroupIdAndCreatedAtAfter(@Param("groupId") Long groupId, @Param("after") LocalDateTime after);
}
