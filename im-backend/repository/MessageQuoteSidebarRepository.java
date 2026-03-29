package com.im.backend.repository;

import com.im.backend.entity.MessageQuoteSidebarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 引用消息侧边栏仓储接口
 * 提供引用消息侧边栏的数据库操作
 */
@Repository
public interface MessageQuoteSidebarRepository extends JpaRepository<MessageQuoteSidebarEntity, Long> {

    // 基础查询方法
    
    /**
     * 根据用户ID和会话ID查找侧边栏记录
     */
    List<MessageQuoteSidebarEntity> findByUserIdAndSessionId(Long userId, Long sessionId);
    
    /**
     * 根据用户ID和会话ID查找侧边栏记录（分页）
     */
    Page<MessageQuoteSidebarEntity> findByUserIdAndSessionId(Long userId, Long sessionId, Pageable pageable);
    
    /**
     * 根据用户ID、会话ID和是否固定查找记录
     */
    List<MessageQuoteSidebarEntity> findByUserIdAndSessionIdAndIsPinned(Long userId, Long sessionId, Boolean isPinned);
    
    /**
     * 根据用户ID查找所有侧边栏记录
     */
    List<MessageQuoteSidebarEntity> findByUserId(Long userId);
    
    /**
     * 根据用户ID和引用ID查找记录
     */
    Optional<MessageQuoteSidebarEntity> findByUserIdAndQuoteId(Long userId, Long quoteId);
    
    /**
     * 根据会话ID查找所有相关侧边栏记录
     */
    List<MessageQuoteSidebarEntity> findBySessionId(Long sessionId);
    
    // 高级查询方法
    
    /**
     * 查找用户所有固定的侧边栏记录
     */
    @Query("SELECT q FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId AND q.isPinned = true ORDER BY q.sidebarIndex ASC")
    List<MessageQuoteSidebarEntity> findPinnedSidebarItemsByUserId(@Param("userId") Long userId);
    
    /**
     * 查找会话中用户的所有侧边栏记录，按最后查看时间倒序排列
     */
    @Query("SELECT q FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId AND q.sessionId = :sessionId ORDER BY q.lastViewedAt DESC")
    List<MessageQuoteSidebarEntity> findRecentSidebarItemsByUserAndSession(@Param("userId") Long userId, @Param("sessionId") Long sessionId);
    
