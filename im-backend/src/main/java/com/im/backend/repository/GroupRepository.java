package com.im.backend.repository;

import com.im.backend.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 群组数据访问接口
 * 对应功能 #15 - 群聊功能
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    /**
     * 根据群主ID查询群组列表
     */
    List<Group> findByOwnerId(Long ownerId);
    
    /**
     * 根据群组名称模糊查询
     */
    @Query("SELECT g FROM Group g WHERE g.name LIKE %:keyword%")
    List<Group> searchByName(@Param("keyword") String keyword);
    
    /**
     * 查询用户加入的所有群组
     */
    @Query("SELECT g FROM Group g JOIN GroupMember gm ON g.id = gm.groupId WHERE gm.userId = :userId")
    List<Group> findGroupsByMemberId(@Param("userId") Long userId);
}
