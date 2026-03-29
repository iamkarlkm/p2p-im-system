package com.im.local.coupon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 拼团详情响应DTO
 */
@Data
public class GroupBuyingDetailResponse {

    /** 拼团ID */
    private Long id;

    /** 活动ID */
    private Long activityId;

    /** 团长信息 */
    private UserInfo leader;

    /** 成团人数要求 */
    private Integer requiredMembers;

    /** 当前参与人数 */
    private Integer currentMembers;

    /** 还差人数 */
    private Integer remainMembers;

    /** 状态 */
    private Integer status;

    /** 状态名称 */
    private String statusName;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 截止时间 */
    private LocalDateTime expireTime;

    /** 拼团价格 */
    private BigDecimal groupPrice;

    /** 参与成员列表 */
    private List<UserInfo> members;

    @Data
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String avatar;
        private LocalDateTime joinTime;
    }
}
