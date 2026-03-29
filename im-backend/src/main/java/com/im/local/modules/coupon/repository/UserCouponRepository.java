package com.im.local.modules.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.local.modules.coupon.entity.UserCoupon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户优惠券数据访问层
 * @author IM Development Team
 * @since 2026-03-28
 */
@Repository
public interface UserCouponRepository extends BaseMapper<UserCoupon> {

    /**
     * 查询用户优惠券列表
     */
    @Select("SELECT uc.*, c.name as couponName, c.type as couponType, " +
            "c.value as couponValue, c.min_spend as minSpend, c.max_discount as maxDiscount, " +
            "m.id as merchantId, m.name as merchantName, m.logo as merchantLogo " +
            "FROM t_user_coupon uc " +
            "LEFT JOIN t_coupon c ON uc.coupon_id = c.id " +
            "LEFT JOIN t_merchant m ON c.merchant_id = m.id " +
            "WHERE uc.user_id = #{userId} AND uc.deleted = 0 " +
            "ORDER BY uc.status ASC, uc.valid_end_time ASC")
    List<UserCoupon> selectByUserId(@Param("userId") Long userId);

    /**
     * 分页查询用户可用优惠券
     */
    @Select("SELECT uc.*, c.name as couponName, c.type as couponType, " +
            "c.value as couponValue, c.min_spend as minSpend, c.max_discount as maxDiscount " +
            "FROM t_user_coupon uc " +
            "LEFT JOIN t_coupon c ON uc.coupon_id = c.id " +
            "WHERE uc.user_id = #{userId} AND uc.status = 0 " +
            "AND uc.valid_end_time > #{now} AND uc.deleted = 0 " +
            "ORDER BY uc.valid_end_time ASC")
    IPage<UserCoupon> selectUsableByUserId(Page<UserCoupon> page,
                                            @Param("userId") Long userId,
                                            @Param("now") LocalDateTime now);

    /**
     * 查询用户已过期优惠券
     */
    @Select("SELECT * FROM t_user_coupon WHERE user_id = #{userId} " +
            "AND status = 0 AND valid_end_time < #{now} AND deleted = 0")
    List<UserCoupon> selectExpiredByUserId(@Param("userId") Long userId,
                                            @Param("now") LocalDateTime now);

    /**
     * 统计用户领取某优惠券的数量
     */
    @Select("SELECT COUNT(*) FROM t_user_coupon " +
            "WHERE user_id = #{userId} AND coupon_id = #{couponId} AND deleted = 0")
    int countByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /**
     * 查询订单使用的优惠券
     */
    @Select("SELECT * FROM t_user_coupon WHERE order_id = #{orderId} AND deleted = 0")
    UserCoupon selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 使用优惠券
     */
    @Update("UPDATE t_user_coupon SET status = 1, use_time = #{useTime}, " +
            "order_id = #{orderId}, order_amount = #{orderAmount}, " +
            "discount_amount = #{discountAmount}, update_time = #{useTime} " +
            "WHERE id = #{id} AND status = 0 AND deleted = 0")
    int useCoupon(@Param("id") Long id, @Param("useTime") LocalDateTime useTime,
                  @Param("orderId") Long orderId, @Param("orderAmount") java.math.BigDecimal orderAmount,
                  @Param("discountAmount") java.math.BigDecimal discountAmount);

    /**
     * 批量标记过期优惠券
     */
    @Update("UPDATE t_user_coupon SET status = 2, update_time = #{now} " +
            "WHERE user_id = #{userId} AND status = 0 " +
            "AND valid_end_time < #{now} AND deleted = 0")
    int markExpired(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * 查询即将过期的优惠券
     */
    @Select("SELECT * FROM t_user_coupon WHERE user_id = #{userId} " +
            "AND status = 0 AND valid_end_time BETWEEN #{now} AND #{expireTime} " +
            "AND deleted = 0 ORDER BY valid_end_time ASC")
    List<UserCoupon> selectExpiringSoon(@Param("userId") Long userId,
                                         @Param("now") LocalDateTime now,
                                         @Param("expireTime") LocalDateTime expireTime);
}
