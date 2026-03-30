package com.im.repository;

import com.im.entity.MessageQueue;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 消息队列数据访问层
 * 功能 #1: 消息队列核心系统 - 队列管理
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Mapper
public interface MessageQueueMapper {
    
    /**
     * 插入队列
     */
    @Insert("INSERT INTO message_queue (queue_name, topic, description, status, max_size, current_size, " +
            "persistent, max_retry_count, create_time, last_active_time, consumer_count, partition_count) " +
            "VALUES (#{queueName}, #{topic}, #{description}, #{status}, #{maxSize}, #{currentSize}, " +
            "#{persistent}, #{maxRetryCount}, #{createTime}, #{lastActiveTime}, #{consumerCount}, #{partitionCount})")
    int insert(MessageQueue queue);
    
    /**
     * 根据名称查询
     */
    @Select("SELECT * FROM message_queue WHERE queue_name = #{queueName}")
    @Results({
        @Result(property = "queueName", column = "queue_name"),
        @Result(property = "maxSize", column = "max_size"),
        @Result(property = "currentSize", column = "current_size"),
        @Result(property = "maxPriority", column = "max_priority"),
        @Result(property = "maxRetryCount", column = "max_retry_count"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "lastActiveTime", column = "last_active_time"),
        @Result(property = "consumerCount", column = "consumer_count"),
        @Result(property = "partitionCount", column = "partition_count"),
        @Result(property = "producerGroup", column = "producer_group"),
        @Result(property = "consumerGroup", column = "consumer_group")
    })
    MessageQueue selectByName(@Param("queueName") String queueName);
    
    /**
     * 根据主题查询
     */
    @Select("SELECT * FROM message_queue WHERE topic = #{topic} AND status = 'ACTIVE'")
    @Results({
        @Result(property = "queueName", column = "queue_name"),
        @Result(property = "currentSize", column = "current_size"),
        @Result(property = "maxSize", column = "max_size")
    })
    List<MessageQueue> selectByTopic(@Param("topic") String topic);
    
    /**
     * 查询所有活跃队列
     */
    @Select("SELECT * FROM message_queue WHERE status = 'ACTIVE' ORDER BY last_active_time DESC")
    @Results({
        @Result(property = "queueName", column = "queue_name"),
        @Result(property = "currentSize", column = "current_size"),
        @Result(property = "maxSize", column = "max_size"),
        @Result(property = "lastActiveTime", column = "last_active_time")
    })
    List<MessageQueue> selectAllActive();
    
    /**
     * 更新队列
     */
    @Update("UPDATE message_queue SET topic = #{topic}, description = #{description}, status = #{status}, " +
            "max_size = #{maxSize}, current_size = #{currentSize}, last_active_time = #{lastActiveTime}, " +
            "consumer_count = #{consumerCount}, partition_count = #{partitionCount} " +
            "WHERE queue_name = #{queueName}")
    int update(MessageQueue queue);
    
    /**
     * 增加队列大小
     */
    @Update("UPDATE message_queue SET current_size = current_size + 1, last_active_time = NOW() " +
            "WHERE queue_name = #{queueName}")
    int incrementSize(@Param("queueName") String queueName);
    
    /**
     * 减少队列大小
     */
    @Update("UPDATE message_queue SET current_size = current_size - 1, last_active_time = NOW() " +
            "WHERE queue_name = #{queueName} AND current_size > 0")
    int decrementSize(@Param("queueName") String queueName);
    
    /**
     * 删除队列
     */
    @Delete("DELETE FROM message_queue WHERE queue_name = #{queueName}")
    int deleteByName(@Param("queueName") String queueName);
    
    /**
     * 统计队列数量
     */
    @Select("SELECT COUNT(*) FROM message_queue WHERE status = 'ACTIVE'")
    int countActiveQueues();
}
