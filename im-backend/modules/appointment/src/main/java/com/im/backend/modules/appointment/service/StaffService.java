package com.im.backend.modules.appointment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.core.result.PageResult;
import com.im.backend.modules.appointment.dto.*;
import com.im.backend.modules.appointment.entity.ServiceStaff;
import com.im.backend.modules.appointment.entity.StaffSchedule;

import java.time.LocalDate;
import java.util.List;

/**
 * 服务人员Service接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface StaffService extends IService<ServiceStaff> {

    /**
     * 创建服务人员
     *
     * @param request 创建请求
     * @return 人员信息
     */
    StaffResponse createStaff(CreateStaffRequest request);

    /**
     * 更新服务人员
     *
     * @param staffId 员工ID
     * @param request 更新请求
     * @return 人员信息
     */
    StaffResponse updateStaff(Long staffId, UpdateStaffRequest request);

    /**
     * 获取服务人员详情
     *
     * @param staffId 员工ID
     * @return 人员详情
     */
    StaffDetailResponse getStaffDetail(Long staffId);

    /**
     * 获取商户服务人员列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<StaffResponse> getMerchantStaffs(StaffQueryRequest request);

    /**
     * 获取门店服务人员列表
     *
     * @param storeId 门店ID
     * @return 人员列表
     */
    List<StaffResponse> getStoreStaffs(Long storeId);

    /**
     * 获取可预约的服务人员
     *
     * @param storeId 门店ID
     * @param serviceId 服务ID
     * @param date 日期
     * @return 人员列表
     */
    List<StaffResponse> getBookableStaffs(Long storeId, Long serviceId, LocalDate date);

    /**
     * 启用/禁用服务人员
     *
     * @param staffId 员工ID
     * @param enabled 是否启用
     * @return 是否成功
     */
    boolean toggleStaffStatus(Long staffId, Boolean enabled);

    /**
     * 删除服务人员
     *
     * @param staffId 员工ID
     * @return 是否成功
     */
    boolean deleteStaff(Long staffId);

    /**
     * 添加服务技能
     *
     * @param staffId 员工ID
     * @param serviceId 服务ID
     * @param proficiency 熟练度
     * @return 是否成功
     */
    boolean addServiceSkill(Long staffId, Long serviceId, Integer proficiency);

    /**
     * 移除服务技能
     *
     * @param staffId 员工ID
     * @param serviceId 服务ID
     * @return 是否成功
     */
    boolean removeServiceSkill(Long staffId, Long serviceId);

    /**
     * 更新技能熟练度
     *
     * @param staffId 员工ID
     * @param serviceId 服务ID
     * @param proficiency 熟练度
     * @return 是否成功
     */
    boolean updateSkillProficiency(Long staffId, Long serviceId, Integer proficiency);

    /**
     * 创建排班
     *
     * @param request 创建请求
     * @return 排班信息
     */
    StaffScheduleResponse createSchedule(CreateScheduleRequest request);

    /**
     * 批量创建排班
     *
     * @param requests 创建请求列表
     * @return 排班信息列表
     */
    List<StaffScheduleResponse> batchCreateSchedules(List<CreateScheduleRequest> requests);

    /**
     * 更新排班
     *
     * @param scheduleId 排班ID
     * @param request 更新请求
     * @return 排班信息
     */
    StaffScheduleResponse updateSchedule(Long scheduleId, UpdateScheduleRequest request);

    /**
     * 获取员工排班
     *
     * @param staffId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    List<StaffScheduleResponse> getStaffSchedules(Long staffId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取门店排班
     *
     * @param storeId 门店ID
     * @param date 日期
     * @return 排班列表
     */
    List<StaffScheduleResponse> getStoreSchedules(Long storeId, LocalDate date);

    /**
     * 删除排班
     *
     * @param scheduleId 排班ID
     * @return 是否成功
     */
    boolean deleteSchedule(Long scheduleId);

    /**
     * 复制排班
     *
     * @param sourceStaffId 源员工ID
     * @param targetStaffId 目标员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 是否成功
     */
    boolean copySchedule(Long sourceStaffId, Long targetStaffId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取服务人员统计
     *
     * @param merchantId 商户ID
     * @return 统计数据
     */
    StaffStatisticsResponse getStaffStatistics(Long merchantId);

    /**
     * 更新服务人员评分
     *
     * @param staffId 员工ID
     * @param rating 评分
     * @return 是否成功
     */
    boolean updateStaffRating(Long staffId, Integer rating);

    /**
     * 增加服务次数
     *
     * @param staffId 员工ID
     * @param serviceId 服务ID
     * @param duration 服务时长
     * @return 是否成功
     */
    boolean incrementServiceCount(Long staffId, Long serviceId, Integer duration);

    /**
     * 检查员工是否可预约
     *
     * @param staffId 员工ID
     * @param date 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否可预约
     */
    boolean checkStaffAvailability(Long staffId, LocalDate date, 
            java.time.LocalTime startTime, java.time.LocalTime endTime);

    /**
     * 获取推荐服务人员
     *
     * @param storeId 门店ID
     * @param serviceId 服务ID
     * @param date 日期
     * @param limit 数量限制
     * @return 推荐列表
     */
    List<StaffResponse> getRecommendedStaffs(Long storeId, Long serviceId, 
            LocalDate date, Integer limit);

    /**
     * 获取今日在岗员工
     *
     * @param storeId 门店ID
     * @return 员工列表
     */
    List<StaffResponse> getTodayWorkingStaffs(Long storeId);

    /**
     * 设置员工休假
     *
     * @param staffId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param reason 原因
     * @return 是否成功
     */
    boolean setStaffLeave(Long staffId, LocalDate startDate, LocalDate endDate, String reason);

    /**
     * 取消休假
     *
     * @param staffId 员工ID
     * @param date 日期
     * @return 是否成功
     */
    boolean cancelLeave(Long staffId, LocalDate date);
}
