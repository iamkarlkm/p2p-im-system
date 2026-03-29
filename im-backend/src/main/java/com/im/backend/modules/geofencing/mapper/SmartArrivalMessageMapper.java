package com.im.backend.modules.geofencing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.geofencing.entity.SmartArrivalMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 智能到店消息Mapper接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Mapper
public interface SmartArrivalMessageMapper extends BaseMapper<SmartArrivalMessage> {

    /**
     * 查询用户消息列表
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数
     * @return 消息列表
     */
    @Select("SELECT * FROM smart_arrival_message WHERE user_id = #{userId} AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<SmartArrivalMessage> selectByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 查询用户未读消息数量
     * @param userId 用户ID
     * @return 未读数量
     */
    @Select("SELECT COUNT(*) FROM smart_arrival_message WHERE user_id = #{userId} AND status != 'READ' AND deleted = 0")
    Integer countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 标记消息已读
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE smart_arrival_message SET status = 'READ', read_time = NOW() " +
            "WHERE id = #{messageId} AND user_id = #{userId}")
    int markAsRead(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * 查询商户消息统计
     * @param merchantId 商户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 消息列表
     */
    @Select("SELECT * FROM smart_arrival_message WHERE merchant_id = #{merchantId} " +
            "AND create_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0")
    List<SmartArrivalMessage> selectByMerchantAndTimeRange(@Param("merchantId") Long merchantId,
                                                           @Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 根据事件ID查询消息
     * @param triggerEventId 触发事件ID
     * @return 消息
     */
    @Select("SELECT * FROM smart_arrival_message WHERE trigger_event_id = #{triggerEventId} AND deleted = 0 LIMIT 1")
    SmartArrivalMessage selectByTriggerEventId(@Param("triggerEventId") Long triggerEventId);
}
