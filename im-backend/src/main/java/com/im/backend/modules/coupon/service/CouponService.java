package com.im.backend.modules.coupon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.coupon.dto.*;
import com.im.backend.modules.coupon.entity.Coupon;
import com.im.backend.modules.coupon.entity.UserCoupon;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务接口 - 本地生活精准营销核心服务
 * 
 * 功能说明:
 * 1. 优惠券CRUD管理 - 商家/平台创建和维护优惠券
 * 2. 用户领取服务 - 支持多种领取场景（主动领取/地理围栏触发/分享等）
 * 3. 优惠券核销服务 - 订单结算时使用优惠券
 * 4. LBS附近优惠券搜索 - 基于Redis Geo实现
 * 5. 优惠券推荐服务 - 个性化推荐
 * 
 * 技术要点:
 * - Redis Lua脚本保证库存扣减原子性
 * - Redis Geo实现附近优惠券搜索
 * - 分布式锁防止超领
 * 
 * 目标指标:
 * - 领取响应 < 100ms
 * - 附近搜索 < 200ms
 * - 核销响应 < 50ms
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
public interface CouponService extends IService<Coupon> {

    // ==================== 优惠券管理 ====================
    
    /**
     * 创建优惠券
     * 
     * 功能: 商家或平台创建新的优惠券活动
     * 流程:
     * 1. 参数校验
     * 2. 生成优惠券编码
     * 3. 计算GeoHash（如启用地理限制）
     * 4. 保存到数据库
     * 5. 如启用地理限制，同步到Redis Geo
     * 
     * @param request 创建请求
     * @param operatorId 操作人ID
     * @return 创建的优惠券ID
     */
    Long createCoupon(CreateCouponRequest request, Long operatorId);
    
    /**
     * 更新优惠券
     * 
     * 限制: 仅允许修改未开始发放的优惠券
     * 
     * @param couponId 优惠券ID
     * @param request 更新请求
     * @param operatorId 操作人ID
     */
    void updateCoupon(Long couponId, UpdateCouponRequest request, Long operatorId);
    
    /**
     * 删除优惠券
     * 
     * 限制: 仅允许删除未领取的优惠券
     * 
     * @param couponId 优惠券ID
     * @param operatorId 操作人ID
     */
    void deleteCoupon(Long couponId, Long operatorId);
    
    /**
     * 获取优惠券详情
     * 
     * @param couponId 优惠券ID
     * @param userId 当前用户ID（用于判断是否已领取）
     * @return 优惠券详情
     */
    CouponDetailResponse getCouponDetail(Long couponId, Long userId);
    
    /**
     * 分页查询优惠券列表
     * 
     * @param query 查询条件
     * @return 分页结果
     */
    Page<CouponListResponse> listCoupons(CouponQueryRequest query);
    
    /**
     * 查询商户优惠券列表
     * 
     * @param merchantId 商户ID
     * @param status 状态筛选
     * @return 优惠券列表
     */
    List<CouponListResponse> listMerchantCoupons(Long merchantId, Integer status);
    
    /**
     * 审核优惠券
     * 
     * @param couponId 优惠券ID
     * @param approved 是否通过
     * @param remark 审核备注
     * @param auditorId 审核人ID
     */
    void auditCoupon(Long couponId, Boolean approved, String remark, Long auditorId);
    
    /**
     * 暂停优惠券发放
     * 
     * @param couponId 优惠券ID
     * @param operatorId 操作人ID
     */
    void pauseCoupon(Long couponId, Long operatorId);
    
    /**
     * 恢复优惠券发放
     * 
     * @param couponId 优惠券ID
     * @param operatorId 操作人ID
     */
    void resumeCoupon(Long couponId, Long operatorId);
    
    /**
     * 提前结束优惠券
     * 
     * @param couponId 优惠券ID
     * @param operatorId 操作人ID
     */
    void terminateCoupon(Long couponId, Long operatorId);
    
    // ==================== 用户领取服务 ====================
    
