package com.im.backend.modules.miniprogram.dto;

import lombok.Data;

/**
 * 开发者等级
 */
@Data
public class DeveloperLevel {

    private Integer level;

    private String levelName;

    private String description;

    private Integer minPoints;

    private Integer maxPoints;

    private String benefits;
}
