package com.im.mapper.discovery;

import com.im.entity.discovery.DiscoveryNewStore;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 新店发现Mapper接口
 */
@Mapper
public interface DiscoveryNewStoreMapper {
    
    @Select("SELECT * FROM discovery_new_store WHERE city_code = #{cityCode} " +
            "AND status = 'ACTIVE' AND deleted = 0 " +
            "ORDER BY heat_score DESC LIMIT #{offset}, #{limit}")
    List<DiscoveryNewStore> selectByCityAndLocation(@Param("cityCode") String cityCode,
                                                     @Param("longitude") Double longitude,
                                                     @Param("latitude") Double latitude,
                                                     @Param("offset") Integer offset,
                                                     @Param("limit") Integer limit);
    
    @Insert("INSERT INTO discovery_new_store (poi_id, merchant_id, store_name, opening_time, " +
            "opening_type, category_id, category_name, longitude, latitude, address, " +
            "business_district, city_code, city_name, images, cover_image, introduction, " +
            "feature_tags, opening_promotions, promotions, avg_price, business_hours, phone, " +
            "heat_score, status, create_time, update_time) VALUES " +
            "(#{poiId}, #{merchantId}, #{storeName}, #{openingTime}, #{openingType}, " +
            "#{categoryId}, #{categoryName}, #{longitude}, #{latitude}, #{address}, " +
            "#{businessDistrict}, #{cityCode}, #{cityName}, #{images}, #{coverImage}, " +
            "#{introduction}, #{featureTags}, #{openingPromotions}, #{promotions}, " +
            "#{avgPrice}, #{businessHours}, #{phone}, #{heatScore}, #{status}, " +
            "NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiscoveryNewStore newStore);
    
    @Update("UPDATE discovery_new_store SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_new_store SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    int incrementFavoriteCount(@Param("id") Long id);
    
    @Update("UPDATE discovery_new_store SET want_to_go_count = want_to_go_count + 1 WHERE id = #{id}")
    int incrementWantToGoCount(@Param("id") Long id);
}
