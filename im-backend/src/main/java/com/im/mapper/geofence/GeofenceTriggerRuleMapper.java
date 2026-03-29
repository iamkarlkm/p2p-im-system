package com.im.mapper.geofence;

import com.im.entity.geofence.GeofenceTriggerRule;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 地理围栏触发规则 Mapper
 * GeoFence Trigger Rule Mapper
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Mapper
public interface GeofenceTriggerRuleMapper {
    
    @Insert("INSERT INTO geofence_trigger_rule (rule_id, rule_name, rule_description, rule_type, " +
            "poi_id, poi_name, center_latitude, center_longitude, radius, fence_geometry, fence_type, " +
            "trigger_event, min_stay_seconds, max_trigger_count, trigger_cooldown_minutes, " +
            "time_restricted, effective_time_start, effective_time_end, effective_days, " +
            "target_user_type, target_user_tags, member_only, min_user_level, first_time_visitor, regular_customer, " +
            "message_type, message_title, message_content, message_template_id, message_extras, " +
            "action_type, action_url, action_mini_program_id, action_mini_program_path, " +
            "deduplication_enabled, deduplication_scope, deduplication_time_window, " +
            "a_b_test_enabled, a_b_test_group, priority, status, trigger_count, message_sent_count, " +
            "message_open_count, conversion_rate, parent_fence_id, fence_level, nested_fence_enabled, " +
            "created_at, updated_at, created_by, updated_by) " +
            "VALUES (#{ruleId}, #{ruleName}, #{ruleDescription}, #{ruleType}, " +
            "#{poiId}, #{poiName}, #{centerLatitude}, #{centerLongitude}, #{radius}, #{fenceGeometry}, #{fenceType}, " +
            "#{triggerEvent}, #{minStaySeconds}, #{maxTriggerCount}, #{triggerCooldownMinutes}, " +
            "#{timeRestricted}, #{effectiveTimeStart}, #{effectiveTimeEnd}, #{effectiveDays}, " +
            "#{targetUserType}, #{targetUserTags}, #{memberOnly}, #{minUserLevel}, #{firstTimeVisitor}, #{regularCustomer}, " +
            "#{messageType}, #{messageTitle}, #{messageContent}, #{messageTemplateId}, #{messageExtras}, " +
            "#{actionType}, #{actionUrl}, #{actionMiniProgramId}, #{actionMiniProgramPath}, " +
            "#{deduplicationEnabled}, #{deduplicationScope}, #{deduplicationTimeWindow}, " +
            "#{aBTestEnabled}, #{aBTestGroup}, #{priority}, #{status}, #{triggerCount}, #{messageSentCount}, " +
            "#{messageOpenCount}, #{conversionRate}, #{parentFenceId}, #{fenceLevel}, #{nestedFenceEnabled}, " +
            "#{createdAt}, #{updatedAt}, #{createdBy}, #{updatedBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GeofenceTriggerRule rule);
    
    @Update("UPDATE geofence_trigger_rule SET " +
            "rule_name = #{ruleName}, rule_description = #{ruleDescription}, rule_type = #{ruleType}, " +
            "poi_id = #{poiId}, poi_name = #{poiName}, center_latitude = #{centerLatitude}, " +
            "center_longitude = #{centerLongitude}, radius = #{radius}, fence_geometry = #{fenceGeometry}, " +
            "fence_type = #{fenceType}, trigger_event = #{triggerEvent}, min_stay_seconds = #{minStaySeconds}, " +
            "max_trigger_count = #{maxTriggerCount}, trigger_cooldown_minutes = #{triggerCooldownMinutes}, " +
            "time_restricted = #{timeRestricted}, effective_time_start = #{effectiveTimeStart}, " +
            "effective_time_end = #{effectiveTimeEnd}, effective_days = #{effectiveDays}, " +
            "target_user_type = #{targetUserType}, target_user_tags = #{targetUserTags}, " +
            "member_only = #{memberOnly}, min_user_level = #{minUserLevel}, first_time_visitor = #{firstTimeVisitor}, " +
            "regular_customer = #{regularCustomer}, message_type = #{messageType}, message_title = #{messageTitle}, " +
            "message_content = #{messageContent}, message_template_id = #{messageTemplateId}, " +
            "message_extras = #{messageExtras}, action_type = #{actionType}, action_url = #{actionUrl}, " +
            "action_mini_program_id = #{actionMiniProgramId}, action_mini_program_path = #{actionMiniProgramPath}, " +
            "deduplication_enabled = #{deduplicationEnabled}, deduplication_scope = #{deduplicationScope}, " +
            "deduplication_time_window = #{deduplicationTimeWindow}, a_b_test_enabled = #{aBTestEnabled}, " +
            "a_b_test_group = #{aBTestGroup}, priority = #{priority}, status = #{status}, " +
            "trigger_count = #{triggerCount}, message_sent_count = #{messageSentCount}, " +
            "message_open_count = #{messageOpenCount}, conversion_rate = #{conversionRate}, " +
            "parent_fence_id = #{parentFenceId}, fence_level = #{fenceLevel}, " +
            "nested_fence_enabled = #{nestedFenceEnabled}, updated_at = #{updatedAt}, updated_by = #{updatedBy} " +
            "WHERE rule_id = #{ruleId}")
    int update(GeofenceTriggerRule rule);
    
    @Delete("DELETE FROM geofence_trigger_rule WHERE rule_id = #{ruleId}")
    int deleteByRuleId(@Param("ruleId") String ruleId);
    
    @Select("SELECT * FROM geofence_trigger_rule WHERE rule_id = #{ruleId}")
    GeofenceTriggerRule selectByRuleId(@Param("ruleId") String ruleId);
    
    @Select("SELECT * FROM geofence_trigger_rule WHERE poi_id = #{poiId} AND status = 'ENABLED'")
    List<GeofenceTriggerRule> selectByPoiId(@Param("poiId") String poiId);
    
    @Select("SELECT * FROM geofence_trigger_rule WHERE rule_type = #{ruleType} AND status = 'ENABLED'")
    List<GeofenceTriggerRule> selectByRuleType(@Param("ruleType") String ruleType);
    
    @Select("SELECT * FROM geofence_trigger_rule WHERE status = 'ENABLED'")
    List<GeofenceTriggerRule> selectAllActive();
    
    @Select("SELECT * FROM geofence_trigger_rule")
    List<GeofenceTriggerRule> selectAll();
    
    @Update("UPDATE geofence_trigger_rule SET status = #{status}, updated_at = NOW() WHERE rule_id = #{ruleId}")
    int updateStatus(@Param("ruleId") String ruleId, @Param("status") String status);
    
    @Update("UPDATE geofence_trigger_rule SET trigger_count = trigger_count + 1 WHERE rule_id = #{ruleId}")
    int incrementTriggerCount(@Param("ruleId") String ruleId);
    
    @Update("UPDATE geofence_trigger_rule SET message_sent_count = message_sent_count + 1 WHERE rule_id = #{ruleId}")
    int incrementMessageSentCount(@Param("ruleId") String ruleId);
    
    @Update("UPDATE geofence_trigger_rule SET message_open_count = message_open_count + 1 WHERE rule_id = #{ruleId}")
    int incrementMessageOpenCount(@Param("ruleId") String ruleId);
}
