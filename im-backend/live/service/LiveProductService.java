package com.im.live.service;

import com.im.live.dto.LiveProductDTO;

import java.util.List;

/**
 * 直播商品服务接口
 */
public interface LiveProductService {

    /**
     * 添加直播商品
     */
    LiveProductDTO addProduct(LiveProductDTO productDTO, Long anchorId);

    /**
     * 更新直播商品
     */
    LiveProductDTO updateProduct(Long productId, LiveProductDTO productDTO, Long anchorId);

    /**
     * 删除直播商品
     */
    void deleteProduct(Long productId, Long anchorId);

    /**
     * 获取直播间商品列表
     */
    List<LiveProductDTO> getRoomProducts(Long roomId);

    /**
     * 开始讲解商品
     */
    void startExplainProduct(Long roomId, Long productId, Long anchorId);

    /**
     * 结束讲解商品
     */
    void endExplainProduct(Long roomId, Long productId, Long anchorId);

    /**
     * 更新商品库存
     */
    boolean updateStock(Long productId, Integer delta);

    /**
     * 上架商品
     */
    void shelveProduct(Long productId, Long anchorId);

    /**
     * 下架商品
     */
    void unshelveProduct(Long productId, Long anchorId);

    /**
     * 获取正在讲解的商品
     */
    LiveProductDTO getExplainingProduct(Long roomId);
}
