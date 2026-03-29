package com.im.backend.modules.delivery.service;

/**
 * 智能调度服务接口
 */
public interface IDispatchService {
    
    /**
     * 智能派单
     */
    boolean dispatchOrder(Long orderId);
    
    /**
     * 批量派单
     */
    int batchDispatch();
    
    /**
     * 重新派单
     */
    boolean redispatchOrder(Long orderId);
    
    /**
     * 计算配送费
     */
    Double calculateDeliveryFee(Double distance, Double weight);
}
