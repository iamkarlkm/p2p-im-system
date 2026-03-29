package com.im.backend.modules.bi.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.bi.entity.CompetitorBenchmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 竞品对标数据访问层
 */
@Mapper
public interface CompetitorBenchmarkMapper extends BaseMapper<CompetitorBenchmark> {

    /**
     * 查询商户最新对标数据
     */
    @Select("SELECT * FROM competitor_benchmark WHERE merchant_id = #{merchantId} " +
            "ORDER BY stat_time DESC LIMIT 1")
    CompetitorBenchmark selectLatest(@Param("merchantId") Long merchantId);

    /**
     * 查询排名趋势
     */
    @Select("SELECT DATE(stat_time) as date, ranking, merchant_rating as rating " +
            "FROM competitor_benchmark WHERE merchant_id = #{merchantId} " +
            "AND stat_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "ORDER BY stat_time")
    List<java.util.Map<String, Object>> selectRankingTrend(@Param("merchantId") Long merchantId);

    /**
     * 查询商圈排名列表
     */
    @Select("SELECT merchant_id, ranking, merchant_rating, merchant_monthly_sales " +
            "FROM competitor_benchmark WHERE business_district_id = #{districtId} " +
            "AND category = #{category} ORDER BY ranking")
    List<CompetitorBenchmark> selectDistrictRanking(@Param("districtId") Long districtId,
                                                     @Param("category") String category);
}
