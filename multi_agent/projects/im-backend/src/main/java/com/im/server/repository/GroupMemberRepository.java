package com.im.server.repository;

import com.im.server.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群成员Repository
 */
@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    /**
     * 查询群组成员列表
     */
    List<GroupMember> findByGroupId(Long groupId);
    
    /**
     * 查询用户在群组中的信息
     */
    GroupMember findByGroupIdAndUserId(Long groupId, Long userId);
    
    /**
     * 检查用户是否在群组中
     */
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    
    /**
     * 统计群组成员数量
     */
    int countByGroupId(Long groupId);
    
    /**
     * 查询用户加入的群组列表
     */
    List<GroupMember> findByUserId(Long userId);
}
