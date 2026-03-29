package com.im.local.coupon.repository;

import com.im.local.coupon.entity.Coupon;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 优惠券数据访问层
 */
@Mapper
public interface CouponMapper {

    @Insert("INSERT INTO im_coupon (template_id, merchant_id, name, description, type, value, " +
            "min_spend, max_discount, total_quantity, received_quantity, used_quantity, " +
            "limit_per_user, validity_type, valid_start_time, valid_end_time, valid_days, " +
            "apply_scope, apply_category_ids, apply_product_ids, geofence_enabled, " +
            "fence_longitude, fence_latitude, fence_radius, status, create_by) " +
            "VALUES (#{templateId}, #{merchantId}, #{name}, #{description}, #{type}, #{value}, " +
            "#{minSpend}, #{maxDiscount}, #{totalQuantity}, 0, 0, " +
            "#{limitPerUser}, #{validityType}, #{validStartTime}, #{validEndTime}, #{validDays}, " +
            "#{applyScope}, #{applyCategoryIds}, #{applyProductIds}, #{geofenceEnabled}, " +
            "#{fenceLongitude}, #{fenceLatitude}, #{fenceRadius}, #{status}, #{createBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Coupon coupon);

    @Select("SELECT * FROM im_coupon WHERE id = #{id} AND deleted = 0")
    Coupon selectById(Long id);

    @Select("SELECT * FROM im_coupon WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY create_time DESC")
    List<Coupon> selectByMerchantId(Long merchantId);

    @Select("SELECT * FROM im_coupon WHERE status = 2 AND deleted = 0 ORDER BY create_time DESC")
    List<Coupon> selectActiveCoupons();

    @Update("UPDATE im_coupon SET received_quantity = received_quantity + 1 WHERE id = #{id} " +
            "AND (total_quantity = -1 OR received_quantity < total_quantity)")
    int incrementReceived(Long id);

    @Update("UPDATE im_coupon SET used_quantity = used_quantity + 1 WHERE id = #{id}")
    int incrementUsed(Long id);

    @Update("UPDATE im_coupon SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE im_coupon SET deleted = 1, update_time = NOW() WHERE id = #{id}")
    int deleteById(Long id);
}