    /**
     * 用户领取优惠券
     * 
     * 功能: 用户主动领取优惠券
     * 流程:
     * 1. 检查优惠券状态（进行中/有库存）
     * 2. 检查领取时间
     * 3. 检查用户领取限制（每人限领/每日限领）
     * 4. Redis Lua脚本原子扣减库存
     * 5. 创建UserCoupon记录
     * 6. 发送领取成功通知
     * 
     * 并发控制: 使用分布式锁防止超领
     * 
     * @param couponId 优惠券ID
     * @param userId 用户ID
     * @param receiveSource 领取来源
     * @param longitude 领取时经度
     * @param latitude 领取时纬度
     * @return 用户优惠券ID
     */
    Long receiveCoupon(Long couponId, Long userId, Integer receiveSource, 
                       BigDecimal longitude, BigDecimal latitude);
    
    /**
     * 地理围栏触发领取优惠券
     * 
     * 功能: 用户进入地理围栏自动触发领券
     * 调用时机: 地理围栏服务检测到用户进入围栏
     * 
     * @param couponId 优惠券ID
     * @param userId 用户ID
     * @param fenceId 围栏ID
     * @param longitude 经度
     * @param latitude 纬度
     * @return 是否领取成功
     */
    Boolean receiveCouponByGeofence(Long couponId, Long userId, Long fenceId,
                                     BigDecimal longitude, BigDecimal latitude);
    
    /**
     * 批量领取优惠券
     * 
     * 功能: 一键领取多个优惠券（如新人礼包）
     * 
     * @param couponIds 优惠券ID列表
     * @param userId 用户ID
     * @param source 领取来源
     * @return 成功领取的券列表
     */
    List<Long> batchReceiveCoupons(List<Long> couponIds, Long userId, Integer source);
    
    /**
     * 检查用户是否可领取
     * 
     * @param couponId 优惠券ID
     * @param userId 用户ID
     * @return 检查结果
     */
    ReceiveCheckResult checkCanReceive(Long couponId, Long userId);
    
    // ==================== 用户优惠券查询 ====================
    
    /**
     * 获取用户优惠券列表
     * 
     * @param userId 用户ID
     * @param status 状态筛选（null表示全部）
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    Page<UserCouponResponse> listUserCoupons(Long userId, Integer status, int page, int size);
    
    /**
     * 获取用户可用优惠券列表
     * 
     * 功能: 订单结算时展示可用优惠券
     * 
     * @param userId 用户ID
     * @param merchantId 商户ID
     * @param orderAmount 订单金额
     * @param categoryIds 商品品类列表
     * @return 可用优惠券列表（按优惠金额排序）
     */
    List<UserCouponResponse> listAvailableCoupons(Long userId, Long merchantId, 
                                                   BigDecimal orderAmount, List<Long> categoryIds);
    
    /**
     * 获取用户优惠券详情
     * 
     * @param userCouponId 用户优惠券ID
     * @param userId 用户ID（权限校验）
     * @return 详情
     */
    UserCouponDetailResponse getUserCouponDetail(Long userCouponId, Long userId);
    
    /**
     * 获取用户优惠券统计
     * 
     * @param userId 用户ID
     * @return 统计信息（未使用/已使用/已过期数量）
     */
    UserCouponStatistics getUserCouponStatistics(Long userId);
    
    // ==================== LBS附近优惠券 ====================
    
    /**
     * 搜索附近优惠券
     * 
     * 功能: 基于Redis Geo搜索用户附近的可用优惠券
     * 技术: Redis GEORADIUS命令
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 半径（米）
     * @param page 页码
     * @param size 每页数量
     * @return 附近优惠券列表（包含距离）
     */
    List<NearbyCouponResponse> searchNearbyCoupons(BigDecimal longitude, BigDecimal latitude, 
                                                    Integer radius, int page, int size);
    
    /**
     * 获取附近优惠券数量
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 半径
     * @return 数量
     */
    Long countNearbyCoupons(BigDecimal longitude, BigDecimal latitude, Integer radius);
    
    /**
     * 获取指定位置的优惠券
     * 
     * @param poiId POI ID
     * @param userId 用户ID
     * @return 该位置的可用优惠券
     */
    List<CouponListResponse> listCouponsByPoi(Long poiId, Long userId);
    
    // ==================== 优惠券核销 ====================
    
