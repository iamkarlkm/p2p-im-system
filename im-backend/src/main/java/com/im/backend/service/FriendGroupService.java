package com.im.backend.service;

import com.im.backend.dto.FriendGroupDTO;
import com.im.backend.dto.FriendGroupResponseDTO;
import com.im.backend.dto.MoveFriendToGroupDTO;

import java.util.List;

/**
 * 好友分组服务接口
 */
public interface FriendGroupService {

    /**
     * 创建好友分组
     */
    FriendGroupResponseDTO createGroup(Long userId, FriendGroupDTO dto);

    /**
     * 更新分组信息
     */
    FriendGroupResponseDTO updateGroup(Long userId, Long groupId, FriendGroupDTO dto);

    /**
     * 删除分组
     */
    void deleteGroup(Long userId, Long groupId);

    /**
     * 获取用户的所有分组
     */
    List<FriendGroupResponseDTO> getUserGroups(Long userId);

    /**
     * 获取分组详情
     */
    FriendGroupResponseDTO getGroupDetail(Long userId, Long groupId);

    /**
     * 重命名分组
     */
    FriendGroupResponseDTO renameGroup(Long userId, Long groupId, String newName);

    /**
     * 更新分组排序
     */
    void updateGroupSortOrder(Long userId, List<Long> groupIds);

    /**
     * 添加好友到分组
     */
    void addFriendToGroup(Long userId, Long groupId, Long friendId);

    /**
     * 从分组移除好友
     */
    void removeFriendFromGroup(Long userId, Long groupId, Long friendId);

    /**
     * 移动好友到其他分组
     */
    void moveFriendToGroup(Long userId, MoveFriendToGroupDTO dto);

    /**
     * 更新好友在分组中的排序
     */
    void updateFriendSortOrder(Long userId, Long groupId, List<Long> friendIds);

    /**
     * 设置分组成员星标
     */
    void setMemberStarred(Long userId, Long groupId, Long friendId, Boolean starred);

    /**
     * 设置分组成员静音
     */
    void setMemberMuted(Long userId, Long groupId, Long friendId, Boolean muted);

    /**
     * 创建默认分组
     */
    FriendGroupResponseDTO createDefaultGroup(Long userId);
}
