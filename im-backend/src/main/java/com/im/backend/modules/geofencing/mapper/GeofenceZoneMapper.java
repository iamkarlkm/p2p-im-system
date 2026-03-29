package com.im.backend.modules.geofencing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.geofencing.entity.GeofenceZone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 地理围栏Mapper接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Mapper
public interface GeofenceZoneMapper extends BaseMapper<GeofenceZone> {

    /**
     * 根据GeoHash前缀查询围栏
     * @param geoHashPrefix GeoHash前缀
     * @return 围栏列表
     */
    @Select("SELECT * FROM geofence_zone WHERE geo_hash LIKE CONCAT(#{geoHashPrefix}, '%') AND enabled = 1 AND deleted = 0")
    List<GeofenceZone> selectByGeoHashPrefix(@Param("geoHashPrefix") String geoHashPrefix);

    /**
     * 查询指定区域内的围栏
     * @param minLng 最小经度
     * @param maxLng 最大经度
     * @param minLat 最小纬度
     * @param maxLat 最大纬度
     * @return 围栏列表
     */
    @Select("SELECT * FROM geofence_zone WHERE min_longitude >= #{minLng} AND max_longitude <= #{maxLng} " +
            "AND min_latitude >= #{minLat} AND max_latitude <= #{maxLat} AND enabled = 1 AND deleted = 0")
    List<GeofenceZone> selectByBoundingBox(@Param("minLng") BigDecimal minLng, @Param("maxLng") BigDecimal maxLng,
                                           @Param("minLat") BigDecimal minLat, @Param("maxLat") BigDecimal maxLat);

    /**
     * 查询商户的所有围栏
     * @param merchantId 商户ID
     * @return 围栏列表
     */
    @Select("SELECT * FROM geofence_zone WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY create_time DESC")
    List<GeofenceZone> selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 查询POI关联的围栏
     * @param poiId POI ID
     * @return 围栏列表
     */
    @Select("SELECT * FROM geofence_zone WHERE poi_id = #{poiId} AND enabled = 1 AND deleted = 0")
    List<GeofenceZone> selectByPoiId(@Param("poiId") Long poiId);

    /**
     * 统计商户围栏数量
     * @param merchantId 商户ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM geofence_zone WHERE merchant_id = #{merchantId} AND deleted = 0")
    Integer countByMerchantId(@Param("merchantId") Long merchantId);
}
