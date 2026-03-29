package com.im.backend.modules.geofence.service;

import com.im.backend.modules.geofence.dto.ArrivalRecordResponse;

import java.util.List;

/**
 * 到店记录服务接口
 */
public interface IArrivalRecordService {

    /**
     * 创建到店记录
     */
    ArrivalRecordResponse createArrivalRecord(Long userId, Long merchantId, Long storeId, 
                                               Double longitude, Double latitude, Long geofenceId);

    /**
     * 更新离店记录
     */
    void updateLeaveRecord(Long userId, Long storeId, Double longitude, Double latitude);

    /**
     * 获取用户到店记录
     */
    List<ArrivalRecordResponse> getUserArrivalRecords(Long userId, Integer limit);

    /**
     * 获取门店到店记录
     */
    List<ArrivalRecordResponse> getStoreArrivalRecords(Long storeId, Integer limit);

    /**
     * 获取用户当前在店状态
     */
    ArrivalRecordResponse getCurrentInStoreStatus(Long userId);

    /**
     * 获取门店今日到店统计
     */
    StoreArrivalStatistics getStoreTodayStatistics(Long storeId);

    /**
     * 识别客户标签(新客/老客/VIP)
     */
    String identifyCustomerTag(Long userId, Long storeId);

    /**
     * 到店统计
     */
    class StoreArrivalStatistics {
        private Long storeId;
        private Integer todayTotal;
        private Integer currentInStore;
        private Integer newCustomers;
        private Integer oldCustomers;
        private Integer vipCustomers;

        // Getters and setters
        public Long getStoreId() { return storeId; }
        public void setStoreId(Long storeId) { this.storeId = storeId; }
        public Integer getTodayTotal() { return todayTotal; }
        public void setTodayTotal(Integer todayTotal) { this.todayTotal = todayTotal; }
        public Integer getCurrentInStore() { return currentInStore; }
        public void setCurrentInStore(Integer currentInStore) { this.currentInStore = currentInStore; }
        public Integer getNewCustomers() { return newCustomers; }
        public void setNewCustomers(Integer newCustomers) { this.newCustomers = newCustomers; }
        public Integer getOldCustomers() { return oldCustomers; }
        public void setOldCustomers(Integer oldCustomers) { this.oldCustomers = oldCustomers; }
        public Integer getVipCustomers() { return vipCustomers; }
        public void setVipCustomers(Integer vipCustomers) { this.vipCustomers = vipCustomers; }
    }
}
