package com.im.service.bi.service;

import com.im.service.bi.dto.CompetitorBenchmarkResponse;

/**
 * 竞品对标服务接口
 */
public interface ICompetitorBenchmarkService {

    /**
     * 获取竞品对标分析
     */
    CompetitorBenchmarkResponse getBenchmarkAnalysis(Long merchantId);

    /**
     * 刷新对标数据
     */
    void refreshBenchmarkData(Long merchantId);

    /**
     * 获取商圈排名
     */
    CompetitorBenchmarkResponse getDistrictRanking(Long districtId, String category);
}
