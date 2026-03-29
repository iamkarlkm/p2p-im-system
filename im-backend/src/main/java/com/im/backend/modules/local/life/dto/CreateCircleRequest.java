package com.im.backend.modules.local.life.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 创建社交圈子请求DTO
 */
@Data
public class CreateCircleRequest {

    @NotBlank(message = "圈子名称不能为空")
    @Size(max = 50, message = "圈子名称最多50字")
    private String name;

    @Size(max = 500, message = "圈子简介最多500字")
    private String description;

    private String avatar;
    private String coverImage;

    @NotBlank(message = "圈子分类不能为空")
    private String category;

    @NotBlank(message = "圈子类型不能为空")
    private String circleType;

    private List<String> poiTypeTags;

    private Double longitude;
    private Double latitude;

    private String cityCode;
    private String cityName;

    private Boolean requireApproval = false;

    private String postPermission = "ALL";
}
