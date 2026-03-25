package com.im.backend.service;

import com.im.backend.entity.MessageReplyChain;
import com.im.backend.entity.ReplyChainNode;
import com.im.backend.dto.ReplyChainRequest;
import com.im.backend.dto.ReplyChainResponse;
import com.im.backend.dto.ReplyChainResponse.ReplyNodeResponse;
import com.im.backend.dto.ReplyChainResponse.MessageContext;
import com.im.backend.repository.MessageReplyChainRepository;
import com.im.backend.repository.ReplyChainNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReplyChainService {
    
    private final MessageReplyChainRepository chainRepository;
    private final ReplyChainNodeRepository nodeRepository;
    
    private static final int MAX_DEPTH = 10;
    
    @Transactional
    public ReplyChainResponse createReplyChain(ReplyChainRequest request, Long userId, String userNickname) {
        log.info("Creating reply chain for conversation: {}, user: {}", request.getConversationId(), userId);
        
        int depth = request.getDepth() != null ? request.getDepth() : 0;
        if (depth >= MAX_DEPTH) {
            depth = MAX_DEPTH - 1;
        }
        
        String branchPath = request.getBranchPath() != null 
            ? request.getBranchPath() + "/" + request.getParentMessageId()
            : "/" + request.getRootMessageId();
        
        MessageReplyChain chain = MessageReplyChain.builder()
                .conversationId(request.getConversationId())
                .rootMessageId(request.getRootMessageId())
                .parentMessageId(request.getParentMessageId())
                .userId(userId)
                .depth(depth)
                .branchPath(branchPath)
                .isBranchNode(depth > 0)
                .isRoot(request.getRootMessageId().equals(request.getParentMessageId()))
                .isLeaf(true)
                .build();
        
        chain = chainRepository.save(chain);
        
        ReplyChainNode node = ReplyChainNode.builder()
                .chainId(chain.getId())
                .messageId(request.getParentMessageId())
                .userId(userId)
                .userNickname(userNickname)
                .contentPreview("")
                .messageType("text")
                .positionInBranch(0)
                .isDeleted(false)
                .build();
        nodeRepository.save(node);
        
        return buildChainResponse(chain);
    }
    
    @Transactional(readOnly = true)
    public ReplyChainResponse getReplyChain(Long chainId) {
        MessageReplyChain chain = chainRepository.findById(chainId)
                .orElseThrow(() -> new RuntimeException("Reply chain not found: " + chainId));
        return buildChainResponse(chain);
    }
    
    @Transactional(readOnly = true)
    public List<ReplyChainResponse> getConversationReplyChains(Long conversationId) {
        List<MessageReplyChain> roots = chainRepository.findRootChainsByConversation(conversationId);
        return roots.stream()
                .map(this::buildChainResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ReplyChainResponse getBranchTree(Long rootMessageId) {
        List<MessageReplyChain> allChains = chainRepository.findByRootMessageId(rootMessageId);
        if (allChains.isEmpty()) {
            throw new RuntimeException("No reply chain found for root message: " + rootMessageId);
        }
        
        MessageReplyChain root = allChains.stream()
                .filter(MessageReplyChain::getIsRoot)
                .findFirst()
                .orElse(allChains.get(0));
        
        ReplyChainResponse response = buildChainResponse(root);
        
        Map<Long, List<MessageReplyChain>> childrenMap = allChains.stream()
                .collect(Collectors.groupingBy(MessageReplyChain::getParentMessageId));
        
        List<ReplyChainResponse> branchNodes = buildBranchNodes(root, childrenMap);
        response.setBranchNodes(branchNodes);
        
        return response;
    }
    
    private List<ReplyChainResponse> buildBranchNodes(MessageReplyChain parent, 
            Map<Long, List<MessageReplyChain>> childrenMap) {
        List<ReplyChainResponse> result = new ArrayList<>();
        List<MessageReplyChain> children = childrenMap.getOrDefault(parent.getParentMessageId(), 
                Collections.emptyList());
        
        for (MessageReplyChain child : children) {
            ReplyChainResponse node = buildChainResponse(child);
            node.setBranchNodes(buildBranchNodes(child, childrenMap));
            result.add(node);
        }
        
        return result;
    }
    
    @Transactional
    public void markMessageDeleted(Long messageId) {
        List<ReplyChainNode> nodes = nodeRepository.findByMessageId(messageId);
        for (ReplyChainNode node : nodes) {
            node.setIsDeleted(true);
            nodeRepository.save(node);
        }
    }
    
    @Transactional(readOnly = true)
    public MessageContext getMessageContext(Long messageId) {
        return MessageContext.builder()
                .messageId(messageId)
                .content("Message content preview")
                .senderName("Sender")
                .messageType("text")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    @Transactional
    public void deleteChain(Long chainId) {
        chainRepository.deleteById(chainId);
        nodeRepository.deleteByChainId(chainId);
    }
    
    private ReplyChainResponse buildChainResponse(MessageReplyChain chain) {
        List<ReplyChainNode> nodes = nodeRepository.findActiveNodesByChain(chain.getId());
        List<ReplyNodeResponse> nodeResponses = nodes.stream()
                .map(n -> ReplyNodeResponse.builder()
                        .id(n.getId())
                        .messageId(n.getMessageId())
                        .userId(n.getUserId())
                        .userNickname(n.getUserNickname())
                        .contentPreview(n.getContentPreview())
                        .messageType(n.getMessageType())
                        .positionInBranch(n.getPositionInBranch())
                        .createdAt(n.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return ReplyChainResponse.builder()
                .id(chain.getId())
                .conversationId(chain.getConversationId())
                .rootMessageId(chain.getRootMessageId())
                .parentMessageId(chain.getParentMessageId())
                .userId(chain.getUserId())
                .depth(chain.getDepth())
                .branchPath(chain.getBranchPath())
                .isBranchNode(chain.getIsBranchNode())
                .isRoot(chain.getIsRoot())
                .isLeaf(chain.getIsLeaf())
                .createdAt(chain.getCreatedAt())
                .updatedAt(chain.getUpdatedAt())
                .branchNodes(nodeResponses)
                .build();
    }
}
