package com.im.backend.modules.delivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.delivery.entity.DeliveryOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 配送订单Mapper
 */
@Mapper
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrder> {
}
