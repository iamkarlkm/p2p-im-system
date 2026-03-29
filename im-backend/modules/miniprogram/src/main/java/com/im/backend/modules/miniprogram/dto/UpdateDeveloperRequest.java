package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 更新开发者请求
 */
@Data
public class UpdateDeveloperRequest {

    private String nickname;

    private String avatar;

    private String bio;

    private String website;

    private String githubUrl;

    private List<String> skills;
}
