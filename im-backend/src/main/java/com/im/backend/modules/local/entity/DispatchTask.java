package com.im.backend.modules.local.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 调度任务实体
 */
@TableName("dispatch_task")
public class DispatchTask implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String orderId;
    
    private Integer serviceType;
    
    private Integer status;
    
    private String pickupAddress;
    
    private BigDecimal pickupLongitude;
    
    private BigDecimal pickupLatitude;
    
    private String pickupContactName;
    
    private String pickupContactPhone;
    
    private String deliveryAddress;
    
    private BigDecimal deliveryLongitude;
    
    private BigDecimal deliveryLatitude;
    
    private String deliveryContactName;
    
    private String deliveryContactPhone;
    
    private String staffId;
    
    private String staffName;
    
    private String staffPhone;
    
    private LocalDateTime estimatedPickupTime;
    
    private LocalDateTime estimatedDeliveryTime;
    
    private LocalDateTime actualPickupTime;
    
    private LocalDateTime actualDeliveryTime;
    
    private String pathPoints;
    
    private Integer deliveryDistance;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal estimatedWeight;
    
    private Integer priority;
    
    private String remark;
    
    private String geofenceId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public Integer getServiceType() { return serviceType; }
    public void setServiceType(Integer serviceType) { this.serviceType = serviceType; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    
    public BigDecimal getPickupLongitude() { return pickupLongitude; }
    public void setPickupLongitude(BigDecimal pickupLongitude) { this.pickupLongitude = pickupLongitude; }
    
    public BigDecimal getPickupLatitude() { return pickupLatitude; }
    public void setPickupLatitude(BigDecimal pickupLatitude) { this.pickupLatitude = pickupLatitude; }
    
    public String getPickupContactName() { return pickupContactName; }
    public void setPickupContactName(String pickupContactName) { this.pickupContactName = pickupContactName; }
    
    public String getPickupContactPhone() { return pickupContactPhone; }
    public void setPickupContactPhone(String pickupContactPhone) { this.pickupContactPhone = pickupContactPhone; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public BigDecimal getDeliveryLongitude() { return deliveryLongitude; }
    public void setDeliveryLongitude(BigDecimal deliveryLongitude) { this.deliveryLongitude = deliveryLongitude; }
    
    public BigDecimal getDeliveryLatitude() { return deliveryLatitude; }
    public void setDeliveryLatitude(BigDecimal deliveryLatitude) { this.deliveryLatitude = deliveryLatitude; }
    
    public String getDeliveryContactName() { return deliveryContactName; }
    public void setDeliveryContactName(String deliveryContactName) { this.deliveryContactName = deliveryContactName; }
    
    public String getDeliveryContactPhone() { return deliveryContactPhone; }
    public void setDeliveryContactPhone(String deliveryContactPhone) { this.deliveryContactPhone = deliveryContactPhone; }
    
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    
    public String getStaffPhone() { return staffPhone; }
    public void setStaffPhone(String staffPhone) { this.staffPhone = staffPhone; }
    
    public LocalDateTime getEstimatedPickupTime() { return estimatedPickupTime; }
    public void setEstimatedPickupTime(LocalDateTime estimatedPickupTime) { this.estimatedPickupTime = estimatedPickupTime; }
    
    public LocalDateTime getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
    
    public LocalDateTime getActualPickupTime() { return actualPickupTime; }
    public void setActualPickupTime(LocalDateTime actualPickupTime) { this.actualPickupTime = actualPickupTime; }
    
    public LocalDateTime getActualDeliveryTime() { return actualDeliveryTime; }
    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) { this.actualDeliveryTime = actualDeliveryTime; }
    
    public String getPathPoints() { return pathPoints; }
    public void setPathPoints(String pathPoints) { this.pathPoints = pathPoints; }
    
    public Integer getDeliveryDistance() { return deliveryDistance; }
    public void setDeliveryDistance(Integer deliveryDistance) { this.deliveryDistance = deliveryDistance; }
    
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
    
    public BigDecimal getEstimatedWeight() { return estimatedWeight; }
    public void setEstimatedWeight(BigDecimal estimatedWeight) { this.estimatedWeight = estimatedWeight; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    public String getGeofenceId() { return geofenceId; }
    public void setGeofenceId(String geofenceId) { this.geofenceId = geofenceId; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
