package com.im.local.coupon.repository;

import com.im.local.coupon.entity.UserCoupon;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户优惠券数据访问层
 */
@Mapper
public interface UserCouponMapper {

    @Insert("INSERT INTO im_user_coupon (user_id, coupon_id, template_id, merchant_id, coupon_code, " +
            "coupon_name, coupon_type, coupon_value, min_spend, valid_start_time, valid_end_time, " +
            "status, receive_time, receive_channel, receive_scene, receive_longitude, receive_latitude) " +
            "VALUES (#{userId}, #{couponId}, #{templateId}, #{merchantId}, #{couponCode}, " +
            "#{couponName}, #{couponType}, #{couponValue}, #{minSpend}, #{validStartTime}, #{validEndTime}, " +
            "0, NOW(), #{receiveChannel}, #{receiveScene}, #{receiveLongitude}, #{receiveLatitude})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserCoupon userCoupon);

    @Select("SELECT * FROM im_user_coupon WHERE id = #{id}")
    UserCoupon selectById(Long id);

    @Select("SELECT * FROM im_user_coupon WHERE user_id = #{userId} ORDER BY receive_time DESC")
    List<UserCoupon> selectByUserId(Long userId);

    @Select("SELECT * FROM im_user_coupon WHERE user_id = #{userId} AND status = #{status} ORDER BY valid_end_time ASC")
    List<UserCoupon> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM im_user_coupon WHERE user_id = #{userId} AND coupon_id = #{couponId}")
    int countByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);

    @Update("UPDATE im_user_coupon SET status = 1, use_time = NOW(), order_id = #{orderId} " +
            "WHERE id = #{id} AND status = 0")
    int markAsUsed(@Param("id") Long id, @Param("orderId") Long orderId);

    @Update("UPDATE im_user_coupon SET status = 2 WHERE status = 0 AND valid_end_time < NOW()")
    int expireCoupons();

    @Select("SELECT * FROM im_user_coupon WHERE user_id = #{userId} AND coupon_id = #{couponId} AND status = 0 LIMIT 1")
    UserCoupon selectUnusedByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
}
