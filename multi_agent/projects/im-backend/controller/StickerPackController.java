package com.im.backend.controller;

import com.im.backend.entity.StickerPackEntity;
import com.im.backend.entity.StickerEntity;
import com.im.backend.service.StickerPackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/v1/stickers")
public class StickerPackController {
    
    @Autowired
    private StickerPackService stickerPackService;
    
    // ==================== Sticker Pack CRUD ====================
    
    @PostMapping("/packs")
    public ResponseEntity<?> createStickerPack(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            StickerPackEntity pack = new StickerPackEntity();
            pack.setName((String) body.get("name"));
            pack.setDescription((String) body.get("description"));
            pack.setAuthor((String) body.get("author"));
            pack.setCategory((String) body.get("category"));
            pack.setCreatedByUserId(userDetails.getUsername());
            
            StickerPackEntity created = stickerPackService.createStickerPack(pack);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/{packId}")
    public ResponseEntity<?> getStickerPack(@PathVariable String packId) {
        try {
            return stickerPackService.getStickerPackByPackId(packId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/packs/{id}")
    public ResponseEntity<?> updateStickerPack(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            StickerPackEntity updates = new StickerPackEntity();
            if (body.containsKey("name")) updates.setName((String) body.get("name"));
            if (body.containsKey("description")) updates.setDescription((String) body.get("description"));
            if (body.containsKey("author")) updates.setAuthor((String) body.get("author"));
            if (body.containsKey("category")) updates.setCategory((String) body.get("category"));
            if (body.containsKey("tags")) updates.setTags(new HashSet<>((Collection<?>) body.get("tags")));
            
            StickerPackEntity updated = stickerPackService.updateStickerPack(id, updates);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/packs/{id}")
    public ResponseEntity<?> deleteStickerPack(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "User requested") String reason) {
        try {
            stickerPackService.deleteStickerPack(id, reason);
            return ResponseEntity.ok(Map.of("message", "Sticker pack deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== Sticker Pack Search ====================
    
    @GetMapping("/packs/search")
    public ResponseEntity<?> searchStickerPacks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isOfficial,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(required = false) Float minRating,
            @RequestParam(required = false) Long minDownloads,
            Pageable pageable) {
        try {
            Page<StickerPackEntity> results = stickerPackService.searchStickerPacks(
                query, category, isOfficial, isFeatured, isFree, 
                minRating, minDownloads, pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/featured")
    public ResponseEntity<?> getFeaturedStickerPacks(Pageable pageable) {
        try {
            Page<StickerPackEntity> results = stickerPackService.getFeaturedStickerPacks(pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/official")
    public ResponseEntity<?> getOfficialStickerPacks(Pageable pageable) {
        try {
            Page<StickerPackEntity> results = stickerPackService.getOfficialStickerPacks(pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/free")
    public ResponseEntity<?> getFreeStickerPacks(Pageable pageable) {
        try {
            Page<StickerPackEntity> results = stickerPackService.getFreeStickerPacks(pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/new")
    public ResponseEntity<?> getNewestStickerPacks(Pageable pageable) {
        try {
            Page<StickerPackEntity> results = stickerPackService.getNewestStickerPacks(pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/trending")
    public ResponseEntity<?> getTrendingStickerPacks(Pageable pageable) {
        try {
            Page<StickerPackEntity> results = stickerPackService.getTrendingStickerPacks(pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/top/downloads")
    public ResponseEntity<?> getTopByDownloads(Pageable pageable) {
        try {
            Page<StickerPackEntity> results = stickerPackService.getTopStickerPacksByDownloads(pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/top/likes")
    public ResponseEntity<?> getTopByLikes(Pageable pageable) {
        try {
            Page<StickerPackEntity> results = stickerPackService.getTopStickerPacksByLikes(pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/top/rating")
    public ResponseEntity<?> getTopByRating(Pageable pageable) {
        try {
            Page<StickerPackEntity> results = stickerPackService.getTopStickerPacksByRating(pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/recommended")
    public ResponseEntity<?> getRecommendedStickerPacks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<StickerPackEntity> results = stickerPackService.getRecommendedStickerPacks(
                userDetails.getUsername(), limit);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== Sticker Management ====================
    
    @PostMapping("/packs/{packId}/stickers")
    public ResponseEntity<?> addStickerToPack(
            @PathVariable Long packId,
            @RequestBody Map<String, Object> body) {
        try {
            StickerEntity sticker = new StickerEntity();
            sticker.setName((String) body.get("name"));
            sticker.setDescription((String) body.get("description"));
            sticker.setImageUrl((String) body.get("imageUrl"));
            sticker.setThumbnailUrl((String) body.get("thumbnailUrl"));
            sticker.setAnimatedUrl((String) body.get("animatedUrl"));
            sticker.setWidth((Integer) body.get("width"));
            sticker.setHeight((Integer) body.get("height"));
            sticker.setFileSizeBytes((Long) body.get("fileSizeBytes"));
            sticker.setFileFormat((String) body.get("fileFormat"));
            sticker.setIsAnimated((Boolean) body.get("isAnimated"));
            sticker.setSortOrder((Integer) body.get("sortOrder"));
            
            StickerEntity created = stickerPackService.addStickerToPack(packId, sticker);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/packs/{packId}/stickers")
    public ResponseEntity<?> getStickersInPack(@PathVariable Long packId) {
        try {
            List<StickerEntity> stickers = stickerPackService.getStickersInPack(packId);
            return ResponseEntity.ok(stickers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/packs/{packId}/stickers/{stickerId}")
    public ResponseEntity<?> removeStickerFromPack(
            @PathVariable Long packId,
            @PathVariable Long stickerId) {
        try {
            stickerPackService.removeStickerFromPack(packId, stickerId);
            return ResponseEntity.ok(Map.of("message", "Sticker removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== Usage Tracking ====================
    
    @PostMapping("/packs/{packId}/download")
    public ResponseEntity<?> recordDownload(
            @PathVariable Long packId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Device-ID", required = false) String deviceId,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress) {
        try {
            stickerPackService.recordDownload(
                packId, 
                userDetails.getUsername(), 
                deviceId != null ? deviceId : "unknown",
                ipAddress != null ? ipAddress : "unknown");
            return ResponseEntity.ok(Map.of("message", "Download recorded"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/stickers/{stickerId}/use")
    public ResponseEntity<?> recordStickerUse(
            @PathVariable Long stickerId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        try {
            stickerPackService.recordStickerUse(
                stickerId,
                userDetails.getUsername(),
                body.get("conversationId"),
                body.get("messageId"));
            return ResponseEntity.ok(Map.of("message", "Usage recorded"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/stickers/{stickerId}/favorite")
    public ResponseEntity<?> favoriteSticker(
            @PathVariable Long stickerId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            stickerPackService.favoriteSticker(stickerId, userDetails.getUsername());
            return ResponseEntity.ok(Map.of("message", "Sticker favorited"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== Rating and Feedback ====================
    
    @PostMapping("/packs/{packId}/rate")
    public ResponseEntity<?> rateStickerPack(
            @PathVariable Long packId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {
        try {
            Float rating = ((Number) body.get("rating")).floatValue();
            String comment = (String) body.get("comment");
            stickerPackService.rateStickerPack(packId, userDetails.getUsername(), rating, comment);
            return ResponseEntity.ok(Map.of("message", "Rating recorded"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== Admin Operations ====================
    
    @PostMapping("/admin/packs/{packId}/approve")
    public ResponseEntity<?> approveStickerPack(
            @PathVariable Long packId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            stickerPackService.approveStickerPack(packId, userDetails.getUsername());
            return ResponseEntity.ok(Map.of("message", "Sticker pack approved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/admin/packs/{packId}/reject")
    public ResponseEntity<?> rejectStickerPack(
            @PathVariable Long packId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        try {
            String reason = body.get("reason");
            stickerPackService.rejectStickerPack(packId, userDetails.getUsername(), reason);
            return ResponseEntity.ok(Map.of("message", "Sticker pack rejected"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/admin/packs/{packId}/feature")
    public ResponseEntity<?> featureStickerPack(@PathVariable Long packId) {
        try {
            stickerPackService.featureStickerPack(packId);
            return ResponseEntity.ok(Map.of("message", "Sticker pack featured"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/admin/packs/{packId}/unfeature")
    public ResponseEntity<?> unfeatureStickerPack(@PathVariable Long packId) {
        try {
            stickerPackService.unfeatureStickerPack(packId);
            return ResponseEntity.ok(Map.of("message", "Sticker pack unfeatured"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== Statistics ====================
    
    @GetMapping("/packs/{packId}/stats")
    public ResponseEntity<?> getStickerPackStatistics(@PathVariable Long packId) {
        try {
            Map<String, Object> stats = stickerPackService.getStickerPackStatistics(packId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getSystemStatistics() {
        try {
            Map<String, Object> stats = stickerPackService.getSystemStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== File Upload ====================
    
    @PostMapping("/packs/{packId}/upload")
    public ResponseEntity<?> uploadStickerPack(
            @PathVariable Long packId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // TODO: Implement file upload logic
            // - Validate file type (ZIP for sticker packs)
            // - Extract and validate contents
            // - Store files using FileStorageService
            // - Create sticker entities from extracted data
            
            return ResponseEntity.ok(Map.of(
                "message", "Upload endpoint ready",
                "filename", file.getOriginalFilename(),
                "size", file.getSize()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}