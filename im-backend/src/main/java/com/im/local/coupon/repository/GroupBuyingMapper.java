package com.im.local.coupon.repository;

import com.im.local.coupon.entity.GroupBuying;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 拼团数据访问层
 */
@Mapper
public interface GroupBuyingMapper {

    @Insert("INSERT INTO im_group_buying (activity_id, leader_id, required_members, current_members, " +
            "status, valid_hours, start_time, expire_time, group_price, product_id) " +
            "VALUES (#{activityId}, #{leaderId}, #{requiredMembers}, #{currentMembers}, " +
            "#{status}, #{validHours}, #{startTime}, #{expireTime}, #{groupPrice}, #{productId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GroupBuying groupBuying);

    @Select("SELECT * FROM im_group_buying WHERE id = #{id}")
    GroupBuying selectById(Long id);

    @Select("SELECT * FROM im_group_buying WHERE activity_id = #{activityId} AND status = 0")
    List<GroupBuying> selectActiveByActivityId(Long activityId);

    @Update("UPDATE im_group_buying SET current_members = current_members + 1, " +
            "status = CASE WHEN current_members + 1 >= required_members THEN 1 ELSE 0 END " +
            "WHERE id = #{id}")
    int incrementMembers(Long id);

    @Update("UPDATE im_group_buying SET status = 1 WHERE id = #{id}")
    int markAsSuccess(Long id);

    @Update("UPDATE im_group_buying SET status = 2 WHERE id = #{id} AND expire_time < NOW() AND status = 0")
    int expireGroups();
}
