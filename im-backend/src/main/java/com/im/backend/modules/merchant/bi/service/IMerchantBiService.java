package com.im.backend.modules.merchant.bi.service;

import com.im.backend.common.Result;
import com.im.backend.modules.merchant.bi.dto.BusinessOverviewResponse;
import com.im.backend.modules.merchant.bi.dto.DailyReportResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * 商户BI数据服务接口 - 功能#312: 商家BI数据智能平台
 */
public interface IMerchantBiService {

    /**
     * 获取经营概览
     */
    Result<BusinessOverviewResponse> getBusinessOverview(Long merchantId);

    /**
     * 获取日报列表
     */
    List<DailyReportResponse> getDailyReports(Long merchantId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取今日实时数据
     */
    DailyReportResponse getTodayRealtime(Long merchantId);
}
