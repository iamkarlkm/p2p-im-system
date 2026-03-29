package com.im.backend.service.impl;

import com.im.backend.dto.poi.*;
import com.im.backend.model.enums.PointTransactionType;
import com.im.backend.model.enums.UserLevel;
import com.im.backend.model.poi.*;
import com.im.backend.service.PoiCheckinService;
import com.im.backend.service.UserPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * POI签到服务实现
 */
@Service
public class PoiCheckinServiceImpl implements PoiCheckinService {

    @Autowired
    private UserPointService userPointService;

    // 模拟数据存储
    private final Map<Long, List<PoiCheckinRecord>> checkinRecords = new HashMap<>();
    private final Map<String, List<PoiCheckinRecord>> poiCheckins = new HashMap<>();
    private long nextId = 1;

    @Override
    @Transactional
    public PoiCheckinResponse checkin(Long userId, PoiCheckinRequest request) {
        PoiCheckinResponse response = new PoiCheckinResponse();
        
        // 检查今日是否已签到
        if (hasCheckedInToday(userId, request.getPoiId())) {
            response.setSuccess(false);
            response.setMessage("今日已签到，请明天再来");
            return response;
        }

        // 计算GeoHash
        String geoHash = encodeGeoHash(request.getLatitude(), request.getLongitude(), 8);
        
        // 获取用户积分账户
        UserPointAccountResponse account = userPointService.getUserPointAccount(userId);
        
        // 计算连续签到天数
        int consecutiveDays = calculateConsecutiveDays(userId, account.getLastCheckinTime());
        boolean isFirstCheckin = account.getTotalCheckins() == 0;
        
        // 计算基础积分
        int basePoints = 10;
        int bonusPoints = calculateBonusPoints(consecutiveDays, isFirstCheckin);
        int totalPoints = basePoints + bonusPoints;
        
        // 应用等级加成
        UserLevel level = UserLevel.valueOf(account.getCurrentLevel());
        totalPoints = (int) (totalPoints * level.getCheckinBonus());

        // 创建签到记录
        PoiCheckinRecord record = new PoiCheckinRecord();
        record.setId(nextId++);
        record.setUserId(userId);
        record.setPoiId(request.getPoiId());
        record.setPoiName(request.getPoiName());
        record.setPoiAddress(request.getPoiAddress());
        record.setLongitude(request.getLongitude());
        record.setLatitude(request.getLatitude());
        record.setGeoHash(geoHash);
        record.setCheckinTime(LocalDateTime.now());
        record.setPointsEarned(totalPoints);
        record.setCheckinType(request.getCheckinType());
        record.setCheckinContent(request.getCheckinContent());
        record.setImageUrls(request.getImageUrls());
        record.setIsPublic(request.getIsPublic());
        record.setDeviceFingerprint(request.getDeviceFingerprint());
        record.setConsecutiveDays(consecutiveDays);
        record.setIsFirstCheckin(isFirstCheckin);
        record.setIsValid(true);

        // 保存记录
        checkinRecords.computeIfAbsent(userId, k -> new ArrayList<>()).add(record);
        poiCheckins.computeIfAbsent(request.getPoiId(), k -> new ArrayList<>()).add(record);

        // 增加用户积分
        String description = String.format("在%s签到获得%d积分", request.getPoiName(), totalPoints);
        userPointService.addPoints(userId, totalPoints, PointTransactionType.CHECKIN.name(), description, record.getId().toString());

        // 构建响应
        response.setSuccess(true);
        response.setCheckinId(record.getId());
        response.setPoiId(request.getPoiId());
        response.setPoiName(request.getPoiName());
        response.setPoiAddress(request.getPoiAddress());
        response.setCheckinTime(record.getCheckinTime());
        response.setPointsEarned(totalPoints);
        response.setConsecutiveDays(consecutiveDays);
        response.setIsFirstCheckin(isFirstCheckin);
        response.setMessage(String.format("签到成功！获得%d积分", totalPoints));

        return response;
    }

