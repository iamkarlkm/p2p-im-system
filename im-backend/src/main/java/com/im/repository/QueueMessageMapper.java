package com.im.repository;

import com.im.entity.QueueMessage;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 队列消息数据访问层
 * 功能 #1: 消息队列核心系统 - 消息持久化
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Mapper
public interface QueueMessageMapper {
    
    /**
     * 插入消息
     */
    @Insert("INSERT INTO queue_message (message_id, queue_name, topic, message_type, payload, headers, " +
            "status, retry_count, max_retry_count, producer_id, create_time, next_retry_time, trace_id, priority, ttl, persistent) " +
            "VALUES (#{messageId}, #{queueName}, #{topic}, #{messageType}, #{payload}, #{headers}, " +
            "#{status}, #{retryCount}, #{maxRetryCount}, #{producerId}, #{createTime}, #{nextRetryTime}, #{traceId}, #{priority}, #{ttl}, #{persistent})")
    int insert(QueueMessage message);
    
    /**
     * 批量插入
     */
    @Insert("<script>" +
            "INSERT INTO queue_message (message_id, queue_name, topic, payload, status, retry_count, create_time) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.messageId}, #{item.queueName}, #{item.topic}, #{item.payload}, #{item.status}, #{item.retryCount}, #{item.createTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<QueueMessage> messages);
    
    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM queue_message WHERE message_id = #{messageId}")
    @Results({
        @Result(property = "messageId", column = "message_id"),
        @Result(property = "queueName", column = "queue_name"),
        @Result(property = "messageType", column = "message_type"),
        @Result(property = "retryCount", column = "retry_count"),
        @Result(property = "maxRetryCount", column = "max_retry_count"),
        @Result(property = "producerId", column = "producer_id"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "processTime", column = "process_time"),
        @Result(property = "completeTime", column = "complete_time"),
        @Result(property = "nextRetryTime", column = "next_retry_time"),
        @Result(property = "errorMessage", column = "error_message"),
        @Result(property = "traceId", column = "trace_id")
    })
    QueueMessage selectById(@Param("messageId") String messageId);
    
    /**
     * 查询队列待处理消息
     */
    @Select("SELECT * FROM queue_message WHERE queue_name = #{queueName} AND status = 'PENDING' " +
            "ORDER BY priority DESC, create_time ASC LIMIT #{limit}")
    @Results({
        @Result(property = "messageId", column = "message_id"),
        @Result(property = "queueName", column = "queue_name")
    })
    List<QueueMessage> selectPendingByQueue(@Param("queueName") String queueName, @Param("limit") int limit);
    
    /**
     * 获取可重试消息
     */
    @Select("SELECT * FROM queue_message WHERE queue_name = #{queueName} AND status = 'RETRYING' " +
            "AND next_retry_time &lt;= #{now} ORDER BY next_retry_time ASC LIMIT #{limit}")
    @Results({
        @Result(property = "messageId", column = "message_id"),
        @Result(property = "queueName", column = "queue_name"),
        @Result(property = "nextRetryTime", column = "next_retry_time")
    })
    List<QueueMessage> selectRetryableMessages(@Param("queueName") String queueName, 
                                                @Param("now") LocalDateTime now, 
                                                @Param("limit") int limit);
    
    /**
     * 获取消息
     */
    @Update("UPDATE queue_message SET status = 'PROCESSING', consumer_id = #{consumerId}, process_time = NOW() " +
            "WHERE message_id = #{messageId} AND status IN ('PENDING', 'RETRYING')")
    int acquireMessage(@Param("messageId") String messageId, @Param("consumerId") String consumerId);
    
    /**
     * 更新状态
     */
    @Update("UPDATE queue_message SET status = #{status}, retry_count = #{retryCount}, " +
            "error_message = #{errorMessage}, complete_time = #{completeTime}, next_retry_time = #{nextRetryTime} " +
            "WHERE message_id = #{messageId}")
    int updateStatus(QueueMessage message);
    
    /**
     * 统计状态数量
     */
    @Select("SELECT COUNT(*) FROM queue_message WHERE queue_name = #{queueName} AND status = #{status}")
    long countByStatus(@Param("queueName") String queueName, @Param("status") String status);
    
    /**
     * 检查重复消息
     */
    @Select("SELECT COUNT(*) FROM queue_message WHERE trace_id = #{traceId} AND queue_name = #{queueName} " +
            "AND create_time &gt;= #{since}")
    int countDuplicate(@Param("traceId") String traceId, @Param("queueName") String queueName, 
                       @Param("since") LocalDateTime since);
    
    /**
     * 清理过期消息
     */
    @Delete("DELETE FROM queue_message WHERE create_time &lt; #{before} AND status IN ('SUCCESS', 'FAILED')")
    int cleanupOldMessages(@Param("before") LocalDateTime before);
}
