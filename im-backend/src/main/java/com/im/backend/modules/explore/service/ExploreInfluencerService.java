package com.im.backend.modules.explore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.explore.entity.ExploreInfluencer;

import java.util.List;

/**
 * 探店达人服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface ExploreInfluencerService extends IService<ExploreInfluencer> {

    /**
     * 申请成为探店达人
     */
    ExploreInfluencer applyForInfluencer(Long userId, ExploreInfluencer influencer);

    /**
     * 审核达人申请
     */
    boolean auditInfluencer(Long influencerId, Integer authStatus, String remark);

    /**
     * 获取达人详情
     */
    ExploreInfluencer getInfluencerDetail(Long influencerId);

    /**
     * 根据用户ID获取达人信息
     */
    ExploreInfluencer getInfluencerByUserId(Long userId);

    /**
     * 获取达人列表
     */
    IPage<ExploreInfluencer> getInfluencerList(Integer pageNum, Integer pageSize, Integer level);

    /**
     * 获取推荐的达人列表
     */
    IPage<ExploreInfluencer> getRecommendedInfluencers(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取热门达人榜单
     */
    List<ExploreInfluencer> getHotInfluencers(Integer limit);

    /**
     * 关注达人
     */
    boolean followInfluencer(Long influencerId, Long userId);

    /**
     * 取消关注达人
     */
    boolean unfollowInfluencer(Long influencerId, Long userId);

    /**
     * 检查用户是否已关注达人
     */
    boolean hasFollowed(Long influencerId, Long userId);

    /**
     * 获取用户的关注列表
     */
    IPage<ExploreInfluencer> getUserFollowings(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 更新达人信息
     */
    ExploreInfluencer updateInfluencer(ExploreInfluencer influencer);

    /**
     * 计算并更新达人影响力分数
     */
    void calculateAndUpdateInfluenceScore(Long influencerId);

    /**
     * 升级达人等级
     */
    boolean upgradeLevel(Long influencerId, Integer newLevel);

    /**
     * 获取达人统计数据
     */
    InfluencerStats getInfluencerStats(Long influencerId);

    /**
     * 达人统计DTO
     */
    class InfluencerStats {
        private Integer totalNotes;
        private Integer totalViews;
        private Integer totalLikes;
        private Integer totalComments;
        private Integer totalShares;
        private Integer newFollowersThisMonth;
        private Long totalEarnings;
        private Double avgRating;

        // Getters and Setters
        public Integer getTotalNotes() { return totalNotes; }
        public void setTotalNotes(Integer totalNotes) { this.totalNotes = totalNotes; }
        
        public Integer getTotalViews() { return totalViews; }
        public void setTotalViews(Integer totalViews) { this.totalViews = totalViews; }
        
        public Integer getTotalLikes() { return totalLikes; }
        public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }
        
        public Integer getTotalComments() { return totalComments; }
        public void setTotalComments(Integer totalComments) { this.totalComments = totalComments; }
        
        public Integer getTotalShares() { return totalShares; }
        public void setTotalShares(Integer totalShares) { this.totalShares = totalShares; }
        
        public Integer getNewFollowersThisMonth() { return newFollowersThisMonth; }
        public void setNewFollowersThisMonth(Integer newFollowersThisMonth) { this.newFollowersThisMonth = newFollowersThisMonth; }
        
        public Long getTotalEarnings() { return totalEarnings; }
        public void setTotalEarnings(Long totalEarnings) { this.totalEarnings = totalEarnings; }
        
        public Double getAvgRating() { return avgRating; }
        public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
    }
}
