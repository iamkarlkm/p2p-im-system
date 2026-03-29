package com.im.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stickers")
public class StickerEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String stickerId;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    private String imageUrl;
    
    @Column(nullable = false)
    private String thumbnailUrl;
    
    @Column(nullable = false)
    private String animatedUrl;
    
    @Column(nullable = false)
    private Integer width;
    
    @Column(nullable = false)
    private Integer height;
    
    @Column(nullable = false)
    private Long fileSizeBytes;
    
    @Column(nullable = false)
    private String fileFormat;
    
    @Column(nullable = false)
    private Boolean isAnimated;
    
    @Column(nullable = false)
    private Integer frameCount;
    
    @Column
    private Double durationSeconds;
    
    @Column(nullable = false)
    private Integer frameRate;
    
    @Column(nullable = false)
    private String mimeType;
    
    @Column(nullable = false)
    private String checksum;
    
    @Column(nullable = false)
    private String checksumAlgorithm;
    
    @Column(nullable = false)
    private Integer sortOrder;
    
    @Column(nullable = false)
    private Long totalUses;
    
    @Column(nullable = false)
    private Long totalFavorites;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean isActive;
    
    @Column(nullable = false)
    private Boolean isDeleted;
    
    @Column
    private LocalDateTime deletedAt;
    
    @Column(nullable = false)
    private Long version;
    
    @Column(nullable = false)
    private String etag;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id", nullable = false)
    private StickerPackEntity stickerPack;
    
    // Constructors
    public StickerEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.isDeleted = false;
        this.totalUses = 0L;
        this.totalFavorites = 0L;
        this.version = 1L;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public StickerEntity(String stickerId, String name, String imageUrl, 
                        String thumbnailUrl, String animatedUrl, Integer width, 
                        Integer height, String fileFormat, Boolean isAnimated,
                        StickerPackEntity stickerPack, Integer sortOrder) {
        this();
        this.stickerId = stickerId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.animatedUrl = animatedUrl;
        this.width = width;
        this.height = height;
        this.fileFormat = fileFormat;
        this.isAnimated = isAnimated;
        this.stickerPack = stickerPack;
        this.sortOrder = sortOrder;
        this.mimeType = isAnimated ? "image/gif" : "image/png";
        this.checksum = "";
        this.checksumAlgorithm = "SHA-256";
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStickerId() { return stickerId; }
    public void setStickerId(String stickerId) { this.stickerId = stickerId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    public String getAnimatedUrl() { return animatedUrl; }
    public void setAnimatedUrl(String animatedUrl) { this.animatedUrl = animatedUrl; }
    
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    
    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
    
    public Boolean getIsAnimated() { return isAnimated; }
    public void setIsAnimated(Boolean isAnimated) { this.isAnimated = isAnimated; }
    
    public Integer getFrameCount() { return frameCount; }
    public void setFrameCount(Integer frameCount) { this.frameCount = frameCount; }
    
    public Double getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Double durationSeconds) { this.durationSeconds = durationSeconds; }
    
    public Integer getFrameRate() { return frameRate; }
    public void setFrameRate(Integer frameRate) { this.frameRate = frameRate; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public String getChecksumAlgorithm() { return checksumAlgorithm; }
    public void setChecksumAlgorithm(String checksumAlgorithm) { this.checksumAlgorithm = checksumAlgorithm; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public Long getTotalUses() { return totalUses; }
    public void setTotalUses(Long totalUses) { this.totalUses = totalUses; }
    
    public Long getTotalFavorites() { return totalFavorites; }
    public void setTotalFavorites(Long totalFavorites) { this.totalFavorites = totalFavorites; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    
    public String getEtag() { return etag; }
    public void setEtag(String etag) { this.etag = etag; }
    
    public StickerPackEntity getStickerPack() { return stickerPack; }
    public void setStickerPack(StickerPackEntity stickerPack) { this.stickerPack = stickerPack; }
    
    // Helper methods
    public void incrementUses() {
        this.totalUses++;
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public void incrementFavorites() {
        this.totalFavorites++;
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public void markAsDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public void updateChecksum(String checksum, String algorithm) {
        this.checksum = checksum;
        this.checksumAlgorithm = algorithm;
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    // Utility methods
    public String getDisplayName() {
        return this.name != null ? this.name : "Sticker " + this.stickerId;
    }
    
    public String getFileExtension() {
        if (this.fileFormat == null) return "";
        switch (this.fileFormat.toLowerCase()) {
            case "png": return ".png";
            case "jpg": case "jpeg": return ".jpg";
            case "gif": return ".gif";
            case "webp": return ".webp";
            case "apng": return ".apng";
            default: return "";
        }
    }
    
    public boolean isValidForUse() {
        return this.isActive && !this.isDeleted && 
               this.stickerPack != null && 
               this.stickerPack.getIsActive() && 
               !this.stickerPack.getIsDeleted();
    }
    
    public String getBestUrl(boolean preferAnimated) {
        if (preferAnimated && this.isAnimated && this.animatedUrl != null && !this.animatedUrl.isEmpty()) {
            return this.animatedUrl;
        }
        return this.imageUrl;
    }
}