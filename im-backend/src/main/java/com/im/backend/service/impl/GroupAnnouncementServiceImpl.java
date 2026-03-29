package com.im.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.backend.dto.AnnouncementDTO;
import com.im.backend.dto.AnnouncementResponseDTO;
import com.im.backend.exception.BusinessException;
import com.im.backend.model.AnnouncementReadRecord;
import com.im.backend.model.GroupAnnouncement;
import com.im.backend.model.GroupMember;
import com.im.backend.repository.AnnouncementReadRecordRepository;
import com.im.backend.repository.GroupAnnouncementRepository;
import com.im.backend.repository.GroupMemberRepository;
import com.im.backend.service.GroupAnnouncementService;
import com.im.backend.service.GroupService;
import com.im.backend.service.UserService;
import com.im.backend.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 群公告服务实现
 */
@Service
public class GroupAnnouncementServiceImpl implements GroupAnnouncementService {

    private static final Logger logger = LoggerFactory.getLogger(GroupAnnouncementServiceImpl.class);

    @Autowired
    private GroupAnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementReadRecordRepository readRecordRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public AnnouncementResponseDTO publishAnnouncement(Long publisherId, AnnouncementDTO dto) {
        logger.info("发布公告: groupId={}, publisherId={}", dto.getGroupId(), publisherId);

        // 检查权限
        checkPublishPermission(dto.getGroupId(), publisherId);

        // 创建公告实体
        GroupAnnouncement announcement = dto.toEntity(publisherId);

        // 处理附件
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            try {
                announcement.setAttachments(objectMapper.writeValueAsString(dto.getAttachments()));
            } catch (JsonProcessingException e) {
                logger.error("附件序列化失败", e);
            }
        }

        // 获取群组成员数
        Long memberCount = groupMemberRepository.countByGroupId(dto.getGroupId());
        announcement.setTotalMembers(memberCount.intValue());

        // 设置需要确认的人数
        if (Boolean.TRUE.equals(dto.getNeedConfirm())) {
            announcement.setNeedConfirmCount(memberCount.intValue());
        }

        // 保存公告
        announcement = announcementRepository.save(announcement);

        // 发送通知
        if (Boolean.TRUE.equals(dto.getSendNotification())) {
            sendAnnouncementNotification(announcement, dto.getNotifyMemberIds());
        }

