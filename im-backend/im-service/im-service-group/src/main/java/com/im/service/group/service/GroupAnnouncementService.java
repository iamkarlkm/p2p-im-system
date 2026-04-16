package com.im.service.group.service;

import com.im.service.group.dto.AnnouncementResponse;
import com.im.service.group.dto.CreateAnnouncementRequest;
import com.im.service.group.entity.Group;
import com.im.service.group.entity.GroupAnnouncement;
import com.im.service.group.repository.GroupAnnouncementRepository;
import com.im.service.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 群公告服务
 * 处理群公告的 CRUD 操作
 * 
 * @author IM Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupAnnouncementService {

    private final GroupAnnouncementRepository announcementRepository;
    private final GroupRepository groupRepository;

    /**
     * 创建群公告
     * 只有群主和管理员可以创建
     */
    @Transactional
    public AnnouncementResponse createAnnouncement(String userId, CreateAnnouncementRequest request) {
        // 验证群组是否存在
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("群组不存在"));
        
        // 验证权限（群主才能发公告）
        if (!Objects.equals(group.getOwnerId(), userId)) {
            throw new RuntimeException("只有群主才能发布公告");
        }

        GroupAnnouncement announcement = new GroupAnnouncement();
        announcement.setGroupId(request.getGroupId());
        announcement.setCreatorId(userId);
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setIsPinned(false);
        announcement.setReadCount(0);
        announcement.setDeleted(false);

        GroupAnnouncement saved = announcementRepository.save(announcement);
        log.info("用户 {} 在群 {} 创建公告 {}", userId, request.getGroupId(), saved.getId());
        
        return convertToResponse(saved, userId, true);
    }

    /**
     * 获取群公告列表
     */
    public List<AnnouncementResponse> getAnnouncements(String groupId, String currentUserId) {
        List<GroupAnnouncement> announcements = announcementRepository
                .findByGroupIdAndDeletedFalseOrderByIsPinnedDescCreatedAtDesc(groupId);
        
        return announcements.stream()
                .map(a -> convertToResponse(a, currentUserId, false))
                .collect(Collectors.toList());
    }

    /**
     * 分页获取群公告
     */
    public Page<AnnouncementResponse> getAnnouncementsPaged(String groupId, String currentUserId, 
                                                             int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("isPinned").descending()
                .and(Sort.by("createdAt").descending()));
        Page<GroupAnnouncement> announcements = announcementRepository
                .findByGroupIdAndDeletedFalse(groupId, pageable);
        
        return announcements.map(a -> convertToResponse(a, currentUserId, false));
    }

    /**
     * 获取群公告详情
     */
    public AnnouncementResponse getAnnouncementById(String announcementId, String currentUserId) {
        GroupAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        if (announcement.getDeleted()) {
            throw new RuntimeException("公告已删除");
        }
        
        return convertToResponse(announcement, currentUserId, false);
    }

    /**
     * 获取群最新公告
     */
    public AnnouncementResponse getLatestAnnouncement(String groupId, String currentUserId) {
        return announcementRepository.findFirstByGroupIdAndDeletedFalseOrderByIsPinnedDescCreatedAtDesc(groupId)
                .map(a -> convertToResponse(a, currentUserId, false))
                .orElse(null);
    }

    /**
     * 获取置顶公告
     */
    public List<AnnouncementResponse> getPinnedAnnouncements(String groupId, String currentUserId) {
        List<GroupAnnouncement> announcements = announcementRepository
                .findByGroupIdAndIsPinnedAndDeletedFalseTrueOrderByPinnedAtDesc(groupId);
        
        return announcements.stream()
                .map(a -> convertToResponse(a, currentUserId, false))
                .collect(Collectors.toList());
    }

    /**
     * 删除群公告
     * 只有创建者可以删除
     */
    @Transactional
    public void deleteAnnouncement(String announcementId, String userId) {
        GroupAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        if (!Objects.equals(announcement.getCreatorId(), userId)) {
            throw new RuntimeException("只有创建者可以删除公告");
        }
        
        announcement.markAsDeleted();
        announcementRepository.save(announcement);
        log.info("用户 {} 删除公告 {}", userId, announcementId);
    }

    /**
     * 置顶公告
     */
    @Transactional
    public void pinAnnouncement(String announcementId, String userId) {
        GroupAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        // 检查是否是群主
        Group group = groupRepository.findById(announcement.getGroupId())
                .orElseThrow(() -> new RuntimeException("群组不存在"));
        
        if (!Objects.equals(group.getOwnerId(), userId)) {
            throw new RuntimeException("只有群主可以置顶公告");
        }
        
        announcement.pin(userId);
        announcementRepository.save(announcement);
        log.info("用户 {} 置顶公告 {}", userId, announcementId);
    }

    /**
     * 取消置顶
     */
    @Transactional
    public void unpinAnnouncement(String announcementId, String userId) {
        GroupAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        Group group = groupRepository.findById(announcement.getGroupId())
                .orElseThrow(() -> new RuntimeException("群组不存在"));
        
        if (!Objects.equals(group.getOwnerId(), userId)) {
            throw new RuntimeException("只有群主可以取消置顶");
        }
        
        announcement.unpin();
        announcementRepository.save(announcement);
        log.info("用户 {} 取消置顶公告 {}", userId, announcementId);
    }

    /**
     * 标记公告已读
     */
    @Transactional
    public void markAsRead(String announcementId) {
        announcementRepository.incrementReadCount(announcementId);
    }

    /**
     * 获取未读数量
     */
    public Long getUnreadCount(String groupId, String userId) {
        // 这里简化处理，实际应该记录用户已读状态
        return announcementRepository.countByGroupIdAndDeletedFalse(groupId);
    }

    /**
     * 转换实体为响应DTO
     */
    private AnnouncementResponse convertToResponse(GroupAnnouncement a, String currentUserId, boolean checkRead) {
        return AnnouncementResponse.builder()
                .id(a.getId())
                .groupId(a.getGroupId())
                .creatorId(a.getCreatorId())
                .title(a.getTitle())
                .content(a.getContent())
                .isPinned(a.getIsPinned())
                .pinnedAt(a.getPinnedAt())
                .readCount(a.getReadCount())
                .createdAt(a.getCreatedAt())
                .isOwner(Objects.equals(a.getCreatorId(), currentUserId))
                .isRead(false) // 简化处理
                .build();
    }
}
