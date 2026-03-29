package com.im.dto.discovery;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 打卡记录DTO
 */
@Data
public class DiscoveryCheckInDTO {
    
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long poiId;
    private String poiName;
    private String poiImage;
    private String content;
    private List<String> images;
    private String videoUrl;
    private BigDecimal rating;
    private BigDecimal spendAmount;
    private List<String> tags;
    private Boolean recommended;
    private String recommendText;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isExpertCheckIn;
    private Integer expertLevel;
    private LocalDateTime checkInTime;
}
