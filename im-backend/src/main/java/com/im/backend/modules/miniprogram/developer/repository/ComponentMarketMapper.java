package com.im.backend.modules.miniprogram.developer.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.miniprogram.developer.entity.ComponentMarket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 组件市场数据访问层
 */
@Mapper
public interface ComponentMarketMapper extends BaseMapper<ComponentMarket> {
    
    /**
     * 根据组件key查询
     */
    @Select("SELECT * FROM component_market WHERE component_key = #{componentKey}")
    ComponentMarket selectByComponentKey(@Param("componentKey") String componentKey);
    
    /**
     * 根据分类查询组件
     */
    @Select("SELECT * FROM component_market WHERE category = #{category} AND status = 1 ORDER BY download_count DESC")
    List<ComponentMarket> selectByCategory(@Param("category") String category);
    
    /**
     * 查询热门组件
     */
    @Select("SELECT * FROM component_market WHERE status = 1 ORDER BY download_count DESC LIMIT #{limit}")
    List<ComponentMarket> selectHotComponents(@Param("limit") Integer limit);
    
    /**
     * 查询开发者发布的组件
     */
    @Select("SELECT * FROM component_market WHERE developer_id = #{developerId} ORDER BY create_time DESC")
    List<ComponentMarket> selectByDeveloperId(@Param("developerId") Long developerId);
    
    /**
     * 增加下载次数
     */
    @Update("UPDATE component_market SET download_count = download_count + 1 WHERE id = #{id}")
    int incrementDownloadCount(@Param("id") Long id);
    
    /**
     * 更新评分
     */
    @Update("UPDATE component_market SET rating = #{rating}, rating_count = rating_count + 1 WHERE id = #{id}")
    int updateRating(@Param("id") Long id, @Param("rating") java.math.BigDecimal rating);
}
