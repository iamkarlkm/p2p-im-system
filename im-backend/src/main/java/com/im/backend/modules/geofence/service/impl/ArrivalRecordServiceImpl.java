package com.im.backend.modules.geofence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.geofence.dto.ArrivalRecordResponse;
import com.im.backend.modules.geofence.entity.ArrivalRecord;
import com.im.backend.modules.geofence.enums.ArrivalStatus;
import com.im.backend.modules.geofence.enums.CustomerTag;
import com.im.backend.modules.geofence.repository.ArrivalRecordMapper;
import com.im.backend.modules.geofence.service.IArrivalRecordService;
import com.im.backend.modules.geofence.service.IPersonalizedArrivalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrivalRecordServiceImpl extends ServiceImpl<ArrivalRecordMapper, ArrivalRecord> implements IArrivalRecordService {

    private final ArrivalRecordMapper arrivalRecordMapper;
    private final IPersonalizedArrivalService personalizedService;

    @Override
    @Transactional
    public ArrivalRecordResponse createArrivalRecord(Long userId, Long merchantId, Long storeId,
                                                      Double longitude, Double latitude, Long geofenceId) {
        // 检查是否已有在店记录
        ArrivalRecord existingRecord = arrivalRecordMapper.selectInStoreRecord(userId, storeId);
        if (existingRecord != null) {
            log.info("用户 {} 已有在店记录, storeId={}", userId, storeId);
            return convertToResponse(existingRecord);
        }

        // 识别客户标签
        String customerTag = identifyCustomerTag(userId, storeId);
        int arrivalCount = arrivalRecordMapper.countTodayArrival(userId, storeId) + 1;

        ArrivalRecord record = new ArrivalRecord();
        record.setUserId(userId);
        record.setMerchantId(merchantId);
        record.setStoreId(storeId);
        record.setEnterTime(LocalDateTime.now());
        record.setEnterLongitude(longitude);
        record.setEnterLatitude(latitude);
        record.setArrivalCount(arrivalCount);
        record.setCustomerTag(customerTag);
        record.setStatus(ArrivalStatus.IN_STORE.getCode());
        record.setServicePushed(false);
        record.setTriggerGeofenceId(geofenceId);

        arrivalRecordMapper.insert(record);

        // 触发个性化服务
        personalizedService.onUserArrival(record);

        log.info("创建到店记录成功: userId={}, storeId={}, customerTag={}", userId, storeId, customerTag);
        return convertToResponse(record);
    }

    @Override
    @Transactional
    public void updateLeaveRecord(Long userId, Long storeId, Double longitude, Double latitude) {
        ArrivalRecord record = arrivalRecordMapper.selectInStoreRecord(userId, storeId);
        if (record == null) {
            log.warn("用户 {} 没有在店记录, storeId={}", userId, storeId);
            return;
        }

        LocalDateTime leaveTime = LocalDateTime.now();
        int duration = (int) ChronoUnit.MINUTES.between(record.getEnterTime(), leaveTime);

        arrivalRecordMapper.updateLeaveInfo(record.getId(), leaveTime, longitude, latitude, duration);
        log.info("更新离店记录成功: userId={}, storeId={}, duration={}分钟", userId, storeId, duration);
    }

    @Override
    public List<ArrivalRecordResponse> getUserArrivalRecords(Long userId, Integer limit) {
        return lambdaQuery()
                .eq(ArrivalRecord::getUserId, userId)
                .orderByDesc(ArrivalRecord::getEnterTime)
                .last("LIMIT " + limit)
                .list()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArrivalRecordResponse> getStoreArrivalRecords(Long storeId, Integer limit) {
        return lambdaQuery()
                .eq(ArrivalRecord::getStoreId, storeId)
                .orderByDesc(ArrivalRecord::getEnterTime)
                .last("LIMIT " + limit)
                .list()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ArrivalRecordResponse getCurrentInStoreStatus(Long userId) {
        List<ArrivalRecord> records = lambdaQuery()
                .eq(ArrivalRecord::getUserId, userId)
                .eq(ArrivalRecord::getStatus, ArrivalStatus.IN_STORE.getCode())
                .list();
        
        if (!records.isEmpty()) {
            return convertToResponse(records.get(0));
        }
        return null;
    }

    @Override
    public StoreArrivalStatistics getStoreTodayStatistics(Long storeId) {
        StoreArrivalStatistics stats = new StoreArrivalStatistics();
        stats.setStoreId(storeId);
        stats.setTodayTotal(arrivalRecordMapper.countTodayUniqueUsers(storeId));
        stats.setCurrentInStore(arrivalRecordMapper.countCurrentInStore(storeId));
        
        // TODO: 细分新客老客VIP统计
        stats.setNewCustomers(0);
        stats.setOldCustomers(0);
        stats.setVipCustomers(0);
        
        return stats;
    }

    @Override
    public String identifyCustomerTag(Long userId, Long storeId) {
        // 查询用户历史到店记录
        int totalVisits = lambdaQuery()
                .eq(ArrivalRecord::getUserId, userId)
                .eq(ArrivalRecord::getStoreId, storeId)
                .count();

        if (totalVisits == 0) {
            return CustomerTag.NEW.getCode();
        }

        // 查询最近到店时间
        ArrivalRecord lastVisit = lambdaQuery()
                .eq(ArrivalRecord::getUserId, userId)
                .eq(ArrivalRecord::getStoreId, storeId)
                .orderByDesc(ArrivalRecord::getEnterTime)
                .last("LIMIT 1")
                .one();

        if (lastVisit != null) {
            long daysSinceLastVisit = ChronoUnit.DAYS.between(lastVisit.getEnterTime(), LocalDateTime.now());
            
            if (daysSinceLastVisit > 90) {
                return CustomerTag.LOST.getCode();
            } else if (daysSinceLastVisit > 30) {
                return CustomerTag.SILENT.getCode();
            } else if (totalVisits >= 10) {
                return CustomerTag.VIP.getCode();
            }
        }

        return CustomerTag.OLD.getCode();
    }

    private ArrivalRecordResponse convertToResponse(ArrivalRecord record) {
        ArrivalRecordResponse response = new ArrivalRecordResponse();
        BeanUtils.copyProperties(record, response);
        return response;
    }
}
