package com.im.backend.modules.miniprogram.market.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 收藏/取消收藏请求DTO
 */
@Data
public class FavoriteRequest {

    @NotNull(message = "小程序ID不能为空")
    private Long appId;

    /**
     * 收藏夹分组ID
     */
    private Long groupId = 0L;

    /**
     * 备注名称
     */
    private String remarkName;
}
