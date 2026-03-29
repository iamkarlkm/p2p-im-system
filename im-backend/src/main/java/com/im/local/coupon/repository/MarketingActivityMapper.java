package com.im.local.coupon.repository;

import com.im.local.coupon.entity.MarketingActivity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 营销活动数据访问层
 */
@Mapper
public interface MarketingActivityMapper {

    @Insert("INSERT INTO im_marketing_activity (merchant_id, name, type, description, cover_image, " +
            "share_image, start_time, end_time, rules, participation_threshold, threshold_amount, " +
            "participation_limit, stock_quantity, require_share, share_title, share_desc, " +
            "geofence_enabled, fence_longitude, fence_latitude, fence_radius, status, create_by) " +
            "VALUES (#{merchantId}, #{name}, #{type}, #{description}, #{coverImage}, " +
            "#{shareImage}, #{startTime}, #{endTime}, #{rules}, #{participationThreshold}, #{thresholdAmount}, " +
            "#{participationLimit}, #{stockQuantity}, #{requireShare}, #{shareTitle}, #{shareDesc}, " +
            "#{geofenceEnabled}, #{fenceLongitude}, #{fenceLatitude}, #{fenceRadius}, #{status}, #{createBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MarketingActivity activity);

    @Select("SELECT * FROM im_marketing_activity WHERE id = #{id} AND deleted = 0")
    MarketingActivity selectById(Long id);

    @Select("SELECT * FROM im_marketing_activity WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY create_time DESC")
    List<MarketingActivity> selectByMerchantId(Long merchantId);

    @Select("SELECT * FROM im_marketing_activity WHERE status = 2 AND deleted = 0 ORDER BY start_time DESC")
    List<MarketingActivity> selectActiveActivities();

    @Update("UPDATE im_marketing_activity SET sold_quantity = sold_quantity + #{quantity}, " +
            "view_count = view_count + 1 WHERE id = #{id}")
    int incrementSold(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE im_marketing_activity SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementView(Long id);

    @Update("UPDATE im_marketing_activity SET share_count = share_count + 1 WHERE id = #{id}")
    int incrementShare(Long id);

    @Update("UPDATE im_marketing_activity SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
