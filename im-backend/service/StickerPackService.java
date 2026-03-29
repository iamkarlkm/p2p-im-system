package com.im.backend.service;

import com.im.backend.entity.StickerPackEntity;
import com.im.backend.entity.StickerEntity;
import com.im.backend.repository.StickerPackRepository;
import com.im.backend.repository.StickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StickerPackService {
    
    @Autowired
    private StickerPackRepository stickerPackRepository;
    
    @Autowired
    private StickerRepository stickerRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    // Basic CRUD operations
    
    @Transactional
    public StickerPackEntity createStickerPack(StickerPackEntity stickerPack) {
        // Generate unique pack ID if not provided
        if (stickerPack.getPackId() == null || stickerPack.getPackId().isEmpty()) {
            stickerPack.setPackId(generatePackId(stickerPack.getName()));
        }
        
        // Validate pack ID uniqueness
        if (stickerPackRepository.existsByPackId(stickerPack.getPackId())) {
            throw new IllegalArgumentException("Sticker pack ID already exists: " + stickerPack.getPackId());
        }
        
        // Set default values
        stickerPack.setCreatedAt(LocalDateTime.now());
        stickerPack.setUpdatedAt(LocalDateTime.now());
        stickerPack.setIsActive(true);
        stickerPack.setIsDeleted(false);
        stickerPack.setTotalDownloads(0L);
        stickerPack.setTotalLikes(0L);
        stickerPack.setAverageRating(0.0f);
        stickerPack.setVersion(1L);
        stickerPack.setEtag(UUID.randomUUID().toString());
        
        // Set approval status
        if (stickerPack.getApprovalStatus() == null) {
            stickerPack.setApprovalStatus("PENDING");
        }
        
        // Save the sticker pack
        StickerPackEntity savedPack = stickerPackRepository.save(stickerPack);
        
        // Log analytics event
        analyticsService.logEvent("sticker_pack_created", 
            Map.of("pack_id", savedPack.getPackId(), 
                   "author", savedPack.getAuthor(),
                   "category", savedPack.getCategory()));
        
        return savedPack;
    }
    
    @Transactional(readOnly = true)
    public Optional<StickerPackEntity> getStickerPackById(Long id) {
        return stickerPackRepository.findByIdAndIsDeletedFalse(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<StickerPackEntity> getStickerPackByPackId(String packId) {
        return stickerPackRepository.findByPackIdAndIsDeletedFalse(packId);
    }
    
    @Transactional
    public StickerPackEntity updateStickerPack(Long id, StickerPackEntity updates) {
        StickerPackEntity existingPack = stickerPackRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + id));
        
        // Update fields if provided
        if (updates.getName() != null) {
            existingPack.setName(updates.getName());
        }
        if (updates.getDescription() != null) {
            existingPack.setDescription(updates.getDescription());
        }
        if (updates.getAuthor() != null) {
            existingPack.setAuthor(updates.getAuthor());
        }
        if (updates.getPublisher() != null) {
            existingPack.setPublisher(updates.getPublisher());
        }
        if (updates.getCoverUrl() != null) {
            existingPack.setCoverUrl(updates.getCoverUrl());
        }
        if (updates.getCoverThumbnailUrl() != null) {
            existingPack.setCoverThumbnailUrl(updates.getCoverThumbnailUrl());
        }
        if (updates.getCategory() != null) {
            existingPack.setCategory(updates.getCategory());
        }
        if (updates.getTags() != null) {
            existingPack.setTags(updates.getTags());
        }
        if (updates.getLicenseType() != null) {
            existingPack.setLicenseType(updates.getLicenseType());
        }
        if (updates.getLicenseUrl() != null) {
            existingPack.setLicenseUrl(updates.getLicenseUrl());
        }
        if (updates.getIsOfficial() != null) {
            existingPack.setIsOfficial(updates.getIsOfficial());
        }
        if (updates.getIsFree() != null) {
            existingPack.setIsFree(updates.getIsFree());
        }
        if (updates.getPrice() != null) {
            existingPack.setPrice(updates.getPrice());
        }
        if (updates.getCurrency() != null) {
            existingPack.setCurrency(updates.getCurrency());
        }
        if (updates.getIsActive() != null) {
            existingPack.setIsActive(updates.getIsActive());
        }
        
        // Update version and ETag
        existingPack.setVersion(existingPack.getVersion() + 1);
        existingPack.setEtag(UUID.randomUUID().toString());
        existingPack.setUpdatedAt(LocalDateTime.now());
        
        StickerPackEntity updatedPack = stickerPackRepository.save(existingPack);
        
        // Log analytics event
        analyticsService.logEvent("sticker_pack_updated", 
            Map.of("pack_id", updatedPack.getPackId()));
        
        return updatedPack;
    }
    
    @Transactional
    public void deleteStickerPack(Long id, String reason) {
        StickerPackEntity existingPack = stickerPackRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + id));
        
        existingPack.markAsDeleted(reason);
        stickerPackRepository.save(existingPack);
        
        // Also mark all stickers in this pack as deleted
        List<StickerEntity> stickers = stickerRepository.findByStickerPackIdAndIsDeletedFalse(id);
        for (StickerEntity sticker : stickers) {
            sticker.markAsDeleted();
            stickerRepository.save(sticker);
        }
        
        // Log analytics event
        analyticsService.logEvent("sticker_pack_deleted", 
            Map.of("pack_id", existingPack.getPackId(), 
                   "reason", reason));
        
        // Notify subscribers
        notificationService.sendNotification("sticker_pack_deleted", 
            Map.of("pack_id", existingPack.getPackId(),
                   "pack_name", existingPack.getName()));
    }
    
    // Search and discovery operations
    
    @Transactional(readOnly = true)
    public Page<StickerPackEntity> searchStickerPacks(String query, String category, 
                                                     Boolean isOfficial, Boolean isFeatured, 
                                                     Boolean isFree, Float minRating, 
                                                     Long minDownloads, Pageable pageable) {
        return stickerPackRepository.searchStickerPacks(
            query, null, category, isOfficial, isFeatured, isFree, 
            minRating, minDownloads, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<StickerPackEntity> searchStickerPacksByName(String name) {
        return stickerPackRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);
    }
    
    @Transactional(readOnly = true)
    public List<StickerPackEntity> searchStickerPacksByAuthor(String author) {
        return stickerPackRepository.findByAuthorContainingIgnoreCaseAndIsDeletedFalse(author);
    }
    
    @Transactional(readOnly = true)
    public List<StickerPackEntity> searchStickerPacksByTag(String tag) {
        return stickerPackRepository.findByTagAndIsDeletedFalse(tag);
    }
    
    @Transactional(readOnly = true)
    public List<StickerPackEntity> searchStickerPacksByTags(Set<String> tags) {
        return stickerPackRepository.findByTagsInAndIsDeletedFalse(tags);
    }
    
    @Transactional(readOnly = true)
    public Page<StickerPackEntity> getFeaturedStickerPacks(Pageable pageable) {
        return stickerPackRepository.findByIsFeaturedTrueAndIsDeletedFalse(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<StickerPackEntity> getOfficialStickerPacks(Pageable pageable) {
        return stickerPackRepository.findByIsOfficialTrueAndIsDeletedFalse(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<StickerPackEntity> getFreeStickerPacks(Pageable pageable) {
        return stickerPackRepository.findByIsFreeTrueAndIsDeletedFalse(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<StickerPackEntity> getNewestStickerPacks(Pageable pageable) {
        return stickerPackRepository.findNewest(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<StickerPackEntity> getTrendingStickerPacks(Pageable pageable) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(7);
        return stickerPackRepository.findTrending(sinceDate, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<StickerPackEntity> getTopStickerPacksByDownloads(Pageable pageable) {
        return stickerPackRepository.findTopByDownloads(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<StickerPackEntity> getTopStickerPacksByLikes(Pageable pageable) {
        return stickerPackRepository.findTopByLikes(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<StickerPackEntity> getTopStickerPacksByRating(Pageable pageable) {
        return stickerPackRepository.findTopByRating(pageable);
    }
    
    // Recommendation engine
    
    @Transactional(readOnly = true)
    public List<StickerPackEntity> getRecommendedStickerPacks(String userId, int limit) {
        // Get user's download history and preferences
        List<String> userCategories = getUserPreferredCategories(userId);
        List<Long> excludedIds = getUserDownloadedPackIds(userId);
        
        Pageable pageable = Pageable.ofSize(limit);
        Page<StickerPackEntity> recommendations = stickerPackRepository.findRecommendations(
            userCategories, excludedIds, pageable);
        
        return recommendations.getContent();
    }
    
    // Sticker management within packs
    
    @Transactional
    public StickerEntity addStickerToPack(Long packId, StickerEntity sticker) {
        StickerPackEntity pack = stickerPackRepository.findByIdAndIsDeletedFalse(packId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + packId));
        
        // Generate unique sticker ID if not provided
        if (sticker.getStickerId() == null || sticker.getStickerId().isEmpty()) {
            sticker.setStickerId(generateStickerId(sticker.getName()));
        }
        
        // Validate sticker ID uniqueness within pack
        if (stickerRepository.findByStickerPackAndStickerIdAndIsDeletedFalse(pack, sticker.getStickerId()).isPresent()) {
            throw new IllegalArgumentException("Sticker ID already exists in this pack: " + sticker.getStickerId());
        }
        
        // Associate with pack
        sticker.setStickerPack(pack);
        
        // Set default values
        sticker.setCreatedAt(LocalDateTime.now());
        sticker.setUpdatedAt(LocalDateTime.now());
        sticker.setIsActive(true);
        sticker.setIsDeleted(false);
        sticker.setTotalUses(0L);
        sticker.setTotalFavorites(0L);
        sticker.setVersion(1L);
        sticker.setEtag(UUID.randomUUID().toString());
        
        // Save the sticker
        StickerEntity savedSticker = stickerRepository.save(sticker);
        
        // Update pack's sticker count
        updatePackStickerCount(packId);
        
        // Log analytics event
        analyticsService.logEvent("sticker_added_to_pack", 
            Map.of("pack_id", pack.getPackId(), 
                   "sticker_id", savedSticker.getStickerId()));
        
        return savedSticker;
    }
    
    @Transactional(readOnly = true)
    public List<StickerEntity> getStickersInPack(Long packId) {
        StickerPackEntity pack = stickerPackRepository.findByIdAndIsDeletedFalse(packId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + packId));
        
        return stickerRepository.findByStickerPackAndIsDeletedFalseOrderBySortOrderAsc(pack);
    }
    
    @Transactional(readOnly = true)
    public List<StickerEntity> getStickersInPackByPackId(String packId) {
        StickerPackEntity pack = stickerPackRepository.findByPackIdAndIsDeletedFalse(packId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with packId: " + packId));
        
        return stickerRepository.findByStickerPackAndIsDeletedFalseOrderBySortOrderAsc(pack);
    }
    
    @Transactional
    public void removeStickerFromPack(Long packId, Long stickerId) {
        StickerEntity sticker = stickerRepository.findByIdAndIsDeletedFalse(stickerId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker not found with id: " + stickerId));
        
        if (!sticker.getStickerPack().getId().equals(packId)) {
            throw new IllegalArgumentException("Sticker does not belong to the specified pack");
        }
        
        sticker.markAsDeleted();
        stickerRepository.save(sticker);
        
        // Update pack's sticker count
        updatePackStickerCount(packId);
        
        // Log analytics event
        analyticsService.logEvent("sticker_removed_from_pack", 
            Map.of("pack_id", sticker.getStickerPack().getPackId(), 
                   "sticker_id", sticker.getStickerId()));
    }
    
    // Download and usage tracking
    
    @Transactional
    public void recordDownload(Long packId, String userId, String deviceId, String ipAddress) {
        StickerPackEntity pack = stickerPackRepository.findByIdAndIsDeletedFalse(packId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + packId));
        
        // Increment download count
        pack.incrementDownloads();
        stickerPackRepository.save(pack);
        
        // Record download analytics
        analyticsService.logEvent("sticker_pack_downloaded", 
            Map.of("pack_id", pack.getPackId(),
                   "user_id", userId,
                   "device_id", deviceId,
                   "ip_address", ipAddress));
        
        // Update user's download history
        updateUserDownloadHistory(userId, packId);
    }
    
    @Transactional
    public void recordStickerUse(Long stickerId, String userId, String conversationId, String messageId) {
        StickerEntity sticker = stickerRepository.findByIdAndIsDeletedFalse(stickerId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker not found with id: " + stickerId));
        
        // Increment usage count
        sticker.incrementUses();
        stickerRepository.save(sticker);
        
        // Also increment pack's usage indirectly
        StickerPackEntity pack = sticker.getStickerPack();
        pack.setUpdatedAt(LocalDateTime.now());
        stickerPackRepository.save(pack);
        
        // Log analytics event
        analyticsService.logEvent("sticker_used", 
            Map.of("sticker_id", sticker.getStickerId(),
                   "pack_id", pack.getPackId(),
                   "user_id", userId,
                   "conversation_id", conversationId,
                   "message_id", messageId));
    }
    
    // Rating and feedback
    
    @Transactional
    public void rateStickerPack(Long packId, String userId, Float rating, String comment) {
        StickerPackEntity pack = stickerPackRepository.findByIdAndIsDeletedFalse(packId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + packId));
        
        // TODO: Implement rating logic
        // This would typically involve creating a RatingEntity and updating average
        
        // Log analytics event
        analyticsService.logEvent("sticker_pack_rated", 
            Map.of("pack_id", pack.getPackId(),
                   "user_id", userId,
                   "rating", rating.toString()));
    }
    
    @Transactional
    public void favoriteSticker(Long stickerId, String userId) {
        StickerEntity sticker = stickerRepository.findByIdAndIsDeletedFalse(stickerId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker not found with id: " + stickerId));
        
        // Increment favorite count
        sticker.incrementFavorites();
        stickerRepository.save(sticker);
        
        // TODO: Record user favorite in a separate entity
        
        // Log analytics event
        analyticsService.logEvent("sticker_favorited", 
            Map.of("sticker_id", sticker.getStickerId(),
                   "user_id", userId));
    }
    
    // Admin operations
    
    @Transactional
    public void approveStickerPack(Long packId, String adminUserId) {
        StickerPackEntity pack = stickerPackRepository.findByIdAndIsDeletedFalse(packId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + packId));
        
        pack.approve(adminUserId);
        stickerPackRepository.save(pack);
        
        // Notify creator
        notificationService.sendNotification("sticker_pack_approved", 
            Map.of("pack_id", pack.getPackId(),
                   "pack_name", pack.getName(),
                   "admin_user_id", adminUserId));
        
        // Log analytics event
        analyticsService.logEvent("sticker_pack_approved", 
            Map.of("pack_id", pack.getPackId(),
                   "admin_user_id", adminUserId));
    }
    
    @Transactional
    public void rejectStickerPack(Long packId, String adminUserId, String reason) {
        StickerPackEntity pack = stickerPackRepository.findByIdAndIsDeletedFalse(packId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + packId));
        
        pack.reject(reason);
        stickerPackRepository.save(pack);
        
        // Notify creator
        notificationService.sendNotification("sticker_pack_rejected", 
            Map.of("pack_id", pack.getPackId(),
                   "pack_name", pack.getName(),
                   "admin_user_id", adminUserId,
                   "reason", reason));
        
        // Log analytics event
        analyticsService.logEvent("sticker_pack_rejected", 
            Map.of("pack_id", pack.getPackId(),
                   "admin_user_id", adminUserId,
                   "reason", reason));
    }
    
    @Transactional
    public void featureStickerPack(Long packId) {
        StickerPackEntity pack = stickerPackRepository.findByIdAndIsDeletedFalse(packId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + packId));
        
        pack.feature();
        stickerPackRepository.save(pack);
        
        // Log analytics event
        analyticsService.logEvent("sticker_pack_featured", 
            Map.of("pack_id", pack.getPackId()));
    }
    
    @Transactional
    public void unfeatureStickerPack(Long packId) {
        StickerPackEntity pack = stickerPackRepository.findByIdAndIsDeletedFalse(packId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + packId));
        
        pack.unfeature();
        stickerPackRepository.save(pack);
        
        // Log analytics event
        analyticsService.logEvent("sticker_pack_unfeatured", 
            Map.of("pack_id", pack.getPackId()));
    }
    
    // Statistics and analytics
    
    @Transactional(readOnly = true)
    public Map<String, Object> getStickerPackStatistics(Long packId) {
        StickerPackEntity pack = stickerPackRepository.findByIdAndIsDeletedFalse(packId)
            .orElseThrow(() -> new IllegalArgumentException("Sticker pack not found with id: " + packId));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("pack_id", pack.getPackId());
        stats.put("total_downloads", pack.getTotalDownloads());
        stats.put("total_likes", pack.getTotalLikes());
        stats.put("average_rating", pack.getAverageRating());
        stats.put("total_stickers", pack.getTotalStickers());
        stats.put("created_at", pack.getCreatedAt());
        stats.put("updated_at", pack.getUpdatedAt());
        
        // Get sticker usage statistics
        List<StickerEntity> stickers = stickerRepository.findByStickerPackIdAndIsDeletedFalse(packId);
        Long totalStickerUses = stickers.stream()
            .mapToLong(StickerEntity::getTotalUses)
            .sum();
        Long totalStickerFavorites = stickers.stream()
            .mapToLong(StickerEntity::getTotalFavorites)
            .sum();
        
        stats.put("total_sticker_uses", totalStickerUses);
        stats.put("total_sticker_favorites", totalStickerFavorites);
        
        // Get top stickers
        List<Map<String, Object>> topStickers = stickers.stream()
            .sorted((s1, s2) -> Long.compare(s2.getTotalUses(), s1.getTotalUses()))
            .limit(5)
            .map(s -> {
                Map<String, Object> stickerStats = new HashMap<>();
                stickerStats.put("sticker_id", s.getStickerId());
                stickerStats.put("name", s.getName());
                stickerStats.put("total_uses", s.getTotalUses());
                stickerStats.put("total_favorites", s.getTotalFavorites());
                return stickerStats;
            })
            .collect(Collectors.toList());
        
        stats.put("top_stickers", topStickers);
        
        return stats;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total_packs", stickerPackRepository.countActiveStickerPacks());
        stats.put("official_packs", stickerPackRepository.countOfficialStickerPacks());
        stats.put("featured_packs", stickerPackRepository.countFeaturedStickerPacks());
        stats.put("free_packs", stickerPackRepository.countFreeStickerPacks());
        stats.put("pending_packs", stickerPackRepository.countPendingStickerPacks());
        stats.put("total_downloads", stickerPackRepository.sumTotalDownloads());
        stats.put("total_likes", stickerPackRepository.sumTotalLikes());
        stats.put("average_rating", stickerPackRepository.getAverageRating());
        
        stats.put("total_stickers", stickerRepository.countActiveStickers());
        stats.put("animated_stickers", stickerRepository.countAnimatedStickers());
        stats.put("total_sticker_uses", stickerRepository.sumTotalUses());
        stats.put("total_sticker_favorites", stickerRepository.sumTotalFavorites());
        
        // Category distribution
        List<Object[]> categoryStats = stickerPackRepository.getCategoryStatistics();
        Map<String, Long> categoryDistribution = new HashMap<>();
        for (Object[] row : categoryStats) {
            categoryDistribution.put((String) row[0], (Long) row[1]);
        }
        stats.put("category_distribution", categoryDistribution);
        
        // File format distribution
        List<Object[]> formatStats = stickerRepository.getFileFormatStatistics();
        Map<String, Long> formatDistribution = new HashMap<>();
        for (Object[] row : formatStats) {
            formatDistribution.put((String) row[0], (Long) row[1]);
        }
        stats.put("format_distribution", formatDistribution);
        
        return stats;
    }
    
    // Helper methods
    
    private String generatePackId(String name) {
        String base = name.toLowerCase()
            .replaceAll("[^a-z0-9]", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
        
        String timestamp = String.valueOf(System.currentTimeMillis() % 1000000);
        String random = UUID.randomUUID().toString().substring(0, 4);
        
        return base + "-" + timestamp + "-" + random;
    }
    
    private String generateStickerId(String name) {
        String base = name.toLowerCase()
            .replaceAll("[^a-z0-9]", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
        
        String random = UUID.randomUUID().toString().substring(0, 8);
        
        return base + "-" + random;
    }
    
    private void updatePackStickerCount(Long packId) {
        StickerPackEntity pack = stickerPackRepository.findById(packId).orElse(null);
        if (pack != null) {
            Long stickerCount = stickerRepository.findByStickerPackIdAndIsDeletedFalse(packId).size();
            pack.setTotalStickers(stickerCount.intValue());
            pack.setUpdatedAt(LocalDateTime.now());
            stickerPackRepository.save(pack);
        }
    }
    
    private List<String> getUserPreferredCategories(String userId) {
        // TODO: Implement based on user's download history and preferences
        // For now, return some default categories
        return Arrays.asList("funny", "cute", "meme", "anime", "gaming");
    }
    
    private List<Long> getUserDownloadedPackIds(String userId) {
        // TODO: Implement based on user's download history
        // For now, return empty list
        return Collections.emptyList();
    }
    
    private void updateUserDownloadHistory(String userId, Long packId) {
        // TODO: Implement user download history tracking
    }
}