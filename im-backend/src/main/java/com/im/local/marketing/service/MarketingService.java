package com.im.local.marketing.service;

import com.im.local.marketing.dto.*;
import com.im.common.result.Result;

import java.util.List;

/**
 * 营销活动服务接口
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
public interface MarketingService {
    
    /**
     * 创建营销活动
     * 
     * @param dto 活动信息
     * @return 活动ID
     */
    Result<String> createActivity(MarketingActivityDTO dto);
    
    /**
     * 获取活动列表
     * 
     * @param merchantId 商户ID（可选）
     * @param activityType 活动类型（可选）
     * @param status 状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 活动列表
     */
    Result<List<ActivityListDTO>> getActivities(String merchantId, String activityType, 
                                                 String status, Integer page, Integer size);
    
    /**
     * 获取活动详情
     * 
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 活动详情
     */
    Result<ActivityDetailDTO> getActivityDetail(String activityId, String userId);
    
    /**
     * 发起拼团
     * 
     * @param userId 用户ID
     * @param dto 拼团信息
     * @return 拼团ID
     */
    Result<String> createGroupBuy(String userId, GroupBuyDTO dto);
    
    /**
     * 参与拼团
     * 
     * @param userId 用户ID
     * @param groupId 拼团ID
     * @param dto 参团信息
     * @return 参团结果
     */
    Result<Void> joinGroupBuy(String userId, String groupId, GroupBuyJoinDTO dto);
    
    /**
     * 获取拼团详情
     * 
     * @param groupId 拼团ID
     * @param userId 用户ID
     * @return 拼团详情
     */
    Result<GroupBuyDetailDTO> getGroupBuyDetail(String groupId, String userId);
    
    /**
     * 发起砍价
     * 
     * @param userId 用户ID
     * @param activityId 活动ID
     * @param productId 商品ID
     * @return 砍价记录ID
     */
    Result<String> startBargain(String userId, String activityId, String productId);
    
    /**
     * 帮好友砍价
     * 
     * @param userId 用户ID
     * @param bargainId 砍价记录ID
     * @return 砍掉金额
     */
    Result<BargainResultDTO> helpBargain(String userId, String bargainId);
    
    /**
     * 获取砍价详情
     * 
     * @param bargainId 砍价记录ID
     * @param userId 用户ID
     * @return 砍价详情
     */
    Result<BargainDetailDTO> getBargainDetail(String bargainId, String userId);
    
    /**
     * 立即购买（秒杀）
     * 
     * @param userId 用户ID
     * @param activityId 活动ID
     * @param productId 商品ID
     * @return 订单信息
     */
    Result<FlashSaleOrderDTO> flashSaleBuy(String userId, String activityId, String productId);
    
    /**
     * 计算活动优惠
     * 
     * @param activityId 活动ID
     * @param productIds 商品ID列表
     * @param amounts 商品金额列表
     * @return 优惠金额
     */
    Result<DiscountCalcResultDTO> calculateDiscount(String activityId, 
                                                     List<String> productIds, 
                                                     List<BigDecimal> amounts);
}