    @Override
    public List<CheckinRecordDTO> getUserCheckinRecords(Long userId, int page, int size) {
        List<PoiCheckinRecord> records = checkinRecords.getOrDefault(userId, new ArrayList<>());
        records.sort((a, b) -> b.getCheckinTime().compareTo(a.getCheckinTime()));
        
        int start = page * size;
        int end = Math.min(start + size, records.size());
        if (start >= records.size()) {
            return new ArrayList<>();
        }
        
        List<CheckinRecordDTO> result = new ArrayList<>();
        for (int i = start; i < end; i++) {
            result.add(convertToDTO(records.get(i)));
        }
        return result;
    }

    @Override
    public List<CheckinRecordDTO> getPoiCheckinRecords(String poiId, int page, int size) {
        List<PoiCheckinRecord> records = poiCheckins.getOrDefault(poiId, new ArrayList<>());
        records.sort((a, b) -> b.getCheckinTime().compareTo(a.getCheckinTime()));
        
        int start = page * size;
        int end = Math.min(start + size, records.size());
        if (start >= records.size()) {
            return new ArrayList<>();
        }
        
        List<CheckinRecordDTO> result = new ArrayList<>();
        for (int i = start; i < end; i++) {
            if (records.get(i).getIsPublic()) {
                result.add(convertToDTO(records.get(i)));
            }
        }
        return result;
    }

    @Override
    public CheckinRecordDTO getCheckinDetail(Long checkinId) {
        for (List<PoiCheckinRecord> records : checkinRecords.values()) {
            for (PoiCheckinRecord record : records) {
                if (record.getId().equals(checkinId)) {
                    return convertToDTO(record);
                }
            }
        }
        return null;
    }

