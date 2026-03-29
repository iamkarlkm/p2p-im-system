package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 开发者响应
 */
@Data
public class DeveloperResponse {

    private Long id;
    private Long userId;
    private String nickname;
    private String avatar;
    private Integer developerType;
    private String developerTypeDesc;
    private Integer authStatus;
    private String authStatusDesc;
    private Integer level;
    private String levelDesc;
    private Integer points;
    private Integer creditScore;
    private Integer componentCount;
    private Integer templateCount;
    private Integer programCount;
    private BigDecimal balance;
    private BigDecimal totalIncome;
    private String bio;
    private String website;
    private String githubUrl;
    private List<String> skills;
    private LocalDateTime createTime;
}
