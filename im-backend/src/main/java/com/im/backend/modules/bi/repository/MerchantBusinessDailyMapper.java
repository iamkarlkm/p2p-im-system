package com.im.backend.modules.bi.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.bi.entity.MerchantBusinessDaily;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 商户经营日报数据访问层
 */
@Mapper
public interface MerchantBusinessDailyMapper extends BaseMapper<MerchantBusinessDaily> {

    /**
     * 查询日期范围内的经营数据
     */
    @Select("SELECT * FROM merchant_business_daily WHERE merchant_id = #{merchantId} " +
            "AND stat_date BETWEEN #{startDate} AND #{endDate} ORDER BY stat_date")
    List<MerchantBusinessDaily> selectByDateRange(@Param("merchantId") Long merchantId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    /**
     * 查询今日数据
     */
    @Select("SELECT * FROM merchant_business_daily WHERE merchant_id = #{merchantId} " +
            "AND stat_date = #{today}")
    MerchantBusinessDaily selectToday(@Param("merchantId") Long merchantId,
                                       @Param("today") LocalDate today);

    /**
     * 查询昨日数据
     */
    @Select("SELECT * FROM merchant_business_daily WHERE merchant_id = #{merchantId} " +
            "AND stat_date = #{yesterday}")
    MerchantBusinessDaily selectYesterday(@Param("merchantId") Long merchantId,
                                           @Param("yesterday") LocalDate yesterday);
}
