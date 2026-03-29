package com.im.backend.modules.local_life.checkin.service;

import com.im.backend.modules.local_life.checkin.dto.*;

import java.util.List;

/**
 * 签到服务接口
 */
public interface CheckinService {

    /**
     * 用户签到
     */
    CheckinResponse checkin(Long userId, CheckinRequest request);

    /**
     * 检查今日是否已签到
     */
    boolean hasCheckedInToday(Long userId);

    /**
     * 获取用户签到记录
     */
    List<CheckinRecordDTO> getUserCheckinRecords(Long userId, Integer page, Integer size);

    /**
     * 获取用户连续签到天数
     */
    Integer getStreakDays(Long userId);

    /**
     * 获取签到日历
     */
    List<String> getCheckinCalendar(Long userId, String month);

    /**
     * 获取附近签到的人
     */
    List<NearbyCheckinDTO> getNearbyCheckins(Double longitude, Double latitude, Integer radius, Integer limit);
}
