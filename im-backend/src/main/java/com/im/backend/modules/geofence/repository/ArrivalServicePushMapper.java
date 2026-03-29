package com.im.backend.modules.geofence.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.geofence.entity.ArrivalServicePush;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 到店服务推送Mapper
 */
public interface ArrivalServicePushMapper extends BaseMapper<ArrivalServicePush> {

    /**
     * 根据到店记录ID查询推送
     */
    @Select("SELECT * FROM im_arrival_service_push WHERE arrival_record_id = #{arrivalRecordId} AND deleted = 0")
    List<ArrivalServicePush> selectByArrivalRecordId(@Param("arrivalRecordId") Long arrivalRecordId);

    /**
     * 更新推送状态为已送达
     */
    @Update("UPDATE im_arrival_service_push SET push_status = 'DELIVERED', delivered_time = #{time} WHERE id = #{id}")
    int updateDelivered(@Param("id") Long id, @Param("time") LocalDateTime time);

    /**
     * 更新推送状态为已读
     */
    @Update("UPDATE im_arrival_service_push SET push_status = 'READ', read_time = #{time} WHERE id = #{id}")
    int updateRead(@Param("id") Long id, @Param("time") LocalDateTime time);

    /**
     * 查询用户今日收到的推送
     */
    @Select("SELECT * FROM im_arrival_service_push WHERE user_id = #{userId} AND DATE(create_time) = CURDATE() AND deleted = 0")
    List<ArrivalServicePush> selectTodayByUserId(@Param("userId") Long userId);

    /**
     * 查询待发送的推送
     */
    @Select("SELECT * FROM im_arrival_service_push WHERE push_status = 'PENDING' AND deleted = 0 LIMIT 100")
    List<ArrivalServicePush> selectPendingPushes();
}
