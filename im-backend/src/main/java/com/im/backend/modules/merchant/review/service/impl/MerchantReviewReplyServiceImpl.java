package com.im.backend.modules.merchant.review.service.impl;

import com.im.backend.common.util.SnowflakeIdGenerator;
import com.im.backend.modules.merchant.review.dto.ReplyReviewRequest;
import com.im.backend.modules.merchant.review.dto.ReviewReplyDTO;
import com.im.backend.modules.merchant.review.entity.MerchantReviewReply;
import com.im.backend.modules.merchant.review.enums.ReplierType;
import com.im.backend.modules.merchant.review.repository.MerchantReviewMapper;
import com.im.backend.modules.merchant.review.repository.MerchantReviewReplyMapper;
import com.im.backend.modules.merchant.review.service.IMerchantReviewReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评价回复服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantReviewReplyServiceImpl implements IMerchantReviewReplyService {

    private final MerchantReviewReplyMapper replyMapper;
    private final MerchantReviewMapper reviewMapper;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional
    public String replyReview(Long replierId, Integer replierType, ReplyReviewRequest request) {
        MerchantReviewReply reply = MerchantReviewReply.builder()
                .replyId("RPL" + idGenerator.nextId())
                .reviewId(request.getReviewId())
                .parentReplyId(request.getParentReplyId())
                .replierId(replierId)
                .replierType(replierType)
                .content(request.getContent())
                .images(request.getImages())
                .likeCount(0)
                .status(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        replyMapper.insert(reply);

        // 更新评价回复数
        reviewMapper.incrementReplyCount(request.getReviewId());

        log.info("{} {} 回复评价 {}", replierType == 1 ? "商户" : "用户", replierId, request.getReviewId());
        return reply.getReplyId();
    }

    @Override
    public List<ReviewReplyDTO> getReviewReplies(String reviewId) {
        List<MerchantReviewReply> replies = replyMapper.selectByReviewId(reviewId);

        // 构建回复树
        Map<String, List<MerchantReviewReply>> parentMap = replies.stream()
                .filter(r -> r.getParentReplyId() != null)
                .collect(Collectors.groupingBy(MerchantReviewReply::getParentReplyId));

        List<ReviewReplyDTO> result = new ArrayList<>();
        for (MerchantReviewReply reply : replies) {
            if (reply.getParentReplyId() == null) {
                ReviewReplyDTO dto = convertToDTO(reply);
                result.add(dto);
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void deleteReply(String replyId, Long replierId) {
        MerchantReviewReply reply = replyMapper.selectByReplyId(replyId);
        if (reply == null) {
            throw new RuntimeException("回复不存在");
        }
        if (!reply.getReplierId().equals(replierId)) {
            throw new RuntimeException("无权删除该回复");
        }

        reply.setStatus(2); // 已删除
        reply.setUpdatedAt(LocalDateTime.now());
        replyMapper.updateById(reply);

        log.info("用户 {} 删除回复 {}", replierId, replyId);
    }

    private ReviewReplyDTO convertToDTO(MerchantReviewReply reply) {
        ReviewReplyDTO dto = new ReviewReplyDTO();
        dto.setReplyId(reply.getReplyId());
        dto.setParentReplyId(reply.getParentReplyId());
        dto.setReplierId(reply.getReplierId());
        dto.setReplierType(reply.getReplierType());
        dto.setContent(reply.getContent());
        dto.setImages(reply.getImages());
        dto.setLikeCount(reply.getLikeCount());
        dto.setCreatedAt(reply.getCreatedAt());
        return dto;
    }
}
