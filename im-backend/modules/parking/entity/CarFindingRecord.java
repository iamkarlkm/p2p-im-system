package com.im.backend.modules.parking.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 反向寻车记录实体类
 * 存储用户寻车历史、导航路径等信息
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_car_finding_record")
@Schema(description = "反向寻车记录实体")
public class CarFindingRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 寻车记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "寻车记录ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 停车记录ID
     */
    @Schema(description = "停车记录ID")
    private Long parkingRecordId;

    /**
     * 停车场ID
     */
    @Schema(description = "停车场ID")
    private Long parkingLotId;

    /**
     * 车牌号
     */
    @Schema(description = "车牌号")
    private String plateNumber;

    /**
     * 当前位置经度
     */
    @Schema(description = "当前位置经度")
    private Double currentLongitude;

    /**
     * 当前位置纬度
     */
    @Schema(description = "当前位置纬度")
    private Double currentLatitude;

    /**
     * 当前位置楼层
     */
    @Schema(description = "当前位置楼层")
    private String currentFloor;

    /**
     * 当前位置区域
     */
    @Schema(description = "当前位置区域")
    private String currentArea;

    /**
     * 目标车位经度
     */
    @Schema(description = "目标车位经度")
    private Double targetLongitude;

    /**
     * 目标车位纬度
     */
    @Schema(description = "目标车位纬度")
    private Double targetLatitude;

    /**
     * 目标车位楼层
     */
    @Schema(description = "目标车位楼层")
    private String targetFloor;

    /**
     * 目标车位区域
     */
    @Schema(description = "目标车位区域")
    private String targetArea;

    /**
     * 目标车位编号
     */
    @Schema(description = "目标车位编号")
    private String targetSpaceNumber;

    /**
     * 导航路径（JSON格式，存储路径点序列）
     */
    @Schema(description = "导航路径")
    private String navigationPath;

    /**
     * 导航距离（米）
     */
    @Schema(description = "导航距离")
    private Integer navigationDistance;

    /**
     * 预计导航时间（分钟）
     */
    @Schema(description = "预计导航时间")
    private Integer estimatedTime;

    /**
     * 寻车开始时间
     */
    @Schema(description = "寻车开始时间")
    private LocalDateTime startTime;

    /**
     * 寻车结束时间
     */
    @Schema(description = "寻车结束时间")
    private LocalDateTime endTime;

    /**
     * 实际寻车时长（分钟）
     */
    @Schema(description = "实际寻车时长")
    private Integer actualDuration;

    /**
     * 是否成功找到车辆
     */
    @Schema(description = "是否成功找到车辆")
    private Boolean foundSuccess;

    /**
     * 寻车方式：1-室内导航 2-照片记忆 3-语音引导 4-AR导航
     */
    @Schema(description = "寻车方式")
    private Integer findingMethod;

    /**
     * 是否使用AR导航
     */
    @Schema(description = "是否使用AR导航")
    private Boolean usedArNavigation;

    /**
     * 寻车状态：1-寻车中 2-已找到 3-已取消
     */
    @Schema(description = "寻车状态")
    private Integer status;

    /**
     * 寻车次数（同一停车记录的寻车尝试次数）
     */
    @Schema(description = "寻车次数")
    private Integer findingCount;

    /**
     * 寻车照片URL（用户标记的照片）
     */
    @Schema(description = "寻车照片URL")
    private String findingPhotoUrl;

    /**
     * 语音备注URL
     */
    @Schema(description = "语音备注URL")
    private String voiceNoteUrl;

    /**
     * 文字备注
     */
    @Schema(description = "文字备注")
    private String textNote;

    /**
     * 是否使用电梯
     */
    @Schema(description = "是否使用电梯")
    private Boolean useElevator;

    /**
     * 推荐电梯ID
     */
    @Schema(description = "推荐电梯ID")
    private String recommendedElevatorId;

    /**
     * 楼层切换次数
     */
    @Schema(description = "楼层切换次数")
    private Integer floorChangeCount;

    /**
     * 删除标记
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 开始寻车
     *
     * @param currentLongitude 当前经度
     * @param currentLatitude  当前纬度
     * @param findingMethod    寻车方式
     */
    public void startFinding(Double currentLongitude, Double currentLatitude, Integer findingMethod) {
        this.currentLongitude = currentLongitude;
        this.currentLatitude = currentLatitude;
        this.findingMethod = findingMethod;
        this.startTime = LocalDateTime.now();
        this.status = 1;
        this.findingCount = findingCount != null ? findingCount + 1 : 1;
    }

    /**
     * 完成寻车
     *
     * @param success 是否成功
     */
    public void completeFinding(Boolean success) {
        this.endTime = LocalDateTime.now();
        this.foundSuccess = success;
        this.status = success ? 2 : 3;
        calculateActualDuration();
    }

    /**
     * 计算实际寻车时长
     */
    public void calculateActualDuration() {
        if (startTime != null && endTime != null) {
            this.actualDuration = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }

    /**
     * 是否寻车中
     *
     * @return 是否寻车中
     */
    public boolean isFinding() {
        return status != null && status == 1;
    }

    /**
     * 更新当前位置
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param floor     楼层
     * @param area      区域
     */
    public void updateCurrentLocation(Double longitude, Double latitude, String floor, String area) {
        this.currentLongitude = longitude;
        this.currentLatitude = latitude;
        this.currentFloor = floor;
        this.currentArea = area;
    }

    /**
     * 设置导航路径
     *
     * @param path     路径JSON
     * @param distance 距离
     * @param time     预计时间
     */
    public void setNavigationPath(String path, Integer distance, Integer time) {
        this.navigationPath = path;
        this.navigationDistance = distance;
        this.estimatedTime = time;
    }

    /**
     * 获取导航进度
     *
     * @return 进度百分比
     */
    public Integer getNavigationProgress() {
        if (navigationPath == null || currentLongitude == null || targetLongitude == null) {
            return 0;
        }

        // 简化计算：直线距离比例
        double totalDistance = calculateDistance(currentLongitude, currentLatitude, targetLongitude, targetLatitude);
        double startDistance = calculateDistance(currentLongitude, currentLatitude, targetLongitude, targetLatitude);

        if (totalDistance == 0) {
            return 100;
        }

        return (int) ((1 - startDistance / totalDistance) * 100);
    }

    /**
     * 计算两点距离（Haversine公式）
     *
     * @param lon1 经度1
     * @param lat1 纬度1
     * @param lon2 经度2
     * @param lat2 纬度2
     * @return 距离（米）
     */
    private double calculateDistance(Double lon1, Double lat1, Double lon2, Double lat2) {
        if (lon1 == null || lat1 == null || lon2 == null || lat2 == null) {
            return 0;
        }

        double R = 6371000; // 地球半径（米）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * 获取寻车状态文本
     *
     * @return 状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "寻车中";
            case 2:
                return "已找到";
            case 3:
                return "已取消";
            default:
                return "未知";
        }
    }

    /**
     * 获取寻车方式文本
     *
     * @return 方式文本
     */
    public String getFindingMethodText() {
        if (findingMethod == null) {
            return "普通导航";
        }
        switch (findingMethod) {
            case 1:
                return "室内导航";
            case 2:
                return "照片记忆";
            case 3:
                return "语音引导";
            case 4:
                return "AR导航";
            default:
                return "普通导航";
        }
    }

    /**
     * 是否跨楼层寻车
     *
     * @return 是否跨楼层
     */
    public boolean isCrossFloor() {
        if (currentFloor == null || targetFloor == null) {
            return false;
        }
        return !currentFloor.equals(targetFloor);
    }

    /**
     * 获取楼层差异
     *
     * @return 楼层差
     */
    public Integer getFloorDifference() {
        if (!isCrossFloor()) {
            return 0;
        }
        try {
            int current = Integer.parseInt(currentFloor.replaceAll("[^-\\d]", ""));
            int target = Integer.parseInt(targetFloor.replaceAll("[^-\\d]", ""));
            return target - current;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
