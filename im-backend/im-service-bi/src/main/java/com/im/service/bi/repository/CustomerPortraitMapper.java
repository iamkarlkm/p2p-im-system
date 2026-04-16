package com.im.service.bi.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.service.bi.entity.CustomerPortrait;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户画像数据访问层
 */
@Mapper
public interface CustomerPortraitMapper extends BaseMapper<CustomerPortrait> {

    /**
     * 统计用户地域分布
     */
    @Select("SELECT region_name as regionName, COUNT(*) as count " +
            "FROM customer_portrait WHERE merchant_id = #{merchantId} " +
            "GROUP BY region_name ORDER BY count DESC")
    List<Map<String, Object>> selectRegionDistribution(@Param("merchantId") Long merchantId);

    /**
     * 统计RFM分层分布
     */
    @Select("SELECT rfm_segment as segment, COUNT(*) as count " +
            "FROM customer_portrait WHERE merchant_id = #{merchantId} " +
            "GROUP BY rfm_segment ORDER BY count DESC")
    List<Map<String, Object>> selectRfmDistribution(@Param("merchantId") Long merchantId);

    /**
     * 统计新老客数量
     */
    @Select("SELECT lifecycle_stage, COUNT(*) as count " +
            "FROM customer_portrait WHERE merchant_id = #{merchantId} " +
            "GROUP BY lifecycle_stage")
    List<Map<String, Object>> selectLifecycleDistribution(@Param("merchantId") Long merchantId);

    /**
     * 查询顾客总数
     */
    @Select("SELECT COUNT(*) FROM customer_portrait WHERE merchant_id = #{merchantId}")
    Integer selectTotalCustomers(@Param("merchantId") Long merchantId);
}
