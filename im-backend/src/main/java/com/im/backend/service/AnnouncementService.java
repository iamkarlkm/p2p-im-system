package com.im.backend.service;

import com.im.backend.dto.AnnouncementCreateRequest;
import com.im.backend.dto.AnnouncementDTO;
import com.im.backend.entity.AnnouncementEntity;
import com.im.backend.entity.AnnouncementReadRecordEntity;
import com.im.backend.repository.AnnouncementRepository;
import com.im.backend.repository.AnnouncementReadRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementReadRecordRepository readRecordRepository;
    private final ObjectMapper objectMapper;
    private final WebSocketService webSocketService;

    /** 发布公告 */
    @Transactional
    public AnnouncementDTO publish(AnnouncementCreateRequest request) {
        AnnouncementEntity entity = AnnouncementEntity.builder()
            .groupId(request.getGroupId())
            .authorId(request.getAuthorId())
            .title(request.getTitle())
            .content(request.getContent())
            .pinned(request.getPinned() != null ? request.getPinned() : false)
            .requiredRead(request.getRequiredRead() != null ? request.getRequiredRead() : false)
            .urgent(request.getUrgent() != null ? request.getUrgent() : false)
            .type(request.getType() != null ? request.getType() : "normal")
            .expireTime(request.getExpireTime())
            .deleted(false)
            .build();

        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            try {
                entity.setAttachments(objectMapper.writeValueAsString(request.getAttachments()));
            } catch (Exception ignored) {}
        }

        entity = announcementRepository.save(entity);

        // 推送 WebSocket 通知给群成员
        pushAnnouncementNotification(entity);

        return AnnouncementDTO.fromEntity(entity);
    }

    /** 获取群公告列表 */
    public List<AnnouncementDTO> getGroupAnnouncements(Long groupId, Long currentUserId) {
        List<AnnouncementEntity> entities = announcementRepository.findActiveByGroupId(groupId, LocalDateTime.now());
        List<Long> announcementIds = entities.stream().map(AnnouncementEntity::getId).collect(Collectors.toList());

        // 获取当前用户已读记录
        Set<Long> readIds = new HashSet<>();
        if (currentUserId != null && !announcementIds.isEmpty()) {
            readIds.addAll(readRecordRepository.findReadIdsByUserIdAndAnnouncementIds(currentUserId, announcementIds));
        }

        return entities.stream().map(e -> {
            Boolean isRead = readIds.contains(e.getId());
            return AnnouncementDTO.fromEntity(e, isRead, isRead, null, null);
        }).collect(Collectors.toList());
    }

    /** 分页获取群公告 */
    public Page<AnnouncementDTO> getGroupAnnouncementsPaged(Long groupId, int page, int size, Long currentUserId) {
        Page<AnnouncementEntity> pageResult = announcementRepository.findPageByGroupId(groupId, LocalDateTime.now(), PageRequest.of(page, size));
        List<Long> ids = pageResult.getContent().stream().map(AnnouncementEntity::getId).collect(Collectors.toList());

        Set<Long> readIds = new HashSet<>();
        if (currentUserId != null && !ids.isEmpty()) {
            readIds.addAll(readRecordRepository.findReadIdsByUserIdAndAnnouncementIds(currentUserId, ids));
        }

        return pageResult.map(e -> AnnouncementDTO.fromEntity(e, readIds.contains(e.getId()), null, null, null));
    }

    /** 标记已读 */
    @Transactional
    public void markAsRead(Long announcementId, Long userId, String deviceType) {
        Optional<AnnouncementReadRecordEntity> existing = readRecordRepository.findByAnnouncementIdAndUserId(announcementId, userId);
        if (existing.isPresent()) return;

        AnnouncementReadRecordEntity record = AnnouncementReadRecordEntity.builder()
            .announcementId(announcementId)
            .userId(userId)
            .deviceType(deviceType)
            .readTime(LocalDateTime.now())
            .confirmed(false)
            .build();
        readRecordRepository.save(record);

        // 通知发布者
        Optional<AnnouncementEntity> ann = announcementRepository.findById(announcementId);
        if (ann.isPresent()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "announcement_read");
            payload.put("announcementId", announcementId);
            payload.put("userId", userId);
            payload.put("groupId", ann.get().getGroupId());
            webSocketService.sendToUser(ann.get().getAuthorId(), payload);
        }
    }

    /** 确认紧急公告 */
    @Transactional
    public void confirmAnnouncement(Long announcementId, Long userId) {
        AnnouncementReadRecordEntity record = readRecordRepository.findByAnnouncementIdAndUserId(announcementId, userId)
            .orElseGet(() -> AnnouncementReadRecordEntity.builder()
                .announcementId(announcementId).userId(userId).readTime(LocalDateTime.now()).confirmed(false).build());
        record.setConfirmed(true);
        readRecordRepository.save(record);
    }

    /** 获取公告已读统计 */
    public Map<String, Object> getReadStats(Long announcementId, Long groupMemberCount) {
        long readCount = readRecordRepository.countByAnnouncementIdAndConfirmedTrue(announcementId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("readCount", (int) readCount);
        stats.put("totalMemberCount", (int) groupMemberCount);
        stats.put("unreadCount", (int) groupMemberCount - (int) readCount);
        stats.put("readRate", groupMemberCount > 0 ? (double) readCount / groupMemberCount : 0.0);
        return stats;
    }

    /** 撤销公告 */
    @Transactional
    public void revokeAnnouncement(Long announcementId, Long userId) {
        announcementRepository.findById(announcementId).ifPresent(e -> {
            if (!e.getAuthorId().equals(userId)) return;
            e.setDeleted(true);
            e.setDeletedTime(LocalDateTime.now());
            announcementRepository.save(e);
        });
    }

    /** 定时清理过期公告 */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupExpiredAnnouncements() {
        int deleted = announcementRepository.deleteExpiredAnnouncements(LocalDateTime.now());
        if (deleted > 0) log.info("清理过期公告: {} 条", deleted);
    }

    private void pushAnnouncementNotification(AnnouncementEntity entity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "new_announcement");
        payload.put("announcementId", entity.getId());
        payload.put("groupId", entity.getGroupId());
        payload.put("title", entity.getTitle());
        payload.put("urgent", entity.getUrgent());
        payload.put("publishTime", entity.getPublishTime().toString());
        webSocketService.sendToGroup(entity.getGroupId(), payload);
    }
}
