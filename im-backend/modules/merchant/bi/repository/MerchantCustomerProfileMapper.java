package com.im.backend.modules.merchant.bi.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantCustomerProfile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户画像数据访问层
 */
public interface MerchantCustomerProfileMapper extends BaseMapper<MerchantCustomerProfile> {

    /**
     * 查询年龄分布
     */
    @Select("SELECT age_group, COUNT(*) as count " +
            "FROM merchant_customer_profile " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY age_group")
    List<Map<String, Object>> selectAgeDistribution(@Param("merchantId") Long merchantId,
                                                     @Param("startDate") String startDate,
                                                     @Param("endDate") String endDate);

    /**
     * 查询性别分布
     */
    @Select("SELECT gender, COUNT(*) as count " +
            "FROM merchant_customer_profile " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY gender")
    List<Map<String, Object>> selectGenderDistribution(@Param("merchantId") Long merchantId,
                                                        @Param("startDate") String startDate,
                                                        @Param("endDate") String endDate);

    /**
     * 查询城市分布TOP10
     */
    @Select("SELECT city, COUNT(*) as count " +
            "FROM merchant_customer_profile " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY city " +
            "ORDER BY count DESC " +
            "LIMIT 10")
    List<Map<String, Object>> selectCityDistribution(@Param("merchantId") Long merchantId,
                                                      @Param("startDate") String startDate,
                                                      @Param("endDate") String endDate);

    /**
     * 查询消费频次分布
     */
    @Select("SELECT consumption_frequency, COUNT(*) as count " +
            "FROM merchant_customer_profile " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY consumption_frequency")
    List<Map<String, Object>> selectFrequencyDistribution(@Param("merchantId") Long merchantId,
                                                           @Param("startDate") String startDate,
                                                           @Param("endDate") String endDate);

    /**
     * 查询地理热力图数据
     */
    @Select("SELECT longitude, latitude, COUNT(*) as intensity, district " +
            "FROM merchant_customer_profile " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate} " +
            "AND longitude IS NOT NULL AND latitude IS NOT NULL " +
            "GROUP BY geo_hash")
    List<Map<String, Object>> selectGeoHeatmapData(@Param("merchantId") Long merchantId,
                                                    @Param("startDate") String startDate,
                                                    @Param("endDate") String endDate);
}
