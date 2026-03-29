package com.im.local.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.local.review.entity.MerchantReviewMedia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评价媒体数据访问层
 */
@Mapper
public interface MerchantReviewMediaMapper extends BaseMapper<MerchantReviewMedia> {

    /**
     * 根据评价ID查询媒体列表
     */
    @Select("SELECT * FROM merchant_review_media WHERE review_id = #{reviewId} AND deleted = 0 ORDER BY sort_order")
    List<MerchantReviewMedia> selectByReviewId(@Param("reviewId") Long reviewId);

    /**
     * 批量插入媒体
     */
    int batchInsert(@Param("list") List<MerchantReviewMedia> mediaList);
}
