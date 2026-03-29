package com.im.backend.modules.coupon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.im.backend.modules.coupon.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 优惠券Mapper接口 - 数据访问层
 * 
 * 功能说明:
 * 1. 优惠券基础CRUD操作
 * 2. 地理位置范围查询（配合Redis Geo使用）
 * 3. 库存扣减乐观锁更新
 * 4. 复杂条件查询支持
 * 
 * SQL优化:
 * - 索引: status, merchant_id, issue_start_time, issue_end_time
 * - 联合索引: (geo_hash, status) 用于附近搜索
 * - 全文索引: name 用于搜索
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 根据GeoHash前缀查询附近优惠券
     * 用于配合Redis Geo的精准筛选
     * 
     * @param geoHashPrefix GeoHash前缀
     * @param status 状态
     * @return 优惠券列表
     */
    @Select("SELECT * FROM im_coupon WHERE geo_hash LIKE CONCAT(#{geoHashPrefix}, '%') AND status = #{status} AND deleted = 0")
    List<Coupon> selectByGeoHashPrefix(@Param("geoHashPrefix") String geoHashPrefix, @Param("status") Integer status);
    
    /**
     * 扣减库存 - 乐观锁实现
     * 
     * @param couponId 优惠券ID
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE im_coupon SET remaining_stock = remaining_stock - 1, " +
            "received_count = received_count + 1, version = version + 1 " +
            "WHERE id = #{couponId} AND remaining_stock > 0 AND version = #{version}")
    int deductStock(@Param("couponId") Long couponId, @Param("version") Integer version);
    
    /**
     * 增加使用次数统计
     * 
     * @param couponId 优惠券ID
     * @param orderAmount 订单金额
     * @param discountAmount 优惠金额
     * @return 影响行数
     */
    @Update("UPDATE im_coupon SET used_count = used_count + 1, " +
            "related_order_amount = related_order_amount + #{orderAmount}, " +
            "total_discount_amount = total_discount_amount + #{discountAmount} " +
            "WHERE id = #{couponId}")
    int incrementUsedStats(@Param("couponId") Long couponId, 
                           @Param("orderAmount") java.math.BigDecimal orderAmount,
                           @Param("discountAmount") java.math.BigDecimal discountAmount);
    
    /**
     * 查询进行中的平台优惠券
     * 
     * @param limit 数量限制
     * @return 优惠券列表
     */
    @Select("SELECT * FROM im_coupon WHERE is_platform_coupon = 1 AND status = 2 AND deleted = 0 " +
            "AND issue_start_time <= NOW() AND issue_end_time >= NOW() " +
            "ORDER BY is_top DESC, sort_order DESC, create_time DESC LIMIT #{limit}")
    List<Coupon> selectActivePlatformCoupons(@Param("limit") Integer limit);
    
    /**
     * 查询商户进行中的优惠券
     * 
     * @param merchantId 商户ID
     * @param limit 数量限制
     * @return 优惠券列表
     */
    @Select("SELECT * FROM im_coupon WHERE merchant_id = #{merchantId} AND status = 2 AND deleted = 0 " +
            "AND issue_start_time <= NOW() AND issue_end_time >= NOW() " +
            "ORDER BY is_top DESC, sort_order DESC, create_time DESC LIMIT #{limit}")
    List<Coupon> selectActiveMerchantCoupons(@Param("merchantId") Long merchantId, @Param("limit") Integer limit);
    
    /**
     * 统计商户优惠券使用情况
     * 
     * @param merchantId 商户ID
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "SUM(received_count) as total_received, " +
            "SUM(used_count) as total_used, " +
            "SUM(related_order_amount) as total_order_amount, " +
            "SUM(total_discount_amount) as total_discount_amount " +
            "FROM im_coupon WHERE merchant_id = #{merchantId} AND deleted = 0")
    java.util.Map<String, Object> selectMerchantStats(@Param("merchantId") Long merchantId);
    
    /**
     * 查询即将过期的优惠券（用于定时任务）
     * 
     * @param hours 提前小时数
     * @return 优惠券列表
     */
    @Select("SELECT * FROM im_coupon WHERE status = 2 AND deleted = 0 " +
            "AND issue_end_time BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL #{hours} HOUR)")
    List<Coupon> selectExpiringCoupons(@Param("hours") Integer hours);
    
    /**
     * 批量更新优惠券状态（用于定时任务）
     * 
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     * @return 影响行数
     */
    @Update("UPDATE im_coupon SET status = #{toStatus}, update_time = NOW() " +
            "WHERE status = #{fromStatus} AND deleted = 0")
    int batchUpdateStatus(@Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus);
}
