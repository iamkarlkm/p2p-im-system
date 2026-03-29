package com.im.backend.modules.merchant.bi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantBiAlert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商家BI预警Mapper
 */
@Mapper
public interface MerchantBiAlertMapper extends BaseMapper<MerchantBiAlert> {
    
    /**
     * 标记所有预警为已读
     */
    @Update("UPDATE merchant_bi_alert SET is_read = 1 WHERE merchant_id = #{merchantId} AND is_read = 0")
    int markAllAsRead(@Param("merchantId") Long merchantId);
}
