package com.im.backend.service;

import com.im.backend.dto.GroupCreateRequest;
import com.im.backend.dto.GroupMemberRequest;
import com.im.backend.entity.Group;
import com.im.backend.entity.GroupMember;

import java.util.List;

/**
 * 群组服务接口
 * 功能 #5: 群组管理基础模块
 */
public interface GroupService {

    /**
     * 创建群组
     */
    Group createGroup(Long ownerId, GroupCreateRequest request);

    /**
     * 解散群组
     */
    void dissolveGroup(String groupId, Long operatorId);

    /**
     * 获取群组信息
     */
    Group getGroupInfo(String groupId);

    /**
     * 更新群组信息
     */
    Group updateGroupInfo(String groupId, Long operatorId, GroupCreateRequest request);

    /**
     * 获取群组列表(我创建的)
     */
    List<Group> getMyCreatedGroups(Long userId);

    /**
     * 获取群组列表(我加入的)
     */
    List<Group> getMyJoinedGroups(Long userId);

    /**
     * 搜索群组
     */
    List<Group> searchGroups(String keyword, Integer limit);

    /**
     * 邀请成员加入群组
     */
    void inviteMember(String groupId, Long inviterId, Long targetUserId);

    /**
     * 加入群组
     */
    void joinGroup(String groupId, Long userId);

    /**
     * 退出群组
     */
    void quitGroup(String groupId, Long userId);

    /**
     * 踢出成员
     */
    void kickMember(String groupId, Long operatorId, Long targetUserId, String reason);

    /**
     * 设置管理员
     */
    void setAdmin(String groupId, Long operatorId, Long targetUserId);

    /**
     * 取消管理员
     */
    void unsetAdmin(String groupId, Long operatorId, Long targetUserId);

    /**
     * 转让群主
     */
    void transferOwnership(String groupId, Long currentOwnerId, Long newOwnerId);

    /**
     * 禁言成员
     */
    void muteMember(String groupId, Long operatorId, Long targetUserId, Integer durationMinutes);

    /**
     * 解除禁言
     */
    void unmuteMember(String groupId, Long operatorId, Long targetUserId);

    /**
     * 获取群组成员列表
     */
    List<GroupMember> getGroupMembers(String groupId);

    /**
     * 获取群组成员详情
     */
    GroupMember getGroupMember(String groupId, Long userId);

    /**
     * 更新群昵称
     */
    void updateGroupNickname(String groupId, Long userId, String nickname);

    /**
     * 检查用户是否为群组成员
     */
    boolean isGroupMember(String groupId, Long userId);

    /**
     * 检查用户是否为群主
     */
    boolean isGroupOwner(String groupId, Long userId);

    /**
     * 检查用户是否为管理员
     */
    boolean isGroupAdmin(String groupId, Long userId);

    /**
     * 处理成员操作请求
     */
    void handleMemberOperation(Long operatorId, GroupMemberRequest request);
}
