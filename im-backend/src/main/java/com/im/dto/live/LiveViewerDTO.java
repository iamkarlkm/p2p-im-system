package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 在线观众DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "在线观众信息")
public class LiveViewerDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "等级")
    private Integer level;

    @Schema(description = "是否主播：0-否 1-是")
    private Integer isAnchor;

    @Schema(description = "是否管理员：0-否 1-是")
    private Integer isAdmin;

    @Schema(description = "进入时间")
    private LocalDateTime enterTime;
}