    /**
     * 核销优惠券
     * 
     * 功能: 订单结算时使用优惠券
     * 流程:
     * 1. 校验优惠券归属和状态
     * 2. 校验使用限制（商户/品类/金额）
     * 3. 计算优惠金额
     * 4. 乐观锁更新状态为已使用
     * 5. 记录使用详情
     * 
     * @param userCouponId 用户优惠券ID
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param orderNo 订单编号
     * @param orderAmount 订单金额
     * @param merchantId 商户ID
     * @param poiId POI ID
     * @param longitude 使用经度
     * @param latitude 使用纬度
     * @return 优惠金额
     */
    BigDecimal useCoupon(Long userCouponId, Long userId, Long orderId, String orderNo,
                         BigDecimal orderAmount, Long merchantId, Long poiId,
                         BigDecimal longitude, BigDecimal latitude);
    
    /**
     * 计算订单最优优惠券
     * 
     * 功能: 为用户订单推荐最优优惠券组合
     * 
     * @param userId 用户ID
     * @param merchantId 商户ID
     * @param orderAmount 订单金额
     * @param productIds 商品ID列表
     * @return 最优优惠券推荐
     */
    BestCouponRecommendation recommendBestCoupon(Long userId, Long merchantId, 
                                                  BigDecimal orderAmount, List<Long> productIds);
    
    /**
     * 订单取消退还优惠券
     * 
     * @param userCouponId 用户优惠券ID
     * @param orderId 订单ID
     * @return 是否退还成功
     */
    Boolean returnCoupon(Long userCouponId, Long orderId);
    
    /**
     * 预锁定优惠券（下单时锁定）
     * 
     * @param userCouponId 用户优惠券ID
     * @param orderId 订单ID
     * @param timeoutSeconds 锁定超时时间
     * @return 是否锁定成功
     */
    Boolean lockCoupon(Long userCouponId, Long orderId, Integer timeoutSeconds);
    
    /**
     * 释放锁定的优惠券
     * 
     * @param userCouponId 用户优惠券ID
     * @param orderId 订单ID
     */
    void unlockCoupon(Long userCouponId, Long orderId);
    
    // ==================== 转赠功能 ====================
    
    /**
     * 转赠优惠券给好友
     * 
     * @param userCouponId 用户优惠券ID
     * @param fromUserId 转出用户ID
     * @param toUserId 接收用户ID
     * @return 转赠结果
     */
    Boolean transferCoupon(Long userCouponId, Long fromUserId, Long toUserId);
    
    /**
     * 接受转赠的优惠券
     * 
     * @param transferCode 转赠码
     * @param toUserId 接收用户ID
     * @return 用户优惠券ID
     */
    Long acceptTransferredCoupon(String transferCode, Long toUserId);
    
    // ==================== 数据统计 ====================
    
    /**
     * 获取优惠券统计数据
     * 
     * @param couponId 优惠券ID
     * @return 统计数据
     */
    CouponStatisticsResponse getCouponStatistics(Long couponId);
    
    /**
     * 获取商户优惠券统计
     * 
     * @param merchantId 商户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    MerchantCouponStatistics getMerchantStatistics(Long merchantId, String startDate, String endDate);
    
    /**
     * 获取平台优惠券统计概览
     * 
     * @return 平台统计
     */
    PlatformCouponOverview getPlatformOverview();
    
    // ==================== 定时任务 ====================
    
    /**
     * 处理过期优惠券
     * 
     * 调用: 定时任务每分钟执行
     * 功能: 将已过期的优惠券状态更新为已过期
     * 
     * @return 处理数量
     */
    Integer processExpiredCoupons();
    
    /**
     * 发送即将过期提醒
     * 
     * 调用: 定时任务每天执行
     * 功能: 提醒用户3天内过期的优惠券
     * 
     * @return 发送数量
     */
    Integer sendExpireReminders();
    
    /**
     * 同步优惠券到Redis
     * 
     * 调用: 优惠券审核通过后/定时任务
     * 功能: 将进行中的优惠券同步到Redis缓存
     * 
     * @param couponId 优惠券ID
     */
    void syncCouponToRedis(Long couponId);
    
    /**
     * 清理已结束优惠券缓存
     * 
     * 调用: 定时任务
     */
    void cleanExpiredCouponCache();
}
