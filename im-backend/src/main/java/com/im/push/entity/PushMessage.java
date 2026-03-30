package com.im.push.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 推送消息实体 - 消息推送通知系统核心实体
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_push_message")
public class PushMessage {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String messageId;
    private String title;
    private String content;
    private Integer pushType; // 1-APNS, 2-FCM, 3-华为, 4-小米, 5-OPPO, 6-VIVO, 7-魅族
    private String deviceToken;
    private Integer status; // 0-待推送, 1-推送中, 2-成功, 3-失败
    private Integer retryCount;
    private String errorMsg;
    private LocalDateTime pushTime;
    private String extraData;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    public static final int TYPE_APNS = 1;
    public static final int TYPE_FCM = 2;
    public static final int TYPE_HUAWEI = 3;
    public static final int TYPE_XIAOMI = 4;
    public static final int TYPE_OPPO = 5;
    public static final int TYPE_VIVO = 6;
    public static final int TYPE_MEIZU = 7;
}
