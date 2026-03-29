package com.im.backend.modules.appointment.dto;

import jakarta.validation.constraints.*;

/**
 * 排队取号请求DTO
 */
public class TakeQueueRequestDTO {

    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    @NotBlank(message = "队列类型不能为空")
    @Size(max = 50, message = "队列类型长度不能超过50")
    private String queueType;

    @NotNull(message = "人数不能为空")
    @Min(value = 1, message = "人数至少为1")
    @Max(value = 50, message = "人数不能超过50")
    private Integer peopleCount;

    @Size(max = 50, message = "桌台类型长度不能超过50")
    private String tableType;

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名长度不能超过50")
    private String contactName;

    @NotBlank(message = "联系人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String contactPhone;

    @Size(max = 200, message = "备注长度不能超过200")
    private String remark;

    private Double userLatitude;
    private Double userLongitude;
    private String source;

    // Getters and Setters
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public String getQueueType() { return queueType; }
    public void setQueueType(String queueType) { this.queueType = queueType; }

    public Integer getPeopleCount() { return peopleCount; }
    public void setPeopleCount(Integer peopleCount) { this.peopleCount = peopleCount; }

    public String getTableType() { return tableType; }
    public void setTableType(String tableType) { this.tableType = tableType; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Double getUserLatitude() { return userLatitude; }
    public void setUserLatitude(Double userLatitude) { this.userLatitude = userLatitude; }

    public Double getUserLongitude() { return userLongitude; }
    public void setUserLongitude(Double userLongitude) { this.userLongitude = userLongitude; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
