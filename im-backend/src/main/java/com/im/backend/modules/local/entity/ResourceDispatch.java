package com.im.backend.modules.local.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资源调度记录实体
 */
@TableName("resource_dispatch")
public class ResourceDispatch implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String geofenceId;
    
    private String staffId;
    
    private String staffName;
    
    private Integer dispatchType;
    
    private Integer status;
    
    private String reason;
    
    private BigDecimal fromLongitude;
    
    private BigDecimal fromLatitude;
    
    private BigDecimal toLongitude;
    
    private BigDecimal toLatitude;
    
    private LocalDateTime estimatedArrivalTime;
    
    private LocalDateTime actualArrivalTime;
    
    private String pathId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getGeofenceId() { return geofenceId; }
    public void setGeofenceId(String geofenceId) { this.geofenceId = geofenceId; }
    
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    
    public Integer getDispatchType() { return dispatchType; }
    public void setDispatchType(Integer dispatchType) { this.dispatchType = dispatchType; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public BigDecimal getFromLongitude() { return fromLongitude; }
    public void setFromLongitude(BigDecimal fromLongitude) { this.fromLongitude = fromLongitude; }
    
    public BigDecimal getFromLatitude() { return fromLatitude; }
    public void setFromLatitude(BigDecimal fromLatitude) { this.fromLatitude = fromLatitude; }
    
    public BigDecimal getToLongitude() { return toLongitude; }
    public void setToLongitude(BigDecimal toLongitude) { this.toLongitude = toLongitude; }
    
    public BigDecimal getToLatitude() { return toLatitude; }
    public void setToLatitude(BigDecimal toLatitude) { this.toLatitude = toLatitude; }
    
    public LocalDateTime getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }
    
    public LocalDateTime getActualArrivalTime() { return actualArrivalTime; }
    public void setActualArrivalTime(LocalDateTime actualArrivalTime) { this.actualArrivalTime = actualArrivalTime; }
    
    public String getPathId() { return pathId; }
    public void setPathId(String pathId) { this.pathId = pathId; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
