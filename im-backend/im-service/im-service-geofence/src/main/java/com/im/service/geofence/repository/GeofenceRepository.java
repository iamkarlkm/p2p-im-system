package com.im.service.geofence.repository;

import com.im.service.geofence.entity.Geofence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 地理围栏数据访问层
 */
@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {

    Optional<Geofence> findByGeofenceId(String geofenceId);

    List<Geofence> findByMerchantId(String merchantId);

    List<Geofence> findByPoiId(String poiId);

    List<Geofence> findByMerchantIdAndStatus(String merchantId, String status);

    List<Geofence> findByParentId(String parentId);

    Page<Geofence> findByStatus(String status, Pageable pageable);

    @Query("SELECT g FROM Geofence g WHERE g.status = 'ACTIVE' AND g.enabled = true")
    List<Geofence> findAllActive();

    @Query("SELECT g FROM Geofence g WHERE g.geoHash LIKE CONCAT(:geoHashPrefix, '%')")
    List<Geofence> findByGeoHashPrefix(@Param("geoHashPrefix") String geoHashPrefix);

    @Query("SELECT g FROM Geofence g WHERE g.status = 'ACTIVE' AND g.enabled = true " +
           "AND ((g.fenceType = 'CIRCLE' AND g.centerLatitude IS NOT NULL) " +
           "OR (g.fenceType = 'POLYGON' AND g.coordinates IS NOT NULL))")
    List<Geofence> findAllActiveWithLocation();
}
