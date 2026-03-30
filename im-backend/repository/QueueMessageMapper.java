package com.im.repository;

import com.im.entity.QueueMessage;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 队列消息数据访问层
 * 功能 #1: 消息队列核心系统
 */
@Mapper
public interface QueueMessageMapper {
    
    @Insert("INSERT INTO queue_message (message_id, queue_name, message_type, payload, " +
            "headers, priority, create_time, expire_time, retry_count, max_retry_count, " +
            "status, producer_id, persistent) VALUES " +
            "(#{messageId}, #{queueName}, #{messageType}, #{payload}, #{headers}, " +
            "#{priority}, #{createTime}, #{expireTime}, #{retryCount}, #{maxRetryCount}, " +
            "#{status}, #{producerId}, #{persistent})")
    int insert(QueueMessage message);
    
    @Select("SELECT * FROM queue_message WHERE message_id = #{messageId}")
    QueueMessage selectById(String messageId);
    
    @Select("SELECT * FROM queue_message WHERE queue_name = #{queueName} " +
            "AND status = 'PENDING' ORDER BY priority DESC, create_time ASC LIMIT #{limit}")
    List<QueueMessage> selectPendingByQueue(@Param("queueName") String queueName, @Param("limit") int limit);
    
    @Update("UPDATE queue_message SET status = #{status}, consumer_id = #{consumerId}, " +
            "consume_time = #{consumeTime}, retry_count = #{retryCount}, " +
            "failure_reason = #{failureReason} WHERE message_id = #{messageId}")
    int updateStatus(QueueMessage message);
    
    @Delete("DELETE FROM queue_message WHERE message_id = #{messageId}")
    int deleteById(String messageId);
    
    @Select("SELECT COUNT(*) FROM queue_message WHERE queue_name = #{queueName} AND status = #{status}")
    long countByQueueAndStatus(@Param("queueName") String queueName, @Param("status") String status);
}
