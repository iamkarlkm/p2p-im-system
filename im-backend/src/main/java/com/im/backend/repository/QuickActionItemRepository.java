package com.im.backend.repository;

import com.im.backend.model.QuickActionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 快捷操作项数据访问层
 */
@Repository
public interface QuickActionItemRepository extends JpaRepository<QuickActionItem, Long> {
    
    /**
     * 根据菜单ID查询所有操作项
     */
    List<QuickActionItem> findByMenuIdOrderBySortOrderAsc(Long menuId);
    
    /**
     * 根据菜单ID和可见性查询
     */
    List<QuickActionItem> findByMenuIdAndIsVisibleOrderBySortOrderAsc(Long menuId, Boolean isVisible);
    
    /**
     * 根据菜单ID删除所有操作项
     */
    void deleteByMenuId(Long menuId);
}
