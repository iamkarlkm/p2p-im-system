package com.im.service;

import com.im.entity.Group;
import com.im.entity.GroupMember;
import java.util.List;

/**
 * 群组服务接口
 * 功能 #5: 群组管理基础模块
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public interface IGroupService {
    
    /**
     * 创建群组
     */
    Group createGroup(String ownerId, String groupName, String description);
    
    /**
     * 解散群组
     */
    boolean dissolveGroup(String groupId, String operatorId);
    
    /**
     * 邀请成员
     */
    boolean inviteMember(String groupId, String inviterId, String userId);
    
    /**
     * 踢出成员
     */
    boolean kickMember(String groupId, String operatorId, String userId);
    
    /**
     * 退出群组
     */
    boolean leaveGroup(String groupId, String userId);
    
    /**
     * 获取群组信息
     */
    Group getGroupInfo(String groupId);
    
    /**
     * 获取群成员列表
     */
    List<GroupMember> getGroupMembers(String groupId);
    
    /**
     * 设置管理员
     */
    boolean setAdmin(String groupId, String ownerId, String userId);
    
    /**
     * 转让群主
     */
    boolean transferOwner(String groupId, String ownerId, String newOwnerId);
    
    /**
     * 更新群组信息
     */
    boolean updateGroupInfo(String groupId, String operatorId, String groupName, String description);
    
    /**
     * 全员禁言
     */
    boolean muteAll(String groupId, String operatorId, boolean mute);
    
    /**
     * 禁言成员
     */
    boolean muteMember(String groupId, String operatorId, String userId, int minutes);
    
    /**
     * 获取用户加入的群组
     */
    List<Group> getUserGroups(String userId);
    
    /**
     * 检查是否是群成员
     */
    boolean isGroupMember(String groupId, String userId);
}
