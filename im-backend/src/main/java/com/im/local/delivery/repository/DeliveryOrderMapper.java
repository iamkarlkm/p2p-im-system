package com.im.local.delivery.repository;

import com.im.local.delivery.entity.DeliveryOrder;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 配送订单数据访问层
 */
@Mapper
public interface DeliveryOrderMapper {
    
    @Insert("INSERT INTO delivery_order (merchant_order_id, merchant_id, user_id, rider_id, " +
            "delivery_no, status, pickup_address, pickup_lat, pickup_lng, pickup_contact, " +
            "pickup_phone, delivery_address, delivery_lat, delivery_lng, receiver_name, " +
            "receiver_phone, distance, estimated_duration, delivery_fee, tip_amount, " +
            "item_type, item_weight, remark, assigned_at, sign_code, " +
            "expect_deliver_time, timeout_minutes, delivery_type, created_at, updated_at) " +
            "VALUES (#{merchantOrderId}, #{merchantId}, #{userId}, #{riderId}, " +
            "#{deliveryNo}, #{status}, #{pickupAddress}, #{pickupLat}, #{pickupLng}, #{pickupContact}, " +
            "#{pickupPhone}, #{deliveryAddress}, #{deliveryLat}, #{deliveryLng}, #{receiverName}, " +
            "#{receiverPhone}, #{distance}, #{estimatedDuration}, #{deliveryFee}, #{tipAmount}, " +
            "#{itemType}, #{itemWeight}, #{remark}, #{assignedAt}, #{signCode}, " +
            "#{expectDeliverTime}, #{timeoutMinutes}, #{deliveryType}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DeliveryOrder order);
    
    @Select("SELECT * FROM delivery_order WHERE id = #{id}")
    DeliveryOrder selectById(Long id);
    
    @Select("SELECT * FROM delivery_order WHERE delivery_no = #{deliveryNo}")
    DeliveryOrder selectByDeliveryNo(String deliveryNo);
    
    @Select("SELECT * FROM delivery_order WHERE rider_id = #{riderId} AND status IN (1,2,3) " +
            "ORDER BY created_at DESC")
    List<DeliveryOrder> selectActiveByRider(Long riderId);
    
    @Select("SELECT * FROM delivery_order WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<DeliveryOrder> selectByUser(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    @Select("SELECT * FROM delivery_order WHERE merchant_id = #{merchantId} AND status = #{status} " +
            "ORDER BY created_at DESC")
    List<DeliveryOrder> selectByMerchantAndStatus(@Param("merchantId") Long merchantId, 
                                                   @Param("status") Integer status);
    
    @Update("UPDATE delivery_order SET status = #{status}, updated_at = NOW() WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);
    
    @Update("UPDATE delivery_order SET rider_id = #{riderId}, status = 1, assigned_at = NOW(), " +
            "updated_at = NOW() WHERE id = #{orderId}")
    int assignRider(@Param("orderId") Long orderId, @Param("riderId") Long riderId);
    
    @Update("UPDATE delivery_order SET status = 3, picked_up_at = NOW(), updated_at = NOW() " +
            "WHERE id = #{orderId}")
    int markPickedUp(Long orderId);
    
    @Update("UPDATE delivery_order SET status = 4, delivered_at = NOW(), sign_image_url = #{signImageUrl}, " +
            "updated_at = NOW() WHERE id = #{orderId}")
    int markDelivered(@Param("orderId") Long orderId, @Param("signImageUrl") String signImageUrl);
    
    @Select("SELECT COUNT(*) FROM delivery_order WHERE rider_id = #{riderId} AND status = 4 " +
            "AND DATE(delivered_at) = CURDATE()")
    int countTodayDeliveredByRider(Long riderId);
}
