package com.im.backend.modules.merchant.bi.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantBusinessStats;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 商户经营统计数据访问层
 */
public interface MerchantBusinessStatsMapper extends BaseMapper<MerchantBusinessStats> {

    /**
     * 查询商户指定日期范围的经营统计
     */
    @Select("SELECT * FROM merchant_business_stats " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate} " +
            "AND stats_hour = -1 " +
            "ORDER BY stats_date")
    List<MerchantBusinessStats> selectByDateRange(@Param("merchantId") Long merchantId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    /**
     * 查询商户指定日期的时段分布
     */
    @Select("SELECT * FROM merchant_business_stats " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date = #{statsDate} " +
            "AND stats_hour >= 0 " +
            "ORDER BY stats_hour")
    List<MerchantBusinessStats> selectHourlyByDate(@Param("merchantId") Long merchantId,
                                                    @Param("statsDate") LocalDate statsDate);

    /**
     * 查询今日实时数据
     */
    @Select("SELECT * FROM merchant_business_stats " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date = CURDATE() " +
            "AND stats_hour = -1")
    MerchantBusinessStats selectTodayStats(@Param("merchantId") Long merchantId);

    /**
     * 查询同比数据
     */
    @Select("SELECT * FROM merchant_business_stats " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date = DATE_SUB(#{currentDate}, INTERVAL 1 YEAR) " +
            "AND stats_hour = -1")
    MerchantBusinessStats selectYearOverYear(@Param("merchantId") Long merchantId,
                                              @Param("currentDate") LocalDate currentDate);

    /**
     * 查询环比数据
     */
    @Select("SELECT * FROM merchant_business_stats " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date = DATE_SUB(#{currentDate}, INTERVAL 1 DAY) " +
            "AND stats_hour = -1")
    MerchantBusinessStats selectMonthOverMonth(@Param("merchantId") Long merchantId,
                                                @Param("currentDate") LocalDate currentDate);
}
