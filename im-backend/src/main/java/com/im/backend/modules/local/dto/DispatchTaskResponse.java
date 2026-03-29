package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 调度任务响应
 */
@Schema(description = "调度任务响应")
public class DispatchTaskResponse {
    
    @Schema(description = "任务ID")
    private String taskId;
    
    @Schema(description = "订单ID")
    private String orderId;
    
    @Schema(description = "任务状态")
    private Integer status;
    
    @Schema(description = "服务类型")
    private Integer serviceType;
    
    @Schema(description = "取货地址")
    private AddressInfo pickupAddress;
    
    @Schema(description = "送货地址")
    private AddressInfo deliveryAddress;
    
    @Schema(description = "分配的服务人员ID")
    private String staffId;
    
    @Schema(description = "服务人员名称")
    private String staffName;
    
    @Schema(description = "服务人员电话")
    private String staffPhone;
    
    @Schema(description = "预计取货时间")
    private LocalDateTime estimatedPickupTime;
    
    @Schema(description = "预计送达时间")
    private LocalDateTime estimatedDeliveryTime;
    
    @Schema(description = "实际取货时间")
    private LocalDateTime actualPickupTime;
    
    @Schema(description = "实际送达时间")
    private LocalDateTime actualDeliveryTime;
    
    @Schema(description = "配送路径点")
    private List<PathPoint> pathPoints;
    
    @Schema(description = "配送距离(米)")
    private Integer deliveryDistance;
    
    @Schema(description = "配送费")
    private BigDecimal deliveryFee;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public Integer getServiceType() { return serviceType; }
    public void setServiceType(Integer serviceType) { this.serviceType = serviceType; }
    
    public AddressInfo getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(AddressInfo pickupAddress) { this.pickupAddress = pickupAddress; }
    
    public AddressInfo getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(AddressInfo deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
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
    
    public List<PathPoint> getPathPoints() { return pathPoints; }
    public void setPathPoints(List<PathPoint> pathPoints) { this.pathPoints = pathPoints; }
    
    public Integer getDeliveryDistance() { return deliveryDistance; }
    public void setDeliveryDistance(Integer deliveryDistance) { this.deliveryDistance = deliveryDistance; }
    
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    
    /**
     * 地址信息
     */
    @Schema(description = "地址信息")
    public static class AddressInfo {
        @Schema(description = "地址")
        private String address;
        
        @Schema(description = "经度")
        private BigDecimal longitude;
        
        @Schema(description = "纬度")
        private BigDecimal latitude;
        
        @Schema(description = "联系人姓名")
        private String contactName;
        
        @Schema(description = "联系人电话")
        private String contactPhone;
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
        
        public BigDecimal getLatitude() { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
        
        public String getContactName() { return contactName; }
        public void setContactName(String contactName) { this.contactName = contactName; }
        
        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    }
    
    /**
     * 路径点
     */
    @Schema(description = "路径点")
    public static class PathPoint {
        @Schema(description = "经度")
        private BigDecimal longitude;
        
        @Schema(description = "纬度")
        private BigDecimal latitude;
        
        @Schema(description = "时间戳")
        private LocalDateTime timestamp;
        
        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
        
        public BigDecimal getLatitude() { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}
