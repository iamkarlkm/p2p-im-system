package com.im.backend.modules.merchant.bi.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantBiDailyReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 商户经营日报数据访问层 - 功能#312: 商家BI数据智能平台
 */
@Repository
public interface MerchantBiDailyReportMapper extends BaseMapper<MerchantBiDailyReport> {

    /**
     * 查询日期范围报表
     */
    @Select("SELECT * FROM merchant_bi_daily_report WHERE merchant_id = #{merchantId} AND report_date BETWEEN #{startDate} AND #{endDate} ORDER BY report_date DESC")
    List<MerchantBiDailyReport> selectByDateRange(@Param("merchantId") Long merchantId, 
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    /**
     * 查询某日报表
     */
    @Select("SELECT * FROM merchant_bi_daily_report WHERE merchant_id = #{merchantId} AND report_date = #{date} LIMIT 1")
    MerchantBiDailyReport selectByDate(@Param("merchantId") Long merchantId, @Param("date") LocalDate date);
}
