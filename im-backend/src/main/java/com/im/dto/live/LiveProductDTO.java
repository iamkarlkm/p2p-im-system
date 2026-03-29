package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 直播商品DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "直播商品信息")
public class LiveProductDTO {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "直播间ID")
    private Long roomId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品副标题")
    private String subtitle;

    @Schema(description = "商品主图URL")
    private String mainImage;

    @Schema(description = "商品图片列表")
    private java.util.List<String> images;

    @Schema(description = "原价（元）")
    private BigDecimal originalPrice;

    @Schema(description = "直播价（元）")
    private BigDecimal livePrice;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "已售数量")
    private Integer soldCount;

    @Schema(description = "限购数量（0表示不限购）")
    private Integer limitPerUser;

    @Schema(description = "商品状态：0-下架 1-上架 2-售罄")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "是否正在讲解：0-否 1-是")
    private Integer isExplaining;

    @Schema(description = "折扣率")
    private BigDecimal discountRate;

    @Schema(description = "商品详情页URL")
    private String detailUrl;

    @Schema(description = "小程序页面路径")
    private String mpPagePath;

    @Schema(description = "运费（元），0表示包邮")
    private BigDecimal freight;

    @Schema(description = "重量（克）")
    private Integer weight;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
