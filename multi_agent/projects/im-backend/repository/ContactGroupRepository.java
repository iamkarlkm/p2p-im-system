package com.im.backend.repository;

import com.im.backend.entity.ContactGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 联系人好友分组仓储接口
 */
@Repository
public interface ContactGroupRepository extends JpaRepository<ContactGroupEntity, Long> {

    // 基础查询

    List<ContactGroupEntity> findByUserId(Long userId);

    List<ContactGroupEntity> findByUserIdOrderBySortIndexAsc(Long userId);

    Optional<ContactGroupEntity> findByUserIdAndId(Long userId, Long id);

    Optional<ContactGroupEntity> findByUserIdAndGroupName(Long userId, String groupName);

    boolean existsByUserIdAndGroupName(Long userId, String groupName);

    // 高级查询

    @Query("SELECT g FROM ContactGroupEntity g WHERE g.userId = :userId AND g.isDefault = true")
    List<ContactGroupEntity> findDefaultGroupsByUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM ContactGroupEntity g WHERE g.userId = :userId AND g.isDefault = false")
    List<ContactGroupEntity> findCustomGroupsByUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM ContactGroupEntity g WHERE g.userId = :userId AND g.hideIfEmpty = false")
    List<ContactGroupEntity> findVisibleGroupsByUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM ContactGroupEntity g WHERE g.userId = :userId AND g.contactCount > 0")
    List<ContactGroupEntity> findNonEmptyGroupsByUserId(@Param("userId") Long userId);

    @Query("SELECT MAX(g.sortIndex) FROM ContactGroupEntity g WHERE g.userId = :userId")
    Integer findMaxSortIndexByUserId(@Param("userId") Long userId);

    // 统计

    @Query("SELECT COUNT(g) FROM ContactGroupEntity g WHERE g.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(g.contactCount) FROM ContactGroupEntity g WHERE g.userId = :userId")
    Long sumContactCountByUserId(@Param("userId") Long userId);

    // 批量操作

    @Modifying
    @Query("UPDATE ContactGroupEntity g SET g.sortIndex = :newIndex WHERE g.id = :id AND g.userId = :userId")
    int updateSortIndex(@Param("id") Long id, @Param("userId") Long userId, @Param("newIndex") Integer newIndex);

    @Modifying
    @Query("UPDATE ContactGroupEntity g SET g.contactCount = g.contactCount + :delta WHERE g.id = :id AND g.userId = :userId")
    int updateContactCount(@Param("id") Long id, @Param("userId") Long userId, @Param("delta") Integer delta);

    @Modifying
    @Query("DELETE FROM ContactGroupEntity g WHERE g.userId = :userId AND g.isDefault = false")
    int deleteCustomGroupsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ContactGroupEntity g WHERE g.userId = :userId AND g.contactCount = 0 AND g.hideIfEmpty = true")
    int deleteEmptyHiddenGroupsByUserId(@Param("userId") Long userId);
}