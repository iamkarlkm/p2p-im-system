package com.im.local.coupon.repository;

import com.im.local.coupon.entity.UserMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户会员数据访问层
 */
@Mapper
public interface UserMemberMapper {

    @Insert("INSERT INTO im_user_member (user_id, merchant_id, current_level_id, current_level, " +
            "growth_value, points_balance, total_points, total_spend, total_orders, status, join_time) " +
            "VALUES (#{userId}, #{merchantId}, #{currentLevelId}, #{currentLevel}, " +
            "0, 0, 0, 0, 0, 1, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserMember userMember);

    @Select("SELECT * FROM im_user_member WHERE id = #{id}")
    UserMember selectById(Long id);

    @Select("SELECT * FROM im_user_member WHERE user_id = #{userId} AND merchant_id = #{merchantId}")
    UserMember selectByUserAndMerchant(@Param("userId") Long userId, @Param("merchantId") Long merchantId);

    @Select("SELECT * FROM im_user_member WHERE user_id = #{userId} ORDER BY total_spend DESC")
    List<UserMember> selectByUserId(Long userId);

    @Update("UPDATE im_user_member SET growth_value = growth_value + #{growth}, " +
            "total_spend = total_spend + #{amount}, total_orders = total_orders + 1, " +
            "last_consume_time = NOW(), last_consume_amount = #{amount}, update_time = NOW() " +
            "WHERE id = #{id}")
    int addGrowthAndSpend(@Param("id") Long id, @Param("growth") Integer growth, @Param("amount") java.math.BigDecimal amount);

    @Update("UPDATE im_user_member SET current_level_id = #{levelId}, current_level = #{level}, " +
            "update_time = NOW() WHERE id = #{id}")
    int updateLevel(@Param("id") Long id, @Param("levelId") Long levelId, @Param("level") Integer level);

    @Update("UPDATE im_user_member SET points_balance = points_balance + #{points}, " +
            "total_points = total_points + #{points}, update_time = NOW() WHERE id = #{id}")
    int addPoints(@Param("id") Long id, @Param("points") Integer points);

    @Update("UPDATE im_user_member SET points_balance = points_balance - #{points}, " +
            "update_time = NOW() WHERE id = #{id} AND points_balance >= #{points}")
    int deductPoints(@Param("id") Long id, @Param("points") Integer points);
}