    @Override
    public Boolean hasCheckedInToday(Long userId, String poiId) {
        List<PoiCheckinRecord> records = checkinRecords.getOrDefault(userId, new ArrayList<>());
        LocalDate today = LocalDate.now();
        
        for (PoiCheckinRecord record : records) {
            if (record.getPoiId().equals(poiId) && 
                record.getCheckinTime().toLocalDate().equals(today)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<HotPoiDTO> getNearbyHotPois(Double longitude, Double latitude, int radius, int limit) {
        Map<String, HotPoiDTO> poiCountMap = new HashMap<>();
        
        for (List<PoiCheckinRecord> records : poiCheckins.values()) {
            for (PoiCheckinRecord record : records) {
                double distance = calculateDistance(latitude, longitude, record.getLatitude(), record.getLongitude());
                if (distance <= radius) {
                    HotPoiDTO dto = poiCountMap.getOrDefault(record.getPoiId(), new HotPoiDTO());
                    dto.setPoiId(record.getPoiId());
                    dto.setPoiName(record.getPoiName());
                    dto.setPoiAddress(record.getPoiAddress());
                    dto.setLongitude(record.getLongitude());
                    dto.setLatitude(record.getLatitude());
                    dto.setDistance(distance);
                    dto.setCheckinCount(dto.getCheckinCount() != null ? dto.getCheckinCount() + 1 : 1);
                    poiCountMap.put(record.getPoiId(), dto);
                }
            }
        }
        
        List<HotPoiDTO> result = new ArrayList<>(poiCountMap.values());
        result.sort((a, b) -> b.getCheckinCount().compareTo(a.getCheckinCount()));
        return result.subList(0, Math.min(limit, result.size()));
    }

    @Override
    public ConsecutiveCheckinStatsDTO getConsecutiveStats(Long userId) {
        UserPointAccountResponse account = userPointService.getUserPointAccount(userId);
        ConsecutiveCheckinStatsDTO stats = new ConsecutiveCheckinStatsDTO();
        
        stats.setCurrentStreak(account.getStreakDays());
        stats.setMaxStreak(account.getMaxStreakDays());
        stats.setTotalCheckins(account.getTotalCheckins());
        stats.setLastCheckinTime(account.getLastCheckinTime());
        
        // 检查今日是否可签到
        boolean canCheckinToday = true;
        if (account.getLastCheckinTime() != null) {
            canCheckinToday = !account.getLastCheckinTime().toLocalDate().equals(LocalDate.now());
        }
        stats.setCanCheckinToday(canCheckinToday);
        
        // 计算下一个奖励
        int[] rewardDays = {7, 30, 100, 365};
        int[] rewardPoints = {50, 200, 500, 2000};
        for (int i = 0; i < rewardDays.length; i++) {
            if (account.getStreakDays() < rewardDays[i]) {
                stats.setNextRewardDays(rewardDays[i] - account.getStreakDays());
                stats.setNextRewardPoints(rewardPoints[i]);
                break;
            }
        }
        
        return stats;
    }

    @Override
    public Boolean deleteCheckin(Long userId, Long checkinId) {
        List<PoiCheckinRecord> records = checkinRecords.get(userId);
        if (records != null) {
            return records.removeIf(r -> r.getId().equals(checkinId));
        }
        return false;
    }

    // 辅助方法
    private CheckinRecordDTO convertToDTO(PoiCheckinRecord record) {
        CheckinRecordDTO dto = new CheckinRecordDTO();
        dto.setId(record.getId());
        dto.setUserId(record.getUserId());
        dto.setPoiId(record.getPoiId());
        dto.setPoiName(record.getPoiName());
        dto.setPoiAddress(record.getPoiAddress());
        dto.setLongitude(record.getLongitude());
        dto.setLatitude(record.getLatitude());
        dto.setCheckinTime(record.getCheckinTime());
        dto.setPointsEarned(record.getPointsEarned());
        dto.setCheckinContent(record.getCheckinContent());
        dto.setImageUrls(record.getImageUrls());
        dto.setConsecutiveDays(record.getConsecutiveDays());
        dto.setIsFirstCheckin(record.getIsFirstCheckin());
        dto.setLikeCount(record.getLikeCount());
        dto.setCommentCount(record.getCommentCount());
        return dto;
    }

    private int calculateConsecutiveDays(Long userId, LocalDateTime lastCheckinTime) {
        if (lastCheckinTime == null) {
            return 1;
        }
        
        LocalDate lastDate = lastCheckinTime.toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        if (lastDate.equals(yesterday)) {
            List<PoiCheckinRecord> records = checkinRecords.getOrDefault(userId, new ArrayList<>());
            int maxStreak = 1;
            for (PoiCheckinRecord record : records) {
                if (record.getConsecutiveDays() > maxStreak) {
                    maxStreak = record.getConsecutiveDays();
                }
            }
            return maxStreak + 1;
        } else if (lastDate.equals(today)) {
            return 1;
        } else {
            return 1;
        }
    }

    private int calculateBonusPoints(int consecutiveDays, boolean isFirstCheckin) {
        int bonus = 0;
        
        if (isFirstCheckin) {
            bonus += 50;
        }
        
        if (consecutiveDays >= 365) {
            bonus += 100;
        } else if (consecutiveDays >= 100) {
            bonus += 50;
        } else if (consecutiveDays >= 30) {
            bonus += 30;
        } else if (consecutiveDays >= 7) {
            bonus += 10;
        }
        
        return bonus;
    }

    private String encodeGeoHash(double lat, double lon, int precision) {
        String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
        double[] latRange = {-90.0, 90.0};
        double[] lonRange = {-180.0, 180.0};
        StringBuilder geohash = new StringBuilder();
        boolean isEven = true;
        int bit = 0;
        int ch = 0;

        while (geohash.length() < precision) {
            double mid;
            if (isEven) {
                mid = (lonRange[0] + lonRange[1]) / 2;
                if (lon > mid) {
                    ch |= (1 << (4 - bit));
                    lonRange[0] = mid;
                } else {
                    lonRange[1] = mid;
                }
            } else {
                mid = (latRange[0] + latRange[1]) / 2;
                if (lat > mid) {
                    ch |= (1 << (4 - bit));
                    latRange[0] = mid;
                } else {
                    latRange[1] = mid;
                }
            }

            isEven = !isEven;
            if (bit < 4) {
                bit++;
            } else {
                geohash.append(base32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }
        return geohash.toString();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