    /**
     * 查找用户最近查看的侧边栏记录（跨所有会话）
     */
    @Query("SELECT q FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId ORDER BY q.lastViewedAt DESC")
    List<MessageQuoteSidebarEntity> findRecentSidebarItemsByUser(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 查找超过指定时间未查看的侧边栏记录
     */
    @Query("SELECT q FROM MessageQuoteSidebarEntity q WHERE q.lastViewedAt < :threshold AND q.isPinned = false")
    List<MessageQuoteSidebarEntity> findStaleSidebarItems(@Param("threshold") Instant threshold);
    
    /**
     * 统计用户侧边栏记录数量
     */
    @Query("SELECT COUNT(q) FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计会话中侧边栏记录数量
     */
    @Query("SELECT COUNT(q) FROM MessageQuoteSidebarEntity q WHERE q.sessionId = :sessionId")
    Long countBySessionId(@Param("sessionId") Long sessionId);
    
    /**
     * 统计用户固定侧边栏记录数量
     */
    @Query("SELECT COUNT(q) FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId AND q.isPinned = true")
    Long countPinnedByUserId(@Param("userId") Long userId);
    
    /**
     * 根据消息类型查找侧边栏记录
     */
    @Query("SELECT q FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId AND q.messageType = :messageType")
    List<MessageQuoteSidebarEntity> findByMessageType(@Param("userId") Long userId, @Param("messageType") String messageType);
    
    /**
     * 根据发送者ID查找侧边栏记录
     */
    @Query("SELECT q FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId AND q.senderId = :senderId")
    List<MessageQuoteSidebarEntity> findBySenderId(@Param("userId") Long userId, @Param("senderId") Long senderId);
    
    // 批量操作和更新方法
    
    /**
     * 批量删除用户的侧边栏记录
     */
    @Modifying
    @Query("DELETE FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId AND q.sessionId = :sessionId")
    int deleteByUserIdAndSessionId(@Param("userId") Long userId, @Param("sessionId") Long sessionId);
    
    /**
     * 删除指定引用ID的侧边栏记录
     */
    @Modifying
    @Query("DELETE FROM MessageQuoteSidebarEntity q WHERE q.quoteId = :quoteId")
    int deleteByQuoteId(@Param("quoteId") Long quoteId);
    
    /**
     * 批量删除超过指定时间的非固定侧边栏记录
     */
    @Modifying
    @Query("DELETE FROM MessageQuoteSidebarEntity q WHERE q.lastViewedAt < :threshold AND q.isPinned = false")
    int deleteStaleSidebarItems(@Param("threshold") Instant threshold);
    
    /**
     * 更新侧边栏位置索引
     */
    @Modifying
    @Query("UPDATE MessageQuoteSidebarEntity q SET q.sidebarIndex = :sidebarIndex WHERE q.id = :id")
    int updateSidebarIndex(@Param("id") Long id, @Param("sidebarIndex") Integer sidebarIndex);
    
    /**
     * 批量更新固定状态
     */
    @Modifying
    @Query("UPDATE MessageQuoteSidebarEntity q SET q.isPinned = :isPinned WHERE q.id IN :ids")
    int updatePinnedStatus(@Param("ids") List<Long> ids, @Param("isPinned") Boolean isPinned);
    
    /**
     * 更新最后查看时间
     */
    @Modifying
    @Query("UPDATE MessageQuoteSidebarEntity q SET q.lastViewedAt = :lastViewedAt WHERE q.id = :id")
    int updateLastViewedAt(@Param("id") Long id, @Param("lastViewedAt") Instant lastViewedAt);
    
    /**
     * 批量更新最后查看时间
     */
    @Modifying
    @Query("UPDATE MessageQuoteSidebarEntity q SET q.lastViewedAt = :lastViewedAt WHERE q.id IN :ids")
    int batchUpdateLastViewedAt(@Param("ids") List<Long> ids, @Param("lastViewedAt") Instant lastViewedAt);
    
    // 搜索方法
    
    /**
     * 搜索侧边栏记录（按预览内容）
     */
    @Query("SELECT q FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId AND LOWER(q.previewContent) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MessageQuoteSidebarEntity> searchByPreviewContent(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    /**
     * 搜索侧边栏记录（按发送者昵称）
     */
    @Query("SELECT q FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId AND LOWER(q.senderNickname) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MessageQuoteSidebarEntity> searchBySenderNickname(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    /**
     * 综合搜索侧边栏记录
     */
    @Query("SELECT q FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId AND " +
           "(LOWER(q.previewContent) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(q.senderNickname) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<MessageQuoteSidebarEntity> searchSidebarItems(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    // 统计方法
    
    /**
     * 按消息类型统计侧边栏记录
     */
    @Query("SELECT q.messageType, COUNT(q) FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId GROUP BY q.messageType")
    List<Object[]> countByMessageType(@Param("userId") Long userId);
    
    /**
     * 按日期统计侧边栏记录新增数量
     */
    @Query("SELECT DATE(q.createdAt), COUNT(q) FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId AND q.createdAt >= :startDate GROUP BY DATE(q.createdAt)")
    List<Object[]> countByDate(@Param("userId") Long userId, @Param("startDate") Instant startDate);
    
    /**
     * 获取用户侧边栏中最早创建的记录时间
     */
    @Query("SELECT MIN(q.createdAt) FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId")
    Optional<Instant> findEarliestCreatedAtByUserId(@Param("userId") Long userId);
    
    /**
     * 获取用户侧边栏中最晚查看的记录时间
     */
    @Query("SELECT MAX(q.lastViewedAt) FROM MessageQuoteSidebarEntity q WHERE q.userId = :userId")
    Optional<Instant> findLatestViewedAtByUserId(@Param("userId") Long userId);
}