package com.im.repository;

import com.im.model.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户标签仓库
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Repository
public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    /**
     * 根据用户ID查询标签列表
     */
    List<UserTag> findByUserId(Long userId);

    /**
     * 根据标签名查询用户列表
     */
    List<UserTag> findByTagName(String tagName);

    /**
     * 根据用户ID和标签名查询
     */
    Optional<UserTag> findByUserIdAndTagName(Long userId, String tagName);

    /**
     * 检查用户是否有某个标签
     */
    boolean existsByUserIdAndTagName(Long userId, String tagName);

    /**
     * 删除用户的特定标签
     */
    void deleteByUserIdAndTagName(Long userId, String tagName);

    /**
     * 删除用户的所有标签
     */
    void deleteByUserId(Long userId);

    /**
     * 统计标签使用次数
     */
    @Query("SELECT t.tagName, COUNT(t) as count FROM UserTag t GROUP BY t.tagName ORDER BY count DESC")
    List<Object[]> countByTagName();

    /**
     * 获取热门标签
     */
    @Query(value = "SELECT t.tagName FROM UserTag t GROUP BY t.tagName ORDER BY COUNT(t) DESC LIMIT :limit", nativeQuery = true)
    List<String> findPopularTags(@Param("limit") int limit);

    /**
     * 根据分类查询标签
     */
    List<UserTag> findByUserIdAndCategory(Long userId, String category);

    /**
     * 统计用户的标签数量
     */
    long countByUserId(Long userId);

    /**
     * 搜索标签（模糊匹配）
     */
    @Query("SELECT t FROM UserTag t WHERE t.tagName LIKE %:keyword% AND t.isPublic = true")
    List<UserTag> searchPublicTags(@Param("keyword") String keyword);
}
