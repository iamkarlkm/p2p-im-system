package com.im.backend.modules.poi.customer_service.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.poi.customer_service.entity.PoiCustomerServiceMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 客服消息Mapper
 */
@Mapper
public interface PoiCustomerServiceMessageMapper extends BaseMapper<PoiCustomerServiceMessage> {

    /**
     * 查询会话的消息列表
     */
    @Select("SELECT * FROM poi_cs_message WHERE session_id = #{sessionId} AND deleted = 0 ORDER BY create_time ASC")
    List<PoiCustomerServiceMessage> selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 分页查询会话消息
     */
    Page<PoiCustomerServiceMessage> selectPageBySessionId(Page<PoiCustomerServiceMessage> page, @Param("sessionId") String sessionId);

    /**
     * 根据消息ID查询
     */
    @Select("SELECT * FROM poi_cs_message WHERE message_id = #{messageId} AND deleted = 0")
    PoiCustomerServiceMessage selectByMessageId(@Param("messageId") String messageId);

    /**
     * 标记消息已读
     */
    @Update("UPDATE poi_cs_message SET read = 1, read_time = NOW() WHERE session_id = #{sessionId} AND sender_type != #{senderType} AND read = 0")
    int markAsRead(@Param("sessionId") String sessionId, @Param("senderType") String senderType);

    /**
     * 查询会话的最后一条消息
     */
    @Select("SELECT * FROM poi_cs_message WHERE session_id = #{sessionId} AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    PoiCustomerServiceMessage selectLastMessage(@Param("sessionId") String sessionId);

    /**
     * 撤回消息
     */
    @Update("UPDATE poi_cs_message SET recalled = 1, recall_time = NOW() WHERE message_id = #{messageId}")
    int recallMessage(@Param("messageId") String messageId);
}
