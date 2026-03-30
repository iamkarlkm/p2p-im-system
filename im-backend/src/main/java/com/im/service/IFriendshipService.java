package com.im.service;

import com.im.entity.Friendship;
import java.util.List;

/**
 * 好友关系服务接口
 * 功能 #4: 好友关系管理系统
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public interface IFriendshipService {
    
    /**
     * 发送好友申请
     */
    Friendship sendFriendRequest(String userId, String friendId, String message);
    
    /**
     * 接受好友申请
     */
    boolean acceptFriendRequest(String friendshipId);
    
    /**
     * 拒绝好友申请
     */
    boolean rejectFriendRequest(String friendshipId);
    
    /**
     * 删除好友
     */
    boolean deleteFriend(String userId, String friendId);
    
    /**
     * 获取好友列表
     */
    List<Friendship> getFriendList(String userId);
    
    /**
     * 获取好友申请列表
     */
    List<Friendship> getPendingRequests(String userId);
    
    /**
     * 修改好友备注
     */
    boolean updateRemark(String userId, String friendId, String remark);
    
    /**
     * 移动好友分组
     */
    boolean moveToGroup(String userId, String friendId, String groupId);
    
    /**
     * 检查是否是好友
     */
    boolean isFriend(String userId, String friendId);
    
    /**
     * 屏蔽好友
     */
    boolean blockFriend(String userId, String friendId);
}
