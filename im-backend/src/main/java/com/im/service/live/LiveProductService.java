package com.im.service.live;

import com.im.common.PageResult;
import com.im.dto.live.*;
import java.util.List;

/**
 * 直播商品服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface LiveProductService {

    /**
     * 添加直播商品
     *
     * @param roomId  直播间ID
     * @param userId  操作用户ID
     * @param request 商品信息
     * @return 商品详情
     */
    LiveProductDTO addProduct(Long roomId, Long userId, AddLiveProductRequestDTO request);

    /**
     * 更新直播商品
     *
     * @param productId 商品ID
     * @param userId    操作用户ID
     * @param request   商品信息
     * @return 商品详情
     */
    LiveProductDTO updateProduct(Long productId, Long userId, UpdateLiveProductRequestDTO request);

    /**
     * 删除直播商品
     *
     * @param productId 商品ID
     * @param userId    操作用户ID
     */
    void deleteProduct(Long productId, Long userId);

    /**
     * 获取商品详情
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    LiveProductDTO getProductDetail(Long productId);

    /**
     * 获取直播间商品列表
     *
     * @param roomId 直播间ID
     * @param status 商品状态
     * @return 商品列表
     */
    List<LiveProductDTO> listRoomProducts(Long roomId, Integer status);

    /**
     * 开始讲解商品
     *
     * @param roomId    直播间ID
     * @param productId 商品ID
     * @param userId    主播用户ID
     */
    void startExplainProduct(Long roomId, Long productId, Long userId);

    /**
     * 结束讲解商品
     *
     * @param roomId    直播间ID
     * @param productId 商品ID
     * @param userId    主播用户ID
     */
    void endExplainProduct(Long roomId, Long productId, Long userId);

    /**
     * 上架商品
     *
     * @param productId 商品ID
     * @param userId    操作用户ID
     */
    void onShelf(Long productId, Long userId);

    /**
     * 下架商品
     *
     * @param productId 商品ID
     * @param userId    操作用户ID
     */
    void offShelf(Long productId, Long userId);

    /**
     * 检查库存
     *
     * @param productId 商品ID
     * @param quantity  购买数量
     * @return 是否充足
     */
    boolean checkStock(Long productId, Integer quantity);

    /**
     * 扣减库存
     *
     * @param productId 商品ID
     * @param quantity  扣减数量
     * @return 是否成功
     */
    boolean decreaseStock(Long productId, Integer quantity);

    /**
     * 恢复库存
     *
     * @param productId 商品ID
     * @param quantity  恢复数量
     */
    void restoreStock(Long productId, Integer quantity);
}
