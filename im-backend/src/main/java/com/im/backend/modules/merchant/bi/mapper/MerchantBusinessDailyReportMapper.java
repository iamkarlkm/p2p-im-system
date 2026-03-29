package com.im.backend.modules.merchant.bi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantBusinessDailyReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家经营日报表Mapper
 */
@Mapper
public interface MerchantBusinessDailyReportMapper extends BaseMapper<MerchantBusinessDailyReport> {
    
    /**
     * 查询日期范围内的报表
     */
    @Select("SELECT * FROM merchant_business_daily_report WHERE merchant_id = #{merchantId} AND report_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY report_date DESC")
    List<MerchantBusinessDailyReport> selectByDateRange(@Param("merchantId") Long merchantId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 查询最近N天的报表
     */
    @Select("SELECT * FROM merchant_business_daily_report WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY report_date DESC LIMIT #{limit}")
    List<MerchantBusinessDailyReport> selectRecentReports(@Param("merchantId") Long merchantId, @Param("limit") Integer limit);
    
    /**
     * 查询某日报表
     */
    @Select("SELECT * FROM merchant_business_daily_report WHERE merchant_id = #{merchantId} AND report_date = #{reportDate} AND deleted = 0 LIMIT 1")
    MerchantBusinessDailyReport selectByMerchantAndDate(@Param("merchantId") Long merchantId, @Param("reportDate") LocalDate reportDate);
}
