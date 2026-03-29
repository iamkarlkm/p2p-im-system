package com.im.local.modules.coupon.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.local.modules.coupon.entity.Coupon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券数据访问层
 * @author IM Development Team
 * @since 2026-03-28
 */
@Repository
public interface CouponRepository extends BaseMapper<Coupon> {

    /**
     * 分页查询附近可用优惠券
     */
    @Select("SELECT c.*, m.name as merchantName, m.logo as merchantLogo, " +
            "ST_Distance_Sphere(point(c.longitude, c.latitude), point(#{lng}, #{lat})) as distance " +
            "FROM t_coupon c " +
            "LEFT JOIN t_merchant m ON c.merchant_id = m.id " +
            "WHERE c.status = 1 AND c.deleted = 0 " +
            "AND c.end_time > #{now} AND c.start_time < #{now} " +
            "AND c.received_quantity < c.total_quantity " +
            "HAVING distance < #{radius} " +
            "ORDER BY distance ASC, c.create_time DESC")
    IPage<Coupon> selectNearbyCoupons(Page<Coupon> page,
                                       @Param("lat") Double lat,
                                       @Param("lng") Double lng,
                                       @Param("radius") Double radius,
                                       @Param("now") LocalDateTime now);

    /**
     * 查询商户优惠券列表
     */
    @Select("SELECT * FROM t_coupon WHERE merchant_id = #{merchantId} " +
            "AND deleted = 0 ORDER BY create_time DESC")
    List<Coupon> selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 查询进行中且有效的优惠券
     */
    @Select("SELECT * FROM t_coupon WHERE status = 1 AND deleted = 0 " +
            "AND end_time > #{now} AND start_time < #{now} " +
            "AND received_quantity < total_quantity")
    List<Coupon> selectActiveCoupons(@Param("now") LocalDateTime now);

    /**
     * 原子扣减库存（使用乐观锁）
     */
    @Update("UPDATE t_coupon SET received_quantity = received_quantity + 1, " +
            "version = version + 1 " +
            "WHERE id = #{id} AND received_quantity < total_quantity " +
            "AND version = #{version}")
    int decrementStock(@Param("id") Long id, @Param("version") Integer version);

    /**
     * 查询新用户专属优惠券
     */
    @Select("SELECT * FROM t_coupon WHERE new_user_only = 1 AND status = 1 " +
            "AND deleted = 0 AND end_time > #{now}")
    List<Coupon> selectNewUserCoupons(@Param("now") LocalDateTime now);

    /**
     * 更新优惠券状态
     */
    @Update("UPDATE t_coupon SET status = #{status}, update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status,
                     @Param("updateTime") LocalDateTime updateTime);
}
