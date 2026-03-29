package com.im.backend.modules.coupon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.im.backend.modules.coupon.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户优惠券Mapper接口 - 数据访问层
 * 
 * 功能说明:
 * 1. 用户优惠券CRUD操作
 * 2. 状态更新乐观锁实现
 * 3. 复杂统计查询
 * 4. 过期优惠券批量处理
 * 
 * SQL优化:
 * - 索引: user_id, coupon_id, status, valid_end_time
 * - 联合索引: (user_id, status) 用于用户券列表查询
 * - 联合索引: (status, valid_end_time) 用于过期处理
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    /**
     * 乐观锁更新状态
     * 
     * @param id ID
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE im_user_coupon SET status = #{toStatus}, version = version + 1, update_time = NOW() " +
            "WHERE id = #{id} AND status = #{fromStatus} AND version = #{version} AND deleted = 0")
    int updateStatusWithVersion(@Param("id") Long id, 
                                 @Param("fromStatus") Integer fromStatus,
                                 @Param("toStatus") Integer toStatus, 
                                 @Param("version") Integer version);
    
    /**
     * 统计用户各类状态优惠券数量
     * 
     * @param userId 用户ID
     * @return 统计结果
     */
    @Select("SELECT status, COUNT(*) as count FROM im_user_coupon " +
            "WHERE user_id = #{userId} AND deleted = 0 GROUP BY status")
    List<java.util.Map<String, Object>> countByUserGroupByStatus(@Param("userId") Long userId);
    
    /**
     * 查询用户即将过期的优惠券
     * 
     * @param userId 用户ID
     * @param beforeTime 截止时间
     * @return 用户优惠券列表
     */
    @Select("SELECT * FROM im_user_coupon WHERE user_id = #{userId} AND status = 0 AND deleted = 0 " +
            "AND valid_end_time <= #{beforeTime} AND valid_end_time > NOW() " +
            "AND upcoming_expire_reminder_sent = false")
    List<UserCoupon> selectExpiringSoonByUser(@Param("userId") Long userId, @Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 查询所有已过期的未处理优惠券（用于定时任务）
     * 
     * @param batchSize 批次大小
     * @return 用户优惠券列表
     */
    @Select("SELECT * FROM im_user_coupon WHERE status = 0 AND deleted = 0 " +
            "AND valid_end_time < NOW() LIMIT #{batchSize}")
    List<UserCoupon> selectExpiredBatch(@Param("batchSize") Integer batchSize);
    
    /**
     * 统计优惠券领取情况
     * 
     * @param couponId 优惠券ID
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) as total_received, " +
            "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as unused_count, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as used_count, " +
            "SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as expired_count " +
            "FROM im_user_coupon WHERE coupon_id = #{couponId} AND deleted = 0")
    java.util.Map<String, Object> countStatsByCoupon(@Param("couponId") Long couponId);
    
    /**
     * 查询用户的所有可用优惠券
     * 包含优惠券模板信息（联表查询）
     * 
     * @param userId 用户ID
     * @return 结果列表
     */
    @Select("SELECT uc.*, c.name as coupon_name, c.coupon_type, c.discount_value, " +
            "c.min_spend, c.max_discount, c.merchant_id, c.merchant_name " +
            "FROM im_user_coupon uc " +
            "INNER JOIN im_coupon c ON uc.coupon_id = c.id " +
            "WHERE uc.user_id = #{userId} AND uc.status = 0 AND uc.deleted = 0 " +
            "AND uc.valid_start_time <= NOW() AND uc.valid_end_time >= NOW()")
    List<java.util.Map<String, Object>> selectAvailableWithCouponInfo(@Param("userId") Long userId);
    
    /**
     * 批量更新已发送提醒标记
     * 
     * @param ids ID列表
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE im_user_coupon SET upcoming_expire_reminder_sent = true, " +
            "upcoming_expire_reminder_time = NOW() WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchUpdateReminderSent(@Param("ids") List<Long> ids);
    
    /**
     * 查询某优惠券某用户的领取数量
     * 
     * @param couponId 优惠券ID
     * @param userId 用户ID
     * @return 领取数量
     */
    @Select("SELECT COUNT(*) FROM im_user_coupon WHERE coupon_id = #{couponId} AND user_id = #{userId} AND deleted = 0")
    Long countByCouponAndUser(@Param("couponId") Long couponId, @Param("userId") Long userId);
    
    /**
     * 查询今日领取数量
     * 
     * @param couponId 优惠券ID
     * @param userId 用户ID
     * @return 今日领取数量
     */
    @Select("SELECT COUNT(*) FROM im_user_coupon WHERE coupon_id = #{couponId} AND user_id = #{userId} " +
            "AND DATE(create_time) = CURDATE() AND deleted = 0")
    Long countTodayByCouponAndUser(@Param("couponId") Long couponId, @Param("userId") Long userId);
    
    /**
     * 批量插入（用于批量发放场景）
     * 
     * @param list 用户优惠券列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<UserCoupon> list);
}
