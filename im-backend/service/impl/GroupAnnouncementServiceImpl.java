package com.im.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.dto.GroupAnnouncementRequest;
import com.im.dto.GroupAnnouncementResponse;
import com.im.entity.GroupAnnouncement;
import com.im.repository.GroupAnnouncementRepository;
import com.im.service.GroupAnnouncementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 群公告服务实现类
 * 功能ID: #30
 * 功能名称: 群公告
 * 
 * @author developer-agent
 * @since 2026-03-30
 */
@Service
public class GroupAnnouncementServiceImpl implements GroupAnnouncementService {

    private static final Logger logger = LoggerFactory.getLogger(GroupAnnouncementServiceImpl.class);

    @Autowired
    private GroupAnnouncementRepository announcementRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // 模拟用户已读记录存储 (实际应使用Redis或数据库)
    // key: announcementId_userId, value: readTime

    @Override
    @Transactional
    public GroupAnnouncementResponse createAnnouncement(GroupAnnouncementRequest request, Long creatorId) {
        logger.info("Creating announcement for group: {}, title: {}, by user: {}", 
                request.getGroupId(), request.getTitle(), creatorId);

        GroupAnnouncement announcement = new GroupAnnouncement();
        announcement.setGroupId(request.getGroupId());
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setCreatorId(creatorId);
        announcement.setPinned(request.getPinned() != null ? request.getPinned() : false);
        announcement.setReadCount(0);
        announcement.setTotalMembers(0); // 应从群组服务获取
        announcement.setConfirmed(false);
        announcement.setDeleted(false);
        announcement.setReadUserIds("[]");

        // 处理附件
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            try {
                announcement.setAttachments(objectMapper.writeValueAsString(request.getAttachments()));
            } catch (Exception e) {
                logger.error("Failed to serialize attachments", e);
            }
        }

        // 如果设置为置顶，取消其他置顶
        if (announcement.getPinned()) {
            announcementRepository.clearAllPinned(request.getGroupId());
            announcement.setPinnedAt(LocalDateTime.now());
        }

        GroupAnnouncement saved = announcementRepository.save(announcement);
        logger.info("Announcement created successfully, id: {}", saved.getId());

