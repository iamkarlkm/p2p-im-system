package com.im.backend.modules.local_life.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local_life.checkin.entity.CheckinRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 签到记录Mapper
 */
@Mapper
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {
}
