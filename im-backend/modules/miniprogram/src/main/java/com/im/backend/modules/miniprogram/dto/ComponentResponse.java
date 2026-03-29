package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 组件响应
 */
@Data
public class ComponentResponse {

    private Long id;
    private String componentKey;
    private String componentName;
    private String description;
    private Integer category;
    private String categoryDesc;
    private String componentType;
    private String icon;
    private String previewImage;
    private Long authorId;
    private String authorName;
    private String version;
    private Integer status;
    private Long downloadCount;
    private BigDecimal rating;
    private Integer ratingCount;
    private BigDecimal price;
    private Boolean isOfficial;
    private List<String> tags;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 组件代码（详情时返回）
     */
    private String templateCode;
    private String styleCode;
    private String scriptCode;
    private Map<String, Object> propsSchema;
    private Map<String, Object> defaultData;
}
