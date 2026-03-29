package com.im.entity.live;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 直播间实体类
 * 小程序直播与本地电商模块 - 核心实体
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_live_room")
public class LiveRoom extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 直播间ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long roomId;

    /** 主播用户ID */
    private Long anchorId;

    /** 商户ID */
    private Long merchantId;

    /** 直播间标题 */
    private String title;

    /** 直播间封面图片URL */
    private String coverImage;

    /** 直播间简介 */
    private String description;

    /** 直播状态：0-预告 1-直播中 2-暂停 3-已结束 4-回放 */
    private Integer status;

    /** 直播类型：1-普通直播 2-带货直播 3-活动直播 */
    private Integer liveType;

    /** 推流地址 */
    private String pushUrl;

    /** 拉流地址 */
    private String pullUrl;

    /** H5播放地址 */
    private String h5Url;

    /** 小程序直播房间号 */
    private String mpRoomId;

    /** 计划开始时间 */
    private LocalDateTime plannedStartTime;

    /** 实际开始时间 */
    private LocalDateTime actualStartTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 观看人数 */
    private Integer viewerCount;

    /** 最高在线人数 */
    private Integer peakOnlineCount;

    /** 点赞数 */
    private Long likeCount;

    /** 分享数 */
    private Integer shareCount;

    /** 商品数量 */
    private Integer productCount;

    /** 订单数量 */
    private Integer orderCount;

    /** 销售额（分） */
    private Long salesAmount;

    /** 是否允许评论：0-否 1-是 */
    private Integer allowComment;

    /** 是否允许连麦：0-否 1-是 */
    private Integer allowLinkMic;

    /** 直播间密码（加密存储） */
    private String roomPassword;

    /** 可见范围：0-公开 1-粉丝 2-会员 3-密码 */
    private Integer visibility;

    /** 地理位置纬度 */
    private Double latitude;

    /** 地理位置经度 */
    private Double longitude;

    /** 地理位置名称 */
    private String locationName;

    /** 直播标签，逗号分隔 */
    private String tags;

    /** 直播分类ID */
    private Long categoryId;

    /** 排序权重 */
    private Integer sortOrder;

    /** 是否推荐：0-否 1-是 */
    private Integer isRecommended;

    /** 推荐时间 */
    private LocalDateTime recommendTime;

    /** 是否删除：0-否 1-是 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 更新人 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    // ==================== 业务方法 ====================

    /**
     * 判断是否正在直播中
     */
    public boolean isLiving() {
        return status != null && status == 1;
    }

    /**
     * 判断是否为带货直播
     */
    public boolean isCommerceLive() {
        return liveType != null && liveType == 2;
    }

    /**
     * 增加观看人数
     */
    public void incrementViewerCount() {
        if (this.viewerCount == null) {
            this.viewerCount = 0;
        }
        this.viewerCount++;
    }

    /**
     * 增加点赞数
     */
    public void addLikes(int count) {
        if (this.likeCount == null) {
            this.likeCount = 0L;
        }
        this.likeCount += count;
    }

    /**
     * 更新最高在线人数
     */
    public void updatePeakOnline(int currentOnline) {
        if (this.peakOnlineCount == null || currentOnline > this.peakOnlineCount) {
            this.peakOnlineCount = currentOnline;
        }
    }

    /**
     * 开始直播
     */
    public void startLive() {
        this.status = 1;
        this.actualStartTime = LocalDateTime.now();
    }

    /**
     * 结束直播
     */
    public void endLive() {
        this.status = 3;
        this.endTime = LocalDateTime.now();
    }

    /**
     * 暂停直播
     */
    public void pauseLive() {
        this.status = 2;
    }

    /**
     * 恢复直播
     */
    public void resumeLive() {
        this.status = 1;
    }

    /**
     * 增加销售额
     */
    public void addSales(long amount) {
        if (this.salesAmount == null) {
            this.salesAmount = 0L;
        }
        this.salesAmount += amount;
        if (this.orderCount == null) {
            this.orderCount = 0;
        }
        this.orderCount++;
    }

    /**
     * 获取销售额（元）
     */
    public double getSalesAmountYuan() {
        return this.salesAmount != null ? this.salesAmount / 100.0 : 0.0;
    }
}
