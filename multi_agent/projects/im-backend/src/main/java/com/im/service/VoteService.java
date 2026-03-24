package com.im.service;

import com.im.entity.VoteEntity;
import com.im.exception.VoteException;
import com.im.repository.VoteRepository;
import com.im.dto.VoteCreateRequest;
import com.im.dto.VoteResponse;
import com.im.dto.VoteSubmitRequest;
import com.im.dto.VoteStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoteService {
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Autowired
    private MessageService messageService;
    
    @Transactional
    public VoteResponse createVote(VoteCreateRequest request) {
        // 验证参数
        validateVoteRequest(request);
        
        VoteEntity vote = new VoteEntity();
        vote.setMessageId(request.getMessageId());
        vote.setGroupId(request.getGroupId());
        vote.setUserId(request.getUserId());
        vote.setTitle(request.getTitle());
        vote.setDescription(request.getDescription());
        vote.setOptions(request.getOptions());
        vote.setIsAnonymous(request.getIsAnonymous());
        vote.setAllowMultipleChoice(request.getAllowMultipleChoice());
        vote.setEndTime(request.getEndTime());
        
        // 初始化投票结果
        List<VoteEntity.VoteResult> results = new ArrayList<>();
        for (int i = 0; i < request.getOptions().size(); i++) {
            VoteEntity.VoteResult result = new VoteEntity.VoteResult();
            result.setOptionIndex(i);
            result.setVoteCount(0);
            results.add(result);
        }
        vote.setResults(results);
        
        VoteEntity savedVote = voteRepository.save(vote);
        
        // 发送消息通知（可选）
        sendVoteCreatedNotification(savedVote);
        
        return convertToVoteResponse(savedVote);
    }
    
    @Transactional
    public VoteResponse submitVote(VoteSubmitRequest request) {
        Optional<VoteEntity> optionalVote = voteRepository.findById(request.getVoteId());
        if (!optionalVote.isPresent()) {
            throw new VoteException("投票不存在");
        }
        
        VoteEntity vote = optionalVote.get();
        
        // 检查投票是否已关闭
        if (vote.getIsClosed()) {
            throw new VoteException("投票已结束");
        }
        
        // 检查是否已投票（对于非匿名投票）
        if (!vote.getIsAnonymous()) {
            Boolean hasVoted = voteRepository.hasUserVoted(vote.getId(), request.getUserId());
            if (hasVoted != null && hasVoted) {
                throw new VoteException("您已经投过票了");
            }
        }
        
        // 检查是否允许多选
        if (!vote.getAllowMultipleChoice() && request.getSelectedOptions().size() > 1) {
            throw new VoteException("此投票不支持多选");
        }
        
        // 更新投票结果
        List<VoteEntity.VoteResult> results = vote.getResults();
        for (Integer optionIndex : request.getSelectedOptions()) {
            if (optionIndex < 0 || optionIndex >= results.size()) {
                throw new VoteException("无效的选项索引: " + optionIndex);
            }
            
            VoteEntity.VoteResult result = results.get(optionIndex);
            result.setVoteCount(result.getVoteCount() + 1);
            
            // 记录参与者（非匿名）
            if (!vote.getIsAnonymous()) {
                result.getParticipantUserIds().add(request.getUserId());
            }
        }
        
        // 更新总票数
        voteRepository.incrementTotalVotes(vote.getId());
        vote.setTotalVotes(vote.getTotalVotes() + 1);
        vote.setUpdatedAt(LocalDateTime.now());
        
        voteRepository.save(vote);
        
        return convertToVoteResponse(vote);
    }
    
    public VoteResponse getVoteById(Long voteId) {
        Optional<VoteEntity> vote = voteRepository.findById(voteId);
        if (!vote.isPresent()) {
            throw new VoteException("投票不存在");
        }
        return convertToVoteResponse(vote.get());
    }
    
    public VoteResponse getVoteByMessageId(Long messageId) {
        Optional<VoteEntity> vote = voteRepository.findByMessageId(messageId);
        if (!vote.isPresent()) {
            throw new VoteException("消息没有关联的投票");
        }
        return convertToVoteResponse(vote.get());
    }
    
    public List<VoteResponse> getGroupVotes(Long groupId, Boolean activeOnly) {
        List<VoteEntity> votes;
        if (activeOnly != null && activeOnly) {
            votes = voteRepository.findActiveVotesByGroup(groupId);
        } else {
            votes = voteRepository.findByGroupId(groupId);
        }
        
        List<VoteResponse> responses = new ArrayList<>();
        for (VoteEntity vote : votes) {
            responses.add(convertToVoteResponse(vote));
        }
        return responses;
    }
    
    @Transactional
    public void closeVote(Long voteId) {
        voteRepository.closeVote(voteId);
        VoteEntity vote = voteRepository.findById(voteId).orElse(null);
        if (vote != null) {
            sendVoteClosedNotification(vote);
        }
    }
    
    public VoteStatistics getVoteStatistics(Long voteId) {
        Optional<VoteEntity> vote = voteRepository.findById(voteId);
        if (!vote.isPresent()) {
            throw new VoteException("投票不存在");
        }
        
        VoteStatistics stats = new VoteStatistics();
        stats.setTotalVotes(vote.get().getTotalVotes());
        stats.setUniqueParticipants(voteRepository.countUniqueParticipants(voteId));
        
        List<VoteEntity.VoteResult> results = vote.get().getResults();
        List<Integer> voteCounts = new ArrayList<>();
        for (VoteEntity.VoteResult result : results) {
            voteCounts.add(result.getVoteCount());
        }
        stats.setOptionVoteCounts(voteCounts);
        
        return stats;
    }
    
    @Scheduled(fixedDelay = 60000) // 每分钟检查一次
    @Transactional
    public void checkExpiredVotes() {
        List<VoteEntity> expiredVotes = voteRepository.findExpiredVotes(LocalDateTime.now());
        for (VoteEntity vote : expiredVotes) {
            voteRepository.closeVote(vote.getId());
            sendVoteClosedNotification(vote);
        }
    }
    
    private void validateVoteRequest(VoteCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new VoteException("投票标题不能为空");
        }
        
        if (request.getOptions() == null || request.getOptions().size() < 2) {
            throw new VoteException("至少需要两个选项");
        }
        
        if (request.getOptions().size() > 10) {
            throw new VoteException("最多支持10个选项");
        }
        
        for (String option : request.getOptions()) {
            if (option == null || option.trim().isEmpty()) {
                throw new VoteException("选项内容不能为空");
            }
        }
    }
    
    private VoteResponse convertToVoteResponse(VoteEntity vote) {
        VoteResponse response = new VoteResponse();
        response.setId(vote.getId());
        response.setMessageId(vote.getMessageId());
        response.setGroupId(vote.getGroupId());
        response.setUserId(vote.getUserId());
        response.setTitle(vote.getTitle());
        response.setDescription(vote.getDescription());
        response.setOptions(vote.getOptions());
        response.setIsAnonymous(vote.getIsAnonymous());
        response.setAllowMultipleChoice(vote.getAllowMultipleChoice());
        response.setEndTime(vote.getEndTime());
        response.setTotalVotes(vote.getTotalVotes());
        response.setIsClosed(vote.getIsClosed());
        response.setCreatedAt(vote.getCreatedAt());
        response.setUpdatedAt(vote.getUpdatedAt());
        
        // 根据是否匿名决定是否返回投票详情
        if (!vote.getIsAnonymous()) {
            List<VoteEntity.VoteResult> results = vote.getResults();
            List<Integer> voteCounts = new ArrayList<>();
            for (VoteEntity.VoteResult result : results) {
                voteCounts.add(result.getVoteCount());
            }
            response.setOptionVoteCounts(voteCounts);
        }
        
        return response;
    }
    
    private void sendVoteCreatedNotification(VoteEntity vote) {
        // 发送消息通知逻辑
        // 这里可以调用消息服务发送通知
    }
    
    private void sendVoteClosedNotification(VoteEntity vote) {
        // 发送投票结束通知逻辑
        // 这里可以调用消息服务发送通知
    }
}