        return convertToResponse(saved, creatorId);
    }

    @Override
    @Transactional
    public GroupAnnouncementResponse updateAnnouncement(Long id, GroupAnnouncementRequest request, Long operatorId) {
        logger.info("Updating announcement: {}, by user: {}", id, operatorId);

        GroupAnnouncement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));

        if (!announcement.getCreatorId().equals(operatorId)) {
            throw new RuntimeException("只有创建者可以编辑公告");
        }

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());

        // 处理附件
        if (request.getAttachments() != null) {
            try {
                announcement.setAttachments(objectMapper.writeValueAsString(request.getAttachments()));
            } catch (Exception e) {
                logger.error("Failed to serialize attachments", e);
            }
        }

        // 处理置顶状态变更
        Boolean newPinned = request.getPinned();
        if (newPinned != null && !newPinned.equals(announcement.getPinned())) {
            if (newPinned) {
                announcementRepository.clearAllPinned(announcement.getGroupId());
                announcement.setPinnedAt(LocalDateTime.now());
            } else {
                announcement.setPinnedAt(null);
            }
            announcement.setPinned(newPinned);
        }

        GroupAnnouncement updated = announcementRepository.save(announcement);
        logger.info("Announcement updated successfully, id: {}", id);

        return convertToResponse(updated, operatorId);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id, Long operatorId) {
        logger.info("Deleting announcement: {}, by user: {}", id, operatorId);

        GroupAnnouncement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));

        if (!announcement.getCreatorId().equals(operatorId)) {
            throw new RuntimeException("只有创建者可以删除公告");
        }

        announcementRepository.softDelete(id, LocalDateTime.now());
        logger.info("Announcement soft deleted successfully, id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupAnnouncementResponse getAnnouncement(Long id, Long userId) {
        GroupAnnouncement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));

        if (announcement.getDeleted()) {
            throw new RuntimeException("公告已被删除");
        }

        return convertToResponse(announcement, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupAnnouncementResponse> getGroupAnnouncements(Long groupId, Long userId) {
        List<GroupAnnouncement> announcements = announcementRepository.findByGroupId(groupId);
        return announcements.stream()
                .map(a -> convertToResponse(a, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupAnnouncementResponse> getGroupAnnouncementsPaged(Long groupId, Long userId, Pageable pageable) {
        Page<GroupAnnouncement> page = announcementRepository.findByGroupIdPaged(groupId, pageable);
        List<GroupAnnouncementResponse> responses = page.getContent().stream()
                .map(a -> convertToResponse(a, userId))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public GroupAnnouncementResponse getLatestAnnouncement(Long groupId, Long userId) {
        List<GroupAnnouncement> announcements = announcementRepository.findLatestByGroupId(groupId, 
                org.springframework.data.domain.PageRequest.of(0, 1));
        if (announcements.isEmpty()) {
            return null;
        }
        return convertToResponse(announcements.get(0), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupAnnouncementResponse> getPinnedAnnouncements(Long groupId, Long userId) {
        List<GroupAnnouncement> announcements = announcementRepository.findPinnedByGroupId(groupId);
        return announcements.stream()
                .map(a -> convertToResponse(a, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Long userId) {
        logger.info("Marking announcement {} as read by user {}", id, userId);

        GroupAnnouncement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));

        if (announcement.getDeleted()) {
            throw new RuntimeException("公告已被删除");
        }

        // 检查用户是否已读
        if (!hasUserRead(announcement, userId)) {
            // 添加到已读列表
            addUserToReadList(announcement, userId);
            // 增加已读计数
            announcementRepository.incrementReadCount(id);
            
            logger.info("Announcement {} marked as read by user {}", id, userId);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long groupId, Long userId) {
        logger.info("Marking all announcements as read for group {} by user {}", groupId, userId);
        
        List<GroupAnnouncement> announcements = announcementRepository.findByGroupId(groupId);
        for (GroupAnnouncement announcement : announcements) {
            if (!hasUserRead(announcement, userId)) {
                addUserToReadList(announcement, userId);
                announcementRepository.incrementReadCount(announcement.getId());
            }
        }
    }

    @Override
    @Transactional
    public void pinAnnouncement(Long id, Boolean pinned, Long operatorId) {
        logger.info("Pinning announcement: {}, pinned: {}, by user: {}", id, pinned, operatorId);

        GroupAnnouncement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));

        if (pinned) {
            announcementRepository.clearAllPinned(announcement.getGroupId());
            announcementRepository.updatePinnedStatus(id, true, LocalDateTime.now());
        } else {
            announcementRepository.updatePinnedStatus(id, false, null);
        }

        logger.info("Announcement pin status updated, id: {}, pinned: {}", id, pinned);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getReadCount(Long id) {
        GroupAnnouncement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        return announcement.getReadCount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupAnnouncementResponse> searchAnnouncements(Long groupId, String keyword, Long userId) {
        if (!StringUtils.hasText(keyword)) {
            return getGroupAnnouncements(groupId, userId);
        }
        List<GroupAnnouncement> announcements = announcementRepository.searchByKeyword(groupId, keyword);
        return announcements.stream()
                .map(a -> convertToResponse(a, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long groupId, Long userId) {
        List<GroupAnnouncement> announcements = announcementRepository.findByGroupId(groupId);
        return announcements.stream()
                .filter(a -> !hasUserRead(a, userId))
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCreator(Long id, Long userId) {
        return announcementRepository.isCreator(id, userId);
    }

    // 辅助方法

    private boolean hasUserRead(GroupAnnouncement announcement, Long userId) {
        try {
            List<Long> readUsers = objectMapper.readValue(announcement.getReadUserIds(), 
                    new TypeReference<List<Long>>() {});
            return readUsers.contains(userId);
        } catch (Exception e) {
            logger.error("Failed to parse read user ids", e);
            return false;
        }
    }

    private void addUserToReadList(GroupAnnouncement announcement, Long userId) {
        try {
            List<Long> readUsers = objectMapper.readValue(announcement.getReadUserIds(), 
                    new TypeReference<List<Long>>() {});
            if (readUsers == null) {
                readUsers = new ArrayList<>();
            }
            if (!readUsers.contains(userId)) {
                readUsers.add(userId);
                announcement.setReadUserIds(objectMapper.writeValueAsString(readUsers));
                announcement.incrementReadCount();
                announcementRepository.save(announcement);
            }
        } catch (Exception e) {
            logger.error("Failed to update read user list", e);
        }
    }

    private GroupAnnouncementResponse convertToResponse(GroupAnnouncement announcement, Long currentUserId) {
        GroupAnnouncementResponse response = new GroupAnnouncementResponse();
        response.setId(announcement.getId());
        response.setGroupId(announcement.getGroupId());
        response.setTitle(announcement.getTitle());
        response.setContent(announcement.getContent());
        response.setCreatorId(announcement.getCreatorId());
        response.setPinned(announcement.getPinned());
        response.setPinnedAt(announcement.getPinnedAt());
        response.setConfirmed(announcement.getConfirmed());
        response.setReadCount(announcement.getReadCount());
        response.setTotalMembers(announcement.getTotalMembers());
        response.setReadPercentage(announcement.getReadPercentage());
        response.setCreatedAt(announcement.getCreatedAt());
        response.setUpdatedAt(announcement.getUpdatedAt());
        response.setHasRead(hasUserRead(announcement, currentUserId));

        // 解析附件
        if (StringUtils.hasText(announcement.getAttachments())) {
            try {
                List<String> attachments = objectMapper.readValue(announcement.getAttachments(), 
                        new TypeReference<List<String>>() {});
                response.setAttachments(attachments);
            } catch (Exception e) {
                logger.error("Failed to parse attachments", e);
                response.setAttachments(new ArrayList<>());
            }
        } else {
            response.setAttachments(new ArrayList<>());
        }

        // TODO: 从用户服务获取创建者信息
        response.setCreatorNickname("用户" + announcement.getCreatorId());
        response.setCreatorAvatar("");

        return response;
    }
}
