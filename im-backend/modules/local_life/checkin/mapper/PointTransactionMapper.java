package com.im.backend.modules.local_life.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local_life.checkin.entity.PointTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分交易记录Mapper
 */
@Mapper
public interface PointTransactionMapper extends BaseMapper<PointTransaction> {
}
