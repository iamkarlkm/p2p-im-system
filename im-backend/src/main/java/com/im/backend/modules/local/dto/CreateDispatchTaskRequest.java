package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建调度任务请求
 */
@Schema(description = "创建调度任务请求")
public class CreateDispatchTaskRequest {
    
    @NotBlank(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private String orderId;
    
    @NotNull(message = "服务类型不能为空")
    @Schema(description = "服务类型")
    private Integer serviceType;
    
    @NotNull(message = "取货地址不能为空")
    @Schema(description = "取货地址")
    private AddressInfo pickupAddress;
    
    @NotNull(message = "送货地址不能为空")
    @Schema(description = "送货地址")
    private AddressInfo deliveryAddress;
    
    @Schema(description = "预计重量(kg)")
    private BigDecimal estimatedWeight;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "预约时间")
    private LocalDateTime appointmentTime;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public Integer getServiceType() { return serviceType; }
    public void setServiceType(Integer serviceType) { this.serviceType = serviceType; }
    
    public AddressInfo getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(AddressInfo pickupAddress) { this.pickupAddress = pickupAddress; }
    
    public AddressInfo getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(AddressInfo deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public BigDecimal getEstimatedWeight() { return estimatedWeight; }
    public void setEstimatedWeight(BigDecimal estimatedWeight) { this.estimatedWeight = estimatedWeight; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
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
        
        // Getters and Setters
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
}