        logger.info("公告发布成功: announcementId={}", announcement.getId());
        return convertToDTO(announcement, publisherId);
    }

    @Override
    @Transactional
    public AnnouncementResponseDTO editAnnouncement(Long announcementId, Long operatorId, AnnouncementDTO dto) {
        logger.info("编辑公告: announcementId={}, operatorId={}", announcementId, operatorId);

        GroupAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException("公告不存在"));

        // 检查编辑权限
        checkEditPermission(announcement, operatorId);

        // 已撤回的公告不能编辑
        if (announcement.isWithdrawn()) {
            throw new BusinessException("已撤回的公告不能编辑");
        }

        // 更新字段
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setAnnouncementType(
            GroupAnnouncement.AnnouncementType.valueOf(dto.getAnnouncementType())
        );
        announcement.setAllowComment(dto.getAllowComment());
        announcement.setEffectiveStart(dto.getEffectiveStart());
        announcement.setEffectiveEnd(dto.getEffectiveEnd());
        announcement.setSendNotification(dto.getSendNotification());

        // 处理附件
        if (dto.getAttachments() != null) {
            try {
                announcement.setAttachments(objectMapper.writeValueAsString(dto.getAttachments()));
            } catch (JsonProcessingException e) {
                logger.error("附件序列化失败", e);
            }
        }

        announcement = announcementRepository.save(announcement);

        // 发送更新通知
        sendAnnouncementUpdateNotification(announcement);

        logger.info("公告编辑成功: announcementId={}", announcementId);
        return convertToDTO(announcement, operatorId);
    }

    @Override
    @Transactional
    public void withdrawAnnouncement(Long announcementId, Long operatorId, String reason) {
        logger.info("撤回公告: announcementId={}, operatorId={}", announcementId, operatorId);

        GroupAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException("公告不存在"));

        // 检查权限
        checkWithdrawPermission(announcement, operatorId);

        // 已撤回的不能重复撤回
        if (announcement.isWithdrawn()) {
            throw new BusinessException("公告已被撤回");
        }

        // 执行撤回
        int result = announcementRepository.withdrawAnnouncement(
            announcementId, LocalDateTime.now(), operatorId, reason
        );

        if (result > 0) {
            // 发送撤回通知
            sendAnnouncementWithdrawNotification(announcement, reason);
            logger.info("公告撤回成功: announcementId={}", announcementId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponseDTO getAnnouncementDetail(Long announcementId, Long userId) {
        GroupAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException("公告不存在"));

        return convertToDTO(announcement, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponseDTO> getGroupAnnouncements(Long groupId, Long userId) {
        List<GroupAnnouncement> announcements = announcementRepository.findByGroupId(groupId);
        return announcements.stream()
                .map(a -> convertToDTO(a, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementResponseDTO> getGroupAnnouncementsPageable(Long groupId, Long userId, Pageable pageable) {
        Page<GroupAnnouncement> page = announcementRepository.findByGroupIdPageable(groupId, pageable);
        return page.map(a -> convertToDTO(a, userId));
    }

    @Override
    @Transactional
    public void markAsRead(Long announcementId, Long userId) {
        // 检查是否已读
        if (readRecordRepository.existsByAnnouncementIdAndUserId(announcementId, userId)) {
            return;
        }

        // 创建阅读记录
        AnnouncementReadRecord record = new AnnouncementReadRecord();
        record.setAnnouncementId(announcementId);
        record.setUserId(userId);
        record.setReadAt(LocalDateTime.now());
        readRecordRepository.save(record);

        // 更新阅读数
        announcementRepository.incrementReadCount(announcementId);
    }

    @Override
    @Transactional
    public void confirmAnnouncement(Long announcementId, Long userId) {
        // 检查公告是否需要确认
        GroupAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException("公告不存在"));

        if (announcement.getNeedConfirmCount() == null || announcement.getNeedConfirmCount() == 0) {
            throw new BusinessException("该公告不需要确认");
        }

        // 获取或创建阅读记录
        AnnouncementReadRecord record = readRecordRepository
                .findByAnnouncementIdAndUserId(announcementId, userId)
                .orElseGet(() -> {
                    AnnouncementReadRecord r = new AnnouncementReadRecord();
                    r.setAnnouncementId(announcementId);
                    r.setUserId(userId);
                    r.setReadAt(LocalDateTime.now());
                    return r;
                });

        // 检查是否已确认
        if (Boolean.TRUE.equals(record.getConfirmed())) {
            return;
        }

        // 标记为已确认
        record.setConfirmed(true);
        record.setConfirmedAt(LocalDateTime.now());
        readRecordRepository.save(record);

        // 更新确认数
        announcementRepository.incrementConfirmCount(announcementId);
    }

    @Override
    @Transactional
    public void markMultipleAsRead(List<Long> announcementIds, Long userId) {
        for (Long announcementId : announcementIds) {
            try {
                markAsRead(announcementId, userId);
            } catch (Exception e) {
                logger.warn("标记已读失败: announcementId={}", announcementId);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getReadUserIds(Long announcementId) {
        return readRecordRepository.findUserIdsByAnnouncementId(announcementId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getConfirmedUserIds(Long announcementId) {
        return readRecordRepository.findByAnnouncementId(announcementId).stream()
                .filter(r -> Boolean.TRUE.equals(r.getConfirmed()))
                .map(AnnouncementReadRecord::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long groupId, Long userId) {
        List<GroupAnnouncement> activeAnnouncements = announcementRepository.findActiveByGroupId(groupId);
        if (activeAnnouncements.isEmpty()) {
            return 0L;
        }

        List<Long> announcementIds = activeAnnouncements.stream()
                .map(GroupAnnouncement::getId)
                .collect(Collectors.toList());

        Set<Long> readIds = readRecordRepository.findReadAnnouncementIdsByUserId(userId, announcementIds);
        return (long) (announcementIds.size() - readIds.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponseDTO> getActiveAnnouncements(Long groupId, Long userId) {
        List<GroupAnnouncement> announcements = announcementRepository.findActiveByGroupId(groupId);
        return announcements.stream()
                .filter(GroupAnnouncement::isActive)
                .map(a -> convertToDTO(a, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponseDTO> getPinnedAnnouncements(Long groupId, Long userId) {
        List<GroupAnnouncement> announcements = announcementRepository.findPinnedByGroupId(groupId);
        return announcements.stream()
                .map(a -> convertToDTO(a, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRead(Long announcementId, Long userId) {
        return readRecordRepository.existsByAnnouncementIdAndUserId(announcementId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasConfirmed(Long announcementId, Long userId) {
        return readRecordRepository.findByAnnouncementIdAndUserId(announcementId, userId)
                .map(AnnouncementReadRecord::getConfirmed)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponseDTO> searchAnnouncements(Long groupId, String keyword, Long userId) {
        List<GroupAnnouncement> announcements = announcementRepository.searchByTitle(groupId, keyword);
        return announcements.stream()
                .map(a -> convertToDTO(a, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int cleanExpiredAnnouncements() {
        return announcementRepository.expireAnnouncements(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long announcementId, Long operatorId) {
        GroupAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException("公告不存在"));

        // 检查权限
        checkDeletePermission(announcement, operatorId);

        // 删除阅读记录
        readRecordRepository.deleteByAnnouncementId(announcementId);

        // 删除公告
        announcementRepository.delete(announcement);

        logger.info("公告删除成功: announcementId={}", announcementId);
    }

    // ========== 私有方法 ==========

    private void checkPublishPermission(Long groupId, Long userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException("不是群组成员"));

        if (member.getRole() == GroupMember.MemberRole.MEMBER) {
            throw new BusinessException("只有管理员或群主可以发布公告");
        }
    }

    private void checkEditPermission(GroupAnnouncement announcement, Long userId) {
        // 发布者或群主/管理员可以编辑
        if (!announcement.getPublisherId().equals(userId)) {
            checkPublishPermission(announcement.getGroupId(), userId);
        }
    }

    private void checkWithdrawPermission(GroupAnnouncement announcement, Long userId) {
        // 发布者或群主/管理员可以撤回
        if (!announcement.getPublisherId().equals(userId)) {
            checkPublishPermission(announcement.getGroupId(), userId);
        }
    }

    private void checkDeletePermission(GroupAnnouncement announcement, Long userId) {
        // 只有群主可以删除
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(announcement.getGroupId(), userId)
                .orElseThrow(() -> new BusinessException("不是群组成员"));

        if (member.getRole() != GroupMember.MemberRole.OWNER) {
            throw new BusinessException("只有群主可以删除公告");
        }
    }

    private AnnouncementResponseDTO convertToDTO(GroupAnnouncement announcement, Long userId) {
        AnnouncementResponseDTO dto = AnnouncementResponseDTO.fromEntity(announcement);

        // 设置发布者信息
        userService.findById(announcement.getPublisherId()).ifPresent(user -> {
            AnnouncementResponseDTO.UserInfoDTO publisherInfo = new AnnouncementResponseDTO.UserInfoDTO();
            publisherInfo.setId(user.getId());
            publisherInfo.setNickname(user.getNickname());
            publisherInfo.setAvatar(user.getAvatar());
            dto.setPublisher(publisherInfo);
        });

        // 设置撤回人信息
        if (announcement.getWithdrawnBy() != null) {
            userService.findById(announcement.getWithdrawnBy()).ifPresent(user -> {
                AnnouncementResponseDTO.UserInfoDTO withdrawnByInfo = new AnnouncementResponseDTO.UserInfoDTO();
                withdrawnByInfo.setId(user.getId());
                withdrawnByInfo.setNickname(user.getNickname());
                withdrawnByInfo.setAvatar(user.getAvatar());
                dto.setWithdrawnBy(withdrawnByInfo);
            });
        }

        // 解析附件
        if (announcement.getAttachments() != null) {
            try {
                List<AnnouncementResponseDTO.AttachmentDTO> attachments = objectMapper.readValue(
                    announcement.getAttachments(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, AnnouncementResponseDTO.AttachmentDTO.class)
                );
                dto.setAttachments(attachments);
            } catch (JsonProcessingException e) {
                logger.error("附件解析失败", e);
            }
        }

        // 设置已读/确认状态
        dto.setHasRead(hasRead(announcement.getId(), userId));
        dto.setHasConfirmed(hasConfirmed(announcement.getId(), userId));

        return dto;
    }

    private void sendAnnouncementNotification(GroupAnnouncement announcement, Set<Long> notifyMemberIds) {
        // WebSocket推送
        Map<String, Object> message = new HashMap<>();
        message.put("type", "NEW_ANNOUNCEMENT");
        message.put("announcementId", announcement.getId());
        message.put("groupId", announcement.getGroupId());
        message.put("title", announcement.getTitle());
        message.put("publisherId", announcement.getPublisherId());
        message.put("createdAt", announcement.getCreatedAt());

        if (notifyMemberIds != null && !notifyMemberIds.isEmpty()) {
            // 发送给指定成员
            for (Long memberId : notifyMemberIds) {
                webSocketService.sendToUser(memberId, "/queue/announcements", message);
            }
        } else {
            // 发送给群组所有成员
            webSocketService.sendToGroup(announcement.getGroupId(), "/topic/announcements", message);
        }
    }

    private void sendAnnouncementUpdateNotification(GroupAnnouncement announcement) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "ANNOUNCEMENT_UPDATED");
        message.put("announcementId", announcement.getId());
        message.put("groupId", announcement.getGroupId());

        webSocketService.sendToGroup(announcement.getGroupId(), "/topic/announcements", message);
    }

    private void sendAnnouncementWithdrawNotification(GroupAnnouncement announcement, String reason) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "ANNOUNCEMENT_WITHDRAWN");
        message.put("announcementId", announcement.getId());
        message.put("groupId", announcement.getGroupId());
        message.put("reason", reason);

        webSocketService.sendToGroup(announcement.getGroupId(), "/topic/announcements", message);
    }
}
