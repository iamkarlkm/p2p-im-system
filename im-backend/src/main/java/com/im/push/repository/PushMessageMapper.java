package com.im.push.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.push.entity.PushMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 推送消息Mapper
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Mapper
public interface PushMessageMapper extends BaseMapper<PushMessage> {
    
    @Select("SELECT * FROM im_push_message WHERE user_id = #{userId} AND status = 0 LIMIT #{limit}")
    List<PushMessage> selectPendingByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    @Update("UPDATE im_push_message SET status = #{status}, push_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
