package com.im.server.announcement;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 群公告仓储层
 * 使用内存存储，生产环境应替换为MySQL
 */
public class AnnouncementRepository {
    
    private final Map<String, Announcement> announcements = new ConcurrentHashMap<>();
    private final Map<String, List<String>> groupAnnouncementIndex = new ConcurrentHashMap<>();
    
    public Announcement save(Announcement announcement) {
        announcements.put(announcement.getAnnouncementId(), announcement);
        groupAnnouncementIndex
            .computeIfAbsent(announcement.getGroupId(), k -> new ArrayList<>())
            .add(announcement.getAnnouncementId());
        return announcement;
    }
    
    public Optional<Announcement> findById(String id) {
        Announcement a = announcements.get(id);
        if (a != null && !a.isDeleted()) {
            return Optional.of(a);
        }
        return Optional.empty();
    }
    
    public List<Announcement> findByGroupId(String groupId) {
        List<String> ids = groupAnnouncementIndex.getOrDefault(groupId, Collections.emptyList());
        return ids.stream()
            .map(announcements::get)
            .filter(a -> a != null && !a.isDeleted())
            .sorted(Comparator
                .comparing(Announcement::isPinned).reversed()
                .thenComparing(Announcement::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
    
    public List<Announcement> findPinnedByGroupId(String groupId) {
        return findByGroupId(groupId).stream()
            .filter(Announcement::isPinned)
            .sorted(Comparator.comparing(Announcement::getPinnedAt).reversed())
            .collect(Collectors.toList());
    }
    
    public List<Announcement> findUnpinnedByGroupId(String groupId) {
        return findByGroupId(groupId).stream()
            .filter(a -> !a.isPinned())
            .sorted(Comparator.comparing(Announcement::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
    
    public Announcement update(Announcement announcement) {
        announcement.setUpdatedAt(LocalDateTime.now());
        announcements.put(announcement.getAnnouncementId(), announcement);
        return announcement;
    }
    
    public boolean delete(String id) {
        Announcement a = announcements.get(id);
        if (a != null) {
            a.softDelete();
            return true;
        }
        return false;
    }
    
    public List<Announcement> findHistoryByGroupId(String groupId, int page, int pageSize) {
        return findByGroupId(groupId).stream()
            .sorted(Comparator.comparing(Announcement::getCreatedAt).reversed())
            .skip((long) (page - 1) * pageSize)
            .limit(pageSize)
            .collect(Collectors.toList());
    }
    
    public int countByGroupId(String groupId) {
        return (int) findByGroupId(groupId).stream().count();
    }
    
    public List<Announcement> findByAuthorId(String authorId) {
        return announcements.values().stream()
            .filter(a -> authorId.equals(a.getAuthorId()) && !a.isDeleted())
            .sorted(Comparator.comparing(Announcement::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
    
    public boolean existsByIdAndGroupId(String id, String groupId) {
        return findById(id)
            .map(a -> groupId.equals(a.getGroupId()))
            .orElse(false);
    }
}
