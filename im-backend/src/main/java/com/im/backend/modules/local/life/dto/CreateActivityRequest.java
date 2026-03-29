package com.im.backend.modules.local.life.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建活动请求DTO
 */
@Data
public class CreateActivityRequest {

    @NotBlank(message = "活动标题不能为空")
    @Size(max = 100, message = "活动标题最多100字")
    private String title;

    @Size(max = 2000, message = "活动描述最多2000字")
    private String description;

    @NotBlank(message = "活动分类不能为空")
    private String category;

    private String coverImage;

    @NotBlank(message = "POI ID不能为空")
    private String poiId;

    @NotBlank(message = "POI名称不能为空")
    private String poiName;

    @NotBlank(message = "详细地址不能为空")
    private String address;

    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @NotNull(message = "活动开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "活动结束时间不能为空")
    private LocalDateTime endTime;

    private LocalDateTime registrationDeadline;

    @NotNull(message = "最大参与人数不能为空")
    @Min(value = 2, message = "至少2人参与")
    @Max(value = 1000, message = "最多1000人参与")
    private Integer maxParticipants;

    @NotBlank(message = "支付方式不能为空")
    private String paymentType;

    private BigDecimal perCapitaFee;

    private Boolean createImGroup = true;

    private Boolean requireApproval = false;

    private List<String> tags;

    private List<ActivityMediaDTO> mediaList;
}
