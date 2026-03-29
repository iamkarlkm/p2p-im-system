package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 个性化推荐VO
 */
@Data
@Schema(description = "个性化推荐")
public class PersonalizedRecommendationVO {
    
    @Schema(description = "推荐ID")
    private String recommendationId;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "商户ID")
    private Long merchantId;
    
    @Schema(description = "推荐场景")
    private String scene;
    
    @Schema(description = "欢迎语")
    private String welcomeMessage;
    
    @Schema(description = "推荐商品列表")
    private List<RecommendedProductVO> recommendedProducts;
    
    @Schema(description = "推荐优惠券")
    private List<RecommendedCouponVO> recommendedCoupons;
    
    @Schema(description = "个性化优惠")
    private PersonalizedOfferVO personalizedOffer;
    
    @Schema(description = "会员权益提示")
    private MemberBenefitHintVO memberBenefitHint;
    
    @Schema(description = "历史偏好标签")
    private List<String> preferenceTags;
}
