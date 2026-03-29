package com.im.backend.modules.geofence.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.geofence.entity.Geofence;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地理围栏Mapper
 */
public interface GeofenceMapper extends BaseMapper<Geofence> {

    /**
     * 根据商户ID查询围栏列表
     */
    @Select("SELECT * FROM im_geofence WHERE merchant_id = #{merchantId} AND status = 'ACTIVE' AND deleted = 0")
    List<Geofence> selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 根据门店ID查询围栏列表
     */
    @Select("SELECT * FROM im_geofence WHERE store_id = #{storeId} AND status = 'ACTIVE' AND deleted = 0")
    List<Geofence> selectByStoreId(@Param("storeId") Long storeId);

    /**
     * 根据用途查询围栏
     */
    @Select("SELECT * FROM im_geofence WHERE purpose = #{purpose} AND status = 'ACTIVE' AND deleted = 0")
    List<Geofence> selectByPurpose(@Param("purpose") String purpose);

    /**
     * 分页查询商户围栏
     */
    IPage<Geofence> selectPageByMerchantId(Page<Geofence> page, @Param("merchantId") Long merchantId);

    /**
     * 查询所有激活的围栏
     */
    @Select("SELECT * FROM im_geofence WHERE status = 'ACTIVE' AND deleted = 0")
    List<Geofence> selectAllActive();
}
