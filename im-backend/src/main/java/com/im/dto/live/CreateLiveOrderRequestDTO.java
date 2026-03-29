package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 创建直播订单请求DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "创建直播订单请求")
public class CreateLiveOrderRequestDTO {

    @NotNull(message = "直播间ID不能为空")
    @Schema(description = "直播间ID", required = true)
    private Long roomId;

    @NotEmpty(message = "商品列表不能为空")
    @Schema(description = "订单商品列表", required = true)
    private List<LiveOrderItemDTO> items;

    @NotBlank(message = "收货人姓名不能为空")
    @Schema(description = "收货人姓名", required = true)
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "收货人电话", required = true)
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    @Schema(description = "收货地址", required = true)
    private String receiverAddress;

    @Schema(description = "详细地址")
    private String receiverDetail;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @Schema(description = "纬度")
    private Double latitude;

    @Schema(description = "经度")
    private Double longitude;

    @NotNull(message = "配送方式不能为空")
    @Min(value = 1, message = "配送方式不合法")
    @Max(value = 3, message = "配送方式不合法")
    @Schema(description = "配送方式：1-快递 2-同城配送 3-到店自提", required = true, example = "1")
    private Integer deliveryType;

    @Schema(description = "自提门店ID（当配送方式为3时需要）")
    private Long pickupStoreId;

    @Schema(description = "买家留言")
    private String buyerRemark;

    @Schema(description = "优惠券ID")
    private Long couponId;

    /**
     * 订单商品项
     */
    @Data
    @Schema(description = "订单商品项")
    public static class LiveOrderItemDTO {

        @NotNull(message = "商品ID不能为空")
        @Schema(description = "商品ID", required = true)
        private Long productId;

        @NotNull(message = "购买数量不能为空")
        @Min(value = 1, message = "购买数量至少为1")
        @Schema(description = "购买数量", required = true, example = "1")
        private Integer quantity;

        @Schema(description = "商品规格")
        private String specification;
    }
}
