package com.im.repository;

import com.im.entity.MessageQueue;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 消息队列数据访问层
 * 功能 #1: 消息队列核心系统
 */
@Mapper
public interface MessageQueueMapper {
    
    @Insert("INSERT INTO message_queue (queue_name, queue_type, durable, exclusive, " +
            "auto_delete, max_length, max_size, message_ttl, status, create_time, owner) VALUES " +
            "(#{queueName}, #{queueType}, #{durable}, #{exclusive}, #{autoDelete}, " +
            "#{maxLength}, #{maxSize}, #{messageTTL}, #{status}, #{createTime}, #{owner})")
    int insert(MessageQueue queue);
    
    @Select("SELECT * FROM message_queue WHERE queue_name = #{queueName}")
    MessageQueue selectByName(String queueName);
    
    @Select("SELECT * FROM message_queue WHERE status = 'ACTIVE'")
    List<MessageQueue> selectAllActive();
    
    @Update("UPDATE message_queue SET status = #{status}, message_count = #{messageCount}, " +
            "consumer_count = #{consumerCount}, last_activity_time = #{lastActivityTime} " +
            "WHERE queue_name = #{queueName}")
    int updateStats(MessageQueue queue);
    
    @Delete("DELETE FROM message_queue WHERE queue_name = #{queueName}")
    int deleteByName(String queueName);
    
    @Select("SELECT COUNT(*) FROM message_queue WHERE status = 'ACTIVE'")
    long countActiveQueues();
}
