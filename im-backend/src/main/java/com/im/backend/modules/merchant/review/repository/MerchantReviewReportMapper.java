package com.im.backend.modules.merchant.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.review.entity.MerchantReviewReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 评价举报Mapper
 */
@Mapper
public interface MerchantReviewReportMapper extends BaseMapper<MerchantReviewReport> {

    /**
     * 查询评价是否已被用户举报
     */
    @Select("SELECT COUNT(*) FROM merchant_review_report WHERE review_id = #{reviewId} AND reporter_id = #{reporterId}")
    int countByReviewAndReporter(@Param("reviewId") String reviewId, @Param("reporterId") Long reporterId);

    /**
     * 查询评价的举报数量
     */
    @Select("SELECT COUNT(*) FROM merchant_review_report WHERE review_id = #{reviewId}")
    int countByReviewId(@Param("reviewId") String reviewId);

    /**
     * 查询待处理的举报列表
     */
    @Select("SELECT * FROM merchant_review_report WHERE status = 0 ORDER BY created_at DESC")
    List<MerchantReviewReport> selectPendingReports();

    /**
     * 处理举报
     */
    @Update("UPDATE merchant_review_report SET status = #{status}, result = #{result}, handler_id = #{handlerId}, handled_at = NOW() WHERE report_id = #{reportId}")
    int handleReport(@Param("reportId") String reportId, @Param("status") Integer status, 
                     @Param("result") String result, @Param("handlerId") Long handlerId);
}
