package com.im.backend.modules.local.search.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local.search.entity.PoiKnowledgeGraph;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * POI知识图谱Mapper
 */
@Mapper
public interface PoiKnowledgeGraphMapper extends BaseMapper<PoiKnowledgeGraph> {

    /**
     * 根据实体名称查询
     */
    @Select("SELECT * FROM poi_knowledge_graph WHERE entity_name = #{entityName} LIMIT 1")
    PoiKnowledgeGraph selectByEntityName(@Param("entityName") String entityName);

    /**
     * 根据实体类型查询热门实体
     */
    @Select("SELECT * FROM poi_knowledge_graph WHERE entity_type = #{entityType} " +
            "ORDER BY hot_score DESC LIMIT #{limit}")
    List<PoiKnowledgeGraph> selectHotByType(@Param("entityType") String entityType, @Param("limit") Integer limit);

    /**
     * 模糊搜索实体
     */
    @Select("SELECT * FROM poi_knowledge_graph WHERE entity_name LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY weight_score DESC LIMIT #{limit}")
    List<PoiKnowledgeGraph> searchByKeyword(@Param("keyword") String keyword, @Param("limit") Integer limit);

    /**
     * 查询关联实体
     */
    @Select("SELECT * FROM poi_knowledge_graph WHERE entity_id IN " +
            "(SELECT related_entity_id FROM poi_entity_relation WHERE entity_id = #{entityId}) " +
            "ORDER BY weight_score DESC")
    List<PoiKnowledgeGraph> selectRelatedEntities(@Param("entityId") String entityId);
}
