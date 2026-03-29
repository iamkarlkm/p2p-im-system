package com.im.backend.modules.poi.customer_service.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.poi.customer_service.entity.PoiCustomerServiceFaq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 客服FAQ Mapper
 */
@Mapper
public interface PoiCustomerServiceFaqMapper extends BaseMapper<PoiCustomerServiceFaq> {

    /**
     * 查询POI的FAQ列表
     */
    @Select("SELECT * FROM poi_cs_faq WHERE poi_id = #{poiId} AND enabled = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<PoiCustomerServiceFaq> selectByPoiId(@Param("poiId") Long poiId);

    /**
     * 查询平台通用FAQ
     */
    @Select("SELECT * FROM poi_cs_faq WHERE poi_id = 0 AND enabled = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<PoiCustomerServiceFaq> selectCommonFaqs();

    /**
     * 查询POI或平台的FAQ
     */
    @Select("SELECT * FROM poi_cs_faq WHERE (poi_id = #{poiId} OR poi_id = 0) AND enabled = 1 AND deleted = 0 ORDER BY poi_id DESC, sort_order ASC")
    List<PoiCustomerServiceFaq> selectByPoiIdOrCommon(@Param("poiId") Long poiId);

    /**
     * 根据分类查询FAQ
     */
    @Select("SELECT * FROM poi_cs_faq WHERE poi_id = #{poiId} AND category = #{category} AND enabled = 1 AND deleted = 0")
    List<PoiCustomerServiceFaq> selectByCategory(@Param("poiId") Long poiId, @Param("category") String category);

    /**
     * 增加FAQ命中次数
     */
    @Update("UPDATE poi_cs_faq SET hit_count = hit_count + 1 WHERE id = #{faqId}")
    int incrementHitCount(@Param("faqId") Long faqId);

    /**
     * 增加有用次数
     */
    @Update("UPDATE poi_cs_faq SET helpful_count = helpful_count + 1 WHERE id = #{faqId}")
    int incrementHelpful(@Param("faqId") Long faqId);

    /**
     * 增加无用次数
     */
    @Update("UPDATE poi_cs_faq SET unhelpful_count = unhelpful_count + 1 WHERE id = #{faqId}")
    int incrementUnhelpful(@Param("faqId") Long faqId);

    /**
     * 搜索FAQ
     */
    @Select("SELECT * FROM poi_cs_faq WHERE (poi_id = #{poiId} OR poi_id = 0) AND (question LIKE CONCAT('%',#{keyword},'%') OR keywords LIKE CONCAT('%',#{keyword},'%')) AND enabled = 1 AND deleted = 0")
    List<PoiCustomerServiceFaq> searchFaqs(@Param("poiId") Long poiId, @Param("keyword") String keyword);
}
