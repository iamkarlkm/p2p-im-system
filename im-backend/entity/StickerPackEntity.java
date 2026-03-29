package com.im.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "sticker_packs")
public class StickerPackEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String packId;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    private String author;
    
    @Column
    private String publisher;
    
    @Column(nullable = false)
    private String coverUrl;
    
    @Column(nullable = false)
    private String coverThumbnailUrl;
    
    @Column(nullable = false)
    private Integer totalStickers;
    
    @Column(nullable = false)
    private Long totalDownloads;
    
    @Column(nullable = false)
    private Long totalLikes;
    
    @Column(nullable = false)
    private Float averageRating;
    
    @Column(nullable = false)
    private Boolean isOfficial;
    
    @Column(nullable = false)
    private Boolean isFeatured;
    
    @Column(nullable = false)
    private Boolean isFree;
    
    @Column
    private Double price;
    
    @Column
    private String currency;
    
    @Column(nullable = false)
    private String category;
    
    @ElementCollection
    @CollectionTable(name = "sticker_pack_tags", joinColumns = @JoinColumn(name = "pack_id"))
    @Column(name = "tag")
    private Set<String> tags;
    
    @Column(nullable = false)
    private String licenseType;
    
    @Column(nullable = false)
    private String licenseUrl;
    
    @Column(nullable = false)
    private String fileFormat;
    
    @Column(nullable = false)
    private Long totalSizeBytes;
    
    @Column(nullable = false)
    private String compatibleAppVersion;
    
    @Column(nullable = false)
    private Integer minAppVersion;
    
    @Column(nullable = false)
    private Integer maxAppVersion;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private LocalDateTime publishedAt;
    
    @Column
    private LocalDateTime featuredAt;
    
    @Column(nullable = false)
    private Boolean isActive;
    
    @Column(nullable = false)
    private Boolean isDeleted;
    
    @Column
    private String deletionReason;
    
    @Column
    private LocalDateTime deletedAt;
    
    @Column(nullable = false)
    private String createdByUserId;
    
    @Column
    private String approvedByUserId;
    
    @Column(nullable = false)
    private String approvalStatus;
    
    @Column
    private String rejectionReason;
    
    @Column(nullable = false)
    private Long version;
    
    @Column(nullable = false)
    private String etag;
    
    @OneToMany(mappedBy = "stickerPack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StickerEntity> stickers;
    
    @OneToMany(mappedBy = "stickerPack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StickerPackDownloadEntity> downloads;
    
    @OneToMany(mappedBy = "stickerPack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StickerPackRatingEntity> ratings;
    
    @OneToMany(mappedBy = "stickerPack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StickerPackFavoriteEntity> favorites;
    
    @OneToMany(mappedBy = "stickerPack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StickerPackReportEntity> reports;
    
    @OneToMany(mappedBy = "stickerPack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StickerPackCommentEntity> comments;
    
    @OneToMany(mappedBy = "stickerPack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StickerPackAnalyticsEntity> analytics;
    
    @OneToMany(mappedBy = "stickerPack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StickerPackTransactionEntity> transactions;
    
    // Constructors
    public StickerPackEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.isDeleted = false;
        this.totalDownloads = 0L;
        this.totalLikes = 0L;
        this.averageRating = 0.0f;
        this.version = 1L;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public StickerPackEntity(String packId, String name, String author, String coverUrl, 
                            String category, String createdByUserId) {
        this();
        this.packId = packId;
        this.name = name;
        this.author = author;
        this.coverUrl = coverUrl;
        this.coverThumbnailUrl = coverUrl;
        this.category = category;
        this.createdByUserId = createdByUserId;
        this.approvalStatus = "PENDING";
    }
    
    // Getters and Setters (abbreviated for brevity)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPackId() { return packId; }
    public void setPackId(String packId) { this.packId = packId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    
    public String getCoverThumbnailUrl() { return coverThumbnailUrl; }
    public void setCoverThumbnailUrl(String coverThumbnailUrl) { this.coverThumbnailUrl = coverThumbnailUrl; }
    
    public Integer getTotalStickers() { return totalStickers; }
    public void setTotalStickers(Integer totalStickers) { this.totalStickers = totalStickers; }
    
    public Long getTotalDownloads() { return totalDownloads; }
    public void setTotalDownloads(Long totalDownloads) { this.totalDownloads = totalDownloads; }
    
    public Long getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Long totalLikes) { this.totalLikes = totalLikes; }
    
    public Float getAverageRating() { return averageRating; }
    public void setAverageRating(Float averageRating) { this.averageRating = averageRating; }
    
    public Boolean getIsOfficial() { return isOfficial; }
    public void setIsOfficial(Boolean isOfficial) { this.isOfficial = isOfficial; }
    
    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
    
    public Boolean getIsFree() { return isFree; }
    public void setIsFree(Boolean isFree) { this.isFree = isFree; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    
    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }
    
    public String getLicenseUrl() { return licenseUrl; }
    public void setLicenseUrl(String licenseUrl) { this.licenseUrl = licenseUrl; }
    
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
    
    public Long getTotalSizeBytes() { return totalSizeBytes; }
    public void setTotalSizeBytes(Long totalSizeBytes) { this.totalSizeBytes = totalSizeBytes; }
    
    public String getCompatibleAppVersion() { return compatibleAppVersion; }
    public void setCompatibleAppVersion(String compatibleAppVersion) { this.compatibleAppVersion = compatibleAppVersion; }
    
    public Integer getMinAppVersion() { return minAppVersion; }
    public void setMinAppVersion(Integer minAppVersion) { this.minAppVersion = minAppVersion; }
    
    public Integer getMaxAppVersion() { return maxAppVersion; }
    public void setMaxAppVersion(Integer maxAppVersion) { this.maxAppVersion = maxAppVersion; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public LocalDateTime getFeaturedAt() { return featuredAt; }
    public void setFeaturedAt(LocalDateTime featuredAt) { this.featuredAt = featuredAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    
    public String getDeletionReason() { return deletionReason; }
    public void setDeletionReason(String deletionReason) { this.deletionReason = deletionReason; }
    
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    
    public String getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(String createdByUserId) { this.createdByUserId = createdByUserId; }
    
    public String getApprovedByUserId() { return approvedByUserId; }
    public void setApprovedByUserId(String approvedByUserId) { this.approvedByUserId = approvedByUserId; }
    
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    
    public String getEtag() { return etag; }
    public void setEtag(String etag) { this.etag = etag; }
    
    public List<StickerEntity> getStickers() { return stickers; }
    public void setStickers(List<StickerEntity> stickers) { this.stickers = stickers; }
    
    public List<StickerPackDownloadEntity> getDownloads() { return downloads; }
    public void setDownloads(List<StickerPackDownloadEntity> downloads) { this.downloads = downloads; }
    
    public List<StickerPackRatingEntity> getRatings() { return ratings; }
    public void setRatings(List<StickerPackRatingEntity> ratings) { this.ratings = ratings; }
    
    public List<StickerPackFavoriteEntity> getFavorites() { return favorites; }
    public void setFavorites(List<StickerPackFavoriteEntity> favorites) { this.favorites = favorites; }
    
    public List<StickerPackReportEntity> getReports() { return reports; }
    public void setReports(List<StickerPackReportEntity> reports) { this.reports = reports; }
    
    public List<StickerPackCommentEntity> getComments() { return comments; }
    public void setComments(List<StickerPackCommentEntity> comments) { this.comments = comments; }
    
    public List<StickerPackAnalyticsEntity> getAnalytics() { return analytics; }
    public void setAnalytics(List<StickerPackAnalyticsEntity> analytics) { this.analytics = analytics; }
    
    public List<StickerPackTransactionEntity> getTransactions() { return transactions; }
    public void setTransactions(List<StickerPackTransactionEntity> transactions) { this.transactions = transactions; }
    
    // Helper methods
    public void incrementDownloads() {
        this.totalDownloads++;
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public void incrementLikes() {
        this.totalLikes++;
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public void updateAverageRating(Float newRating) {
        // This is a simplified calculation
        this.averageRating = newRating;
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public void markAsDeleted(String reason) {
        this.isDeleted = true;
        this.deletionReason = reason;
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public void approve(String approvedByUserId) {
        this.approvalStatus = "APPROVED";
        this.approvedByUserId = approvedByUserId;
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public void reject(String rejectionReason) {
        this.approvalStatus = "REJECTED";
        this.rejectionReason = rejectionReason;
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public void feature() {
        this.isFeatured = true;
        this.featuredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version++;
        this.etag = java.util.UUID.randomUUID().toString();
    }
    
    public void unfeature() {
        this.isFeatured = false;
        this.featuredAt = null;
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
}