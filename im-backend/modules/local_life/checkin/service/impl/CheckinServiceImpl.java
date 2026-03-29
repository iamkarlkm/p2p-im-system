package com.im.backend.modules.local_life.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.im.backend.modules.local_life.checkin.dto.*;
import com.im.backend.modules.local_life.checkin.entity.CheckinRecord;
import com.im.backend.modules.local_life.checkin.enums.CheckinStatus;
import com.im.backend.modules.local_life.checkin.enums.PointType;
import com.im.backend.modules.local_life.checkin.mapper.CheckinRecordMapper;
import com.im.backend.modules.local_life.checkin.service.BadgeService;
import com.im.backend.modules.local_life.checkin.service.CheckinService;
import com.im.backend.modules.local_life.checkin.service.PointService;
import com.im.backend.modules.local_life.checkin.util.GeoHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 签到服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements CheckinService {

    private final CheckinRecordMapper checkinRecordMapper;
    private final PointService pointService;
    private final BadgeService badgeService;

    @Override
    @Transactional
    public CheckinResponse checkin(Long userId, CheckinRequest request) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        
        // 检查今日是否已签到
        if (hasCheckedInToday(userId)) {
            CheckinResponse response = new CheckinResponse();
            response.setStatus(CheckinStatus.ALREADY_CHECKED.getCode());
            response.setStatusDesc(CheckinStatus.ALREADY_CHECKED.getDesc());
            return response;
        }

        // 计算GeoHash
        String geoHash = GeoHashUtil.encode(request.getLatitude(), request.getLongitude(), 8);

        // 检查是否是首次在该POI签到
        boolean isFirstTimeAtPoi = isFirstTimeAtPoi(userId, request.getPoiId());

        // 获取连续签到天数
        int streakDays = calculateStreakDays(userId);

        // 创建签到记录
        CheckinRecord record = new CheckinRecord();
        record.setUserId(userId);
        record.setPoiId(request.getPoiId());
        record.setPoiName(request.getPoiName());
        record.setPoiType(request.getPoiType());
        record.setLongitude(request.getLongitude());
        record.setLatitude(request.getLatitude());
        record.setGeoHash(geoHash);
        record.setStatus(CheckinStatus.SUCCESS.getCode());
        record.setStreakDays(streakDays);
        record.setFirstTimeAtPoi(isFirstTimeAtPoi);
        record.setDeviceId(request.getDeviceId());
        record.setCheckinTime(LocalDateTime.now());
        record.setCheckinDate(today);
        record.setRemark(request.getRemark());
        
        checkinRecordMapper.insert(record);

        // 计算并增加积分
        int earnedPoints = pointService.calculateCheckinPoints(userId, isFirstTimeAtPoi, streakDays);
        pointService.addPoints(userId, earnedPoints, PointType.CHECKIN.name(), record.getId(), "签到获得积分");

        // 检查徽章
        List<CheckinResponse.BadgeInfo> newBadges = badgeService.checkAndGrantBadges(userId, today);

        // 构建响应
        CheckinResponse response = new CheckinResponse();
        response.setCheckinId(record.getId());
        response.setStatus(CheckinStatus.SUCCESS.getCode());
        response.setStatusDesc(CheckinStatus.SUCCESS.getDesc());
        response.setEarnedPoints(earnedPoints);
        response.setStreakDays(streakDays);
        response.setFirstTimeAtPoi(isFirstTimeAtPoi);
        response.setNewBadges(newBadges);
        response.setCheckinTime(record.getCheckinTime());

        return response;
    }

    @Override
    public boolean hasCheckedInToday(Long userId) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinRecord::getUserId, userId)
               .eq(CheckinRecord::getCheckinDate, today);
        return checkinRecordMapper.selectCount(wrapper) > 0;
    }

    private boolean isFirstTimeAtPoi(Long userId, String poiId) {
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinRecord::getUserId, userId)
               .eq(CheckinRecord::getPoiId, poiId);
        return checkinRecordMapper.selectCount(wrapper) == 0;
    }

    private int calculateStreakDays(Long userId) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE);

        // 检查昨天是否签到
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinRecord::getUserId, userId)
               .eq(CheckinRecord::getCheckinDate, yesterday);
        
        if (checkinRecordMapper.selectCount(wrapper) == 0) {
            return 1; // 昨天没签到,重置为1
        }

        // 获取昨天签到的连续天数
        wrapper.orderByDesc(CheckinRecord::getId).last("LIMIT 1");
        CheckinRecord lastRecord = checkinRecordMapper.selectOne(wrapper);
        return lastRecord != null ? lastRecord.getStreakDays() + 1 : 1;
    }

    @Override
    public List<CheckinRecordDTO> getUserCheckinRecords(Long userId, Integer page, Integer size) {
        // 简化实现
        return List.of();
    }

    @Override
    public Integer getStreakDays(Long userId) {
        return calculateStreakDays(userId);
    }

    @Override
    public List<String> getCheckinCalendar(Long userId, String month) {
        // 简化实现
        return List.of();
    }

    @Override
    public List<NearbyCheckinDTO> getNearbyCheckins(Double longitude, Double latitude, Integer radius, Integer limit) {
        // 简化实现
        return List.of();
    }
}
