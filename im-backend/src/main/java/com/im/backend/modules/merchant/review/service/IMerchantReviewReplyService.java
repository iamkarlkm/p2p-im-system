package com.im.backend.modules.merchant.review.service;

import com.im.backend.modules.merchant.review.dto.ReplyReviewRequest;
import com.im.backend.modules.merchant.review.dto.ReviewReplyDTO;

import java.util.List;

/**
 * 评价回复服务接口
 */
public interface IMerchantReviewReplyService {

    /**
     * 回复评价
     */
    String replyReview(Long replierId, Integer replierType, ReplyReviewRequest request);

    /**
     * 获取评价的回复列表
     */
    List<ReviewReplyDTO> getReviewReplies(String reviewId);

    /**
     * 删除回复
     */
    void deleteReply(String replyId, Long replierId);
}
