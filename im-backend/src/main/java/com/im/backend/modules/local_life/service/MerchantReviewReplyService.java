package com.im.backend.modules.local_life.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.local_life.dto.CreateReplyRequestDTO;
import com.im.backend.modules.local_life.dto.MerchantReviewReplyDTO;
import com.im.backend.modules.local_life.entity.MerchantReviewReply;

import java.util.List;

/**
 * 评价回复 Service
 */
public interface MerchantReviewReplyService extends IService<MerchantReviewReply> {

    /**
     * 创建回复
     */
    MerchantReviewReplyDTO createReply(Long replyBy, Integer replyType, CreateReplyRequestDTO request);

    /**
     * 获取评价的回复列表
     */
    List<MerchantReviewReplyDTO> getReviewReplies(Long reviewId);

    /**
     * 删除回复
     */
    void deleteReply(Long replyId, Long operatorId, Integer operatorType);

    /**
     * 商家置顶回复
     */
    void pinReply(Long replyId, Long merchantId);

    /**
     * 取消置顶
     */
    void unpinReply(Long replyId, Long merchantId);
}
