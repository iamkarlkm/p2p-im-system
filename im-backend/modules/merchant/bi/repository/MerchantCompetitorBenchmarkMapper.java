package com.im.backend.modules.merchant.bi.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantCompetitorBenchmark;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 竞品对标数据访问层
 */
public interface MerchantCompetitorBenchmarkMapper extends BaseMapper<MerchantCompetitorBenchmark> {

    /**
     * 查询商户对标数据
     */
    @Select("SELECT * FROM merchant_competitor_benchmark " +
            "WHERE merchant_id = #{merchantId} " +
            "AND benchmark_type = #{benchmarkType} " +
            "ORDER BY create_time DESC " +
            "LIMIT 1")
    MerchantCompetitorBenchmark selectLatestByType(@Param("merchantId") Long merchantId,
                                                    @Param("benchmarkType") String benchmarkType);

    /**
     * 查询商户排名趋势
     */
    @Select("SELECT * FROM merchant_competitor_benchmark " +
            "WHERE merchant_id = #{merchantId} " +
            "AND benchmark_type = #{benchmarkType} " +
            "ORDER BY create_time DESC " +
            "LIMIT #{limit}")
    List<MerchantCompetitorBenchmark> selectTrendByType(@Param("merchantId") Long merchantId,
                                                         @Param("benchmarkType") String benchmarkType,
                                                         @Param("limit") Integer limit);
}
