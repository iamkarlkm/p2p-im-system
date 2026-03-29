package com.im.local.coupon.repository;

import com.im.local.coupon.entity.BargainActivity;
import com.im.local.coupon.entity.BargainHelpRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 砍价数据访问层
 */
@Mapper
public interface BargainActivityMapper {

    @Insert("INSERT INTO im_bargain_activity (activity_id, user_id, original_price, floor_price, " +
            "current_price, bargained_amount, status, helper_count, valid_hours, start_time, expire_time) " +
            "VALUES (#{activityId}, #{userId}, #{originalPrice}, #{floorPrice}, " +
            "#{currentPrice}, #{bargainedAmount}, #{status}, #{helperCount}, #{validHours}, #{startTime}, #{expireTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BargainActivity bargainActivity);

    @Select("SELECT * FROM im_bargain_activity WHERE id = #{id}")
    BargainActivity selectById(Long id);

    @Select("SELECT * FROM im_bargain_activity WHERE user_id = #{userId} AND status = 0")
    List<BargainActivity> selectActiveByUserId(Long userId);

    @Update("UPDATE im_bargain_activity SET current_price = #{currentPrice}, " +
            "bargained_amount = #{bargainedAmount}, helper_count = helper_count + 1 " +
            "WHERE id = #{id}")
    int updateBargainProgress(@Param("id") Long id, @Param("currentPrice") java.math.BigDecimal currentPrice,
                              @Param("bargainedAmount") java.math.BigDecimal bargainedAmount);

    @Update("UPDATE im_bargain_activity SET status = 1 WHERE id = #{id}")
    int markAsSuccess(Long id);

    @Insert("INSERT INTO im_bargain_help_record (bargain_id, helper_id, bargain_amount, is_new_user, help_time) " +
            "VALUES (#{bargainId}, #{helperId}, #{bargainAmount}, #{isNewUser}, #{helpTime})")
    int insertHelpRecord(BargainHelpRecord record);
}
