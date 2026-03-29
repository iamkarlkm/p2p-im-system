package com.im.local.coupon.repository;

import com.im.local.coupon.entity.PointsTransaction;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 积分流水数据访问层
 */
@Mapper
public interface PointsTransactionMapper {

    @Insert("INSERT INTO im_points_transaction (user_id, merchant_id, points, balance_before, balance_after, " +
            "change_type, source_type, source_id, source_desc, order_no, order_amount, expire_time, remark) " +
            "VALUES (#{userId}, #{merchantId}, #{points}, #{balanceBefore}, #{balanceAfter}, " +
            "#{changeType}, #{sourceType}, #{sourceId}, #{sourceDesc}, #{orderNo}, #{orderAmount}, #{expireTime}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointsTransaction transaction);

    @Select("SELECT * FROM im_points_transaction WHERE id = #{id}")
    PointsTransaction selectById(Long id);

    @Select("SELECT * FROM im_points_transaction WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PointsTransaction> selectByUserId(Long userId);

    @Select("SELECT * FROM im_points_transaction WHERE user_id = #{userId} AND merchant_id = #{merchantId} " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    List<PointsTransaction> selectByUserAndMerchant(@Param("userId") Long userId, @Param("merchantId") Long merchantId, @Param("limit") Integer limit);
}
