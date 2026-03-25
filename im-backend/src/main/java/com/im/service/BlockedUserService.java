package com.im.service;

import com.im.dto.BlockedUserDTO;
import com.im.entity.BlockedUserEntity;
import com.im.entity.UserEntity;
import com.im.repository.BlockedUserRepository;
import com.im.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户黑名单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlockedUserService {
    
    private final BlockedUserRepository blockedUserRepository;
    private final UserRepository userRepository;
    
    /**
     * 拉黑用户
     */
    @Transactional
    public BlockedUserDTO blockUser(Long blockerId, Long blockedId, String reason) {
        // 检查是否已经拉黑
        if (blockedUserRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            log.warn("用户 {} 已经拉黑了用户 {}", blockerId, blockedId);
            return null;
        }
        
        // 不能拉黑自己
        if (blockerId.equals(blockedId)) {
            log.warn("用户 {} 不能拉黑自己", blockerId);
            return null;
        }
        
        BlockedUserEntity entity = BlockedUserEntity.builder()
                .blockerId(blockerId)
                .blockedId(blockedId)
                .reason(reason)
                .hideOnlineStatus(true)
                .muteMessages(true)
                .build();
        
        entity = blockedUserRepository.save(entity);
        log.info("用户 {} 拉黑了用户 {}", blockerId, blockedId);
        
        return toDTO(entity);
    }
    
    /**
     * 解除拉黑
     */
    @Transactional
    public boolean unblockUser(Long blockerId, Long blockedId) {
        if (!blockedUserRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            log.warn("用户 {} 没有拉黑用户 {}", blockerId, blockedId);
            return false;
        }
        
        blockedUserRepository.unblockUser(blockerId, blockedId);
        log.info("用户 {} 解除了对用户 {} 的拉黑", blockerId, blockedId);
        return true;
    }
    
    /**
     * 获取用户的黑名单列表
     */
    public List<BlockedUserDTO> getBlockedUsers(Long blockerId) {
        List<BlockedUserEntity> entities = blockedUserRepository.findByBlockerId(blockerId);
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    /**
     * 获取用户的所有黑名单ID
     */
    public List<Long> getBlockedUserIds(Long blockerId) {
        return blockedUserRepository.findBlockedIdsByBlockerId(blockerId);
    }
    
    /**
     * 检查是否已拉黑某个用户
     */
    public boolean isUserBlocked(Long blockerId, Long blockedId) {
        return blockedUserRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
    }
    
    /**
     * 批量检查是否在黑名单中
     */
    public List<Long> checkBlockedUsers(Long blockerId, List<Long> userIds) {
        return blockedUserRepository.findBlockedIdsInList(blockerId, userIds);
    }
    
    /**
     * 获取黑名单数量
     */
    public long getBlockedCount(Long blockerId) {
        return blockedUserRepository.countByBlockerId(blockerId);
    }
    
    /**
     * 检查双向拉黑
     */
    public boolean isMutualBlock(Long userId1, Long userId2) {
        return blockedUserRepository.isBlockedByUser(userId1, userId2) 
            && blockedUserRepository.isBlockedByUser(userId2, userId1);
    }
    
    /**
     * 实体转DTO
     */
    private BlockedUserDTO toDTO(BlockedUserEntity entity) {
        if (entity == null) return null;
        
        // 获取被拉黑用户的信息
        UserEntity blockedUser = userRepository.findById(entity.getBlockedId()).orElse(null);
        
        return BlockedUserDTO.builder()
                .id(entity.getId())
                .blockedId(entity.getBlockedId())
                .blockedUsername(blockedUser != null ? blockedUser.getUsername() : null)
                .blockedAvatar(blockedUser != null ? blockedUser.getAvatar() : null)
                .reason(entity.getReason())
                .blockedAt(entity.getBlockedAt())
                .hideOnlineStatus(entity.getHideOnlineStatus())
                .muteMessages(entity.getMuteMessages())
                .build();
    }
}
