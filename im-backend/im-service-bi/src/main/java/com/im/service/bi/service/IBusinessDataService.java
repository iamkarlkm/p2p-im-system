package com.im.service.bi.service;

import com.im.service.bi.dto.BusinessDashboardResponse;
import com.im.service.bi.dto.BusinessReportRequest;
import com.im.service.bi.dto.BusinessReportResponse;

/**
 * 经营数据服务接口
 */
public interface IBusinessDataService {

    /**
     * 获取经营数据看板
     */
    BusinessDashboardResponse getDashboard(Long merchantId);

    /**
     * 获取经营报表
     */
    BusinessReportResponse getBusinessReport(BusinessReportRequest request);

    /**
     * 获取实时经营指标
     */
    BusinessDashboardResponse getRealtimeMetrics(Long merchantId);
}
