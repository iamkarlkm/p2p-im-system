package com.im.server.repository;

import com.im.server.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群组Repository
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    /**
     * 根据群组ID查询群组
     */
    Group findByGroupId(String groupId);
    
    /**
     * 查询用户创建的群组
     */
    List<Group> findByOwnerId(Long ownerId);
    
    /**
     * 根据群组名称模糊查询
     */
    List<Group> findByGroupNameContaining(String groupName);
}
