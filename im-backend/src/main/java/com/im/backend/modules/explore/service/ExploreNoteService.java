package com.im.backend.modules.explore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.explore.entity.ExploreNote;

import java.math.BigDecimal;
import java.util.List;

/**
 * 探店笔记服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface ExploreNoteService extends IService<ExploreNote> {

    /**
     * 发布探店笔记
     */
    ExploreNote publishNote(ExploreNote note);

    /**
     * 更新探店笔记
     */
    ExploreNote updateNote(ExploreNote note);

    /**
     * 获取笔记详情
     */
    ExploreNote getNoteDetail(Long noteId);

    /**
     * 分页查询笔记列表
     */
    IPage<ExploreNote> pageNotes(Integer pageNum, Integer pageSize, Integer status);

    /**
     * 获取用户的笔记列表
     */
    IPage<ExploreNote> getUserNotes(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取POI相关的笔记列表
     */
    IPage<ExploreNote> getPoiNotes(Long poiId, Integer pageNum, Integer pageSize);

    /**
     * 获取推荐笔记列表（基于推荐算法）
     */
    IPage<ExploreNote> getRecommendedNotes(Long userId, BigDecimal longitude, BigDecimal latitude, 
                                           Integer pageNum, Integer pageSize);

    /**
     * 获取附近的热门探店笔记
     */
    IPage<ExploreNote> getNearbyHotNotes(BigDecimal longitude, BigDecimal latitude, 
                                         Double radius, Integer pageNum, Integer pageSize);

    /**
     * 获取精选笔记列表
     */
    IPage<ExploreNote> getFeaturedNotes(Integer pageNum, Integer pageSize);

    /**
     * 点赞笔记
     */
    boolean likeNote(Long noteId, Long userId);

    /**
     * 取消点赞
     */
    boolean unlikeNote(Long noteId, Long userId);

    /**
     * 收藏笔记
     */
    boolean favoriteNote(Long noteId, Long userId);

    /**
     * 取消收藏
     */
    boolean unfavoriteNote(Long noteId, Long userId);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long noteId);

    /**
     * 审核笔记
     */
    boolean auditNote(Long noteId, Integer status, String rejectReason);

    /**
     * 设置精选笔记
     */
    boolean setFeatured(Long noteId, Boolean featured);

    /**
     * 搜索笔记
     */
    IPage<ExploreNote> searchNotes(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取用户的点赞笔记列表
     */
    IPage<ExploreNote> getUserLikedNotes(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户的收藏笔记列表
     */
    IPage<ExploreNote> getUserFavoritedNotes(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 删除笔记
     */
    boolean deleteNote(Long noteId, Long userId);
}
