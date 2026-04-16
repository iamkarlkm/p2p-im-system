package com.im.service.group.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.service.group.dto.*;
import com.im.service.group.entity.Group;
import com.im.service.group.entity.GroupMember;
import com.im.service.group.mapper.GroupMemberRepository;
import com.im.service.group.mapper.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 群组服务
 * 提供群组管理、成员管理、权限控制等业务逻辑
 *
 * @author IM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService extends ServiceImpl<GroupRepository, Group> {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    // ==================== 群组管理 ====================

    /**
     * 创建群组
     *
     * @param request  创建请求
     * @param ownerId  群主ID
     * @return 创建的群组
     */
    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request, Long ownerId) {
        log.info("Creating group: {}, owner: {}", request.getName(), ownerId);

        // 创建群组实体
        Group group = new Group();
        group.setName(request.getName());
        group.setAvatar(request.getAvatar());
        group.setDescription(request.getDescription());
        group.setOwnerId(ownerId);
        group.setType(request.getType());
        group.setJoinType(request.getJoinType());
        group.setSpeakPermission(request.getSpeakPermission());
        group.setAllowMemberInvite(request.getAllowMemberInvite());
        group.setAllowMemberModifyName(request.getAllowMemberModifyName());
        group.setEnableVerify(request.getEnableVerify());
        group.setMaxMembers(request.getMaxMembers());
        group.setMemberCount(1); // 群主
        group.setStatus(Group.Status.NORMAL);

        // 保存群组
        groupRepository.insert(group);

        // 创建群主成员记录
        GroupMember owner = GroupMember.createOwner(group.getId(), ownerId);
        groupMemberRepository.insert(owner);

        // 添加邀请的成员
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            addMembersInternal(group.getId(), request.getMemberIds(), ownerId, GroupMember.JoinType.INVITE);
            group.setMemberCount(1 + request.getMemberIds().size());
            groupRepository.updateMemberCount(group.getId(), group.getMemberCount());
        }

        log.info("Group created successfully: id={}, name={}", group.getId(), group.getName());
        return convertToGroupResponse(group, ownerId);
    }

    /**
     * 获取群组详情
     *
     * @param groupId 群组ID
     * @param userId  当前用户ID
     * @return 群组详情
     */
    public GroupResponse getGroupDetail(Long groupId, Long userId) {
        Group group = groupRepository.selectById(groupId);
        if (group == null || group.getDeleted()) {
            throw new RuntimeException("群组不存在");
        }
        return convertToGroupResponse(group, userId);
    }

    /**
     * 更新群组信息
     *
     * @param groupId 群组ID
     * @param request 更新请求
     * @param userId  操作用户ID
     * @return 更新后的群组
     */
    @Transactional
    public GroupResponse updateGroup(Long groupId, UpdateGroupRequest request, Long userId) {
        // 检查权限
        checkModifyPermission(groupId, userId);

        Group group = groupRepository.selectById(groupId);
        if (group == null || group.getDeleted()) {
            throw new RuntimeException("群组不存在");
        }

        // 更新群组信息
        group.updateInfo(request.getName(), request.getAvatar(), request.getDescription());
        group.updateSettings(request.getJoinType(), request.getSpeakPermission(),
                request.getAllowMemberInvite(), request.getAllowMemberModifyName(), request.getEnableVerify());
        if (request.getMaxMembers() != null) {
            group.setMaxMembers(request.getMaxMembers());
        }

        groupRepository.updateById(group);
        log.info("Group updated: id={}, by={}", groupId, userId);

        return convertToGroupResponse(group, userId);
    }

    /**
     * 解散群组
     *
     * @param groupId 群组ID
     * @param userId  操作用户ID
     */
    @Transactional
    public void dissolveGroup(Long groupId, Long userId) {
        // 只有群主可以解散群组
        if (!groupRepository.isOwner(groupId, userId)) {
            throw new RuntimeException("只有群主可以解散群组");
        }

        Group group = groupRepository.selectById(groupId);
        if (group == null || group.getDeleted()) {
            throw new RuntimeException("群组不存在");
        }

        if (group.isDissolved()) {
            throw new RuntimeException("群组已经解散");
        }

        group.dissolve(userId);
        groupRepository.updateById(group);

        // 将所有成员标记为已退出
        List<GroupMember> members = groupMemberRepository.findActiveMembersByGroupId(groupId);
        for (GroupMember member : members) {
            member.markAsLeft();
            groupMemberRepository.updateById(member);
        }

        log.info("Group dissolved: id={}, by={}", groupId, userId);
    }

    /**
     * 搜索群组
     *
     * @param keyword  关键字
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public IPage<GroupResponse> searchGroups(String keyword, int pageNum, int pageSize) {
        Page<Group> page = new Page<>(pageNum, pageSize);
        IPage<Group> groupPage = groupRepository.searchByNamePage(page, keyword);

        List<GroupResponse> responses = groupPage.getRecords().stream()
                .map(g -> convertToGroupResponse(g, null))
                .collect(Collectors.toList());

        Page<GroupResponse> resultPage = new Page<>(pageNum, pageSize, groupPage.getTotal());
        resultPage.setRecords(responses);
        return resultPage;
    }

    /**
     * 获取用户创建的群组
     *
     * @param ownerId  群主ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public IPage<GroupResponse> getOwnedGroups(Long ownerId, int pageNum, int pageSize) {
        Page<Group> page = new Page<>(pageNum, pageSize);
        IPage<Group> groupPage = groupRepository.findByOwnerIdPage(page, ownerId);

        List<GroupResponse> responses = groupPage.getRecords().stream()
                .map(g -> convertToGroupResponse(g, ownerId))
                .collect(Collectors.toList());

        Page<GroupResponse> resultPage = new Page<>(pageNum, pageSize, groupPage.getTotal());
        resultPage.setRecords(responses);
        return resultPage;
    }

    /**
     * 获取用户加入的群组
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public IPage<GroupResponse> getJoinedGroups(Long userId, int pageNum, int pageSize) {
        Page<GroupMember> page = new Page<>(pageNum, pageSize);
        IPage<GroupMember> memberPage = groupMemberRepository.findByUserIdPage(page, userId);

        List<GroupResponse> responses = new ArrayList<>();
        for (GroupMember member : memberPage.getRecords()) {
            Group group = groupRepository.selectById(member.getGroupId());
            if (group != null && !group.getDeleted() && !group.isDissolved()) {
                responses.add(convertToGroupResponse(group, userId));
            }
        }

        Page<GroupResponse> resultPage = new Page<>(pageNum, pageSize, memberPage.getTotal());
        resultPage.setRecords(responses);
        return resultPage;
    }

    /**
     * 更新群公告
     *
     * @param groupId     群组ID
     * @param content     公告内容
     * @param publisherId 发布者ID
     * @return 更新后的群组
     */
    @Transactional
    public GroupResponse updateAnnouncement(Long groupId, String content, Long publisherId) {
        checkModifyPermission(groupId, publisherId);

        Group group = groupRepository.selectById(groupId);
        if (group == null || group.getDeleted()) {
            throw new RuntimeException("群组不存在");
        }

        group.updateAnnouncement(content, publisherId);
        groupRepository.updateById(group);

        log.info("Group announcement updated: id={}, by={}", groupId, publisherId);
        return convertToGroupResponse(group, publisherId);
    }

    /**
     * 全员禁言
     *
     * @param groupId         群组ID
     * @param durationMinutes 禁言时长（分钟），null表示永久
     * @param operatorId      操作者ID
     */
    @Transactional
    public void muteAll(Long groupId, Integer durationMinutes, Long operatorId) {
        checkAdminPermission(groupId, operatorId);

        Group group = groupRepository.selectById(groupId);
        if (group == null || group.getDeleted()) {
            throw new RuntimeException("群组不存在");
        }

        group.muteAll(durationMinutes);
        groupRepository.updateById(group);

        log.info("Group muted all: id={}, duration={}, by={}", groupId, durationMinutes, operatorId);
    }

    /**
     * 取消全员禁言
     *
     * @param groupId    群组ID
     * @param operatorId 操作者ID
     */
    @Transactional
    public void unmuteAll(Long groupId, Long operatorId) {
        checkAdminPermission(groupId, operatorId);

        Group group = groupRepository.selectById(groupId);
        if (group == null || group.getDeleted()) {
            throw new RuntimeException("群组不存在");
        }

        group.unmuteAll();
        groupRepository.updateById(group);

        log.info("Group unmuted all: id={}, by={}", groupId, operatorId);
    }

    // ==================== 成员管理 ====================

    /**
     * 添加群成员
     *
     * @param groupId 群组ID
     * @param request 添加请求
     * @param operatorId 操作者ID
     * @return 添加的成员列表
     */
    @Transactional
    public List<GroupMemberResponse> addMembers(Long groupId, AddGroupMemberRequest request, Long operatorId) {
        Group group = groupRepository.selectById(groupId);
        if (group == null || group.getDeleted()) {
            throw new RuntimeException("群组不存在");
        }

        if (group.isDissolved()) {
            throw new RuntimeException("群组已解散");
        }

        // 检查是否有权限添加成员
        if (!canInviteMember(groupId, operatorId)) {
            throw new RuntimeException("没有权限添加成员");
        }

        // 检查群组是否已满
        if (group.isFull()) {
            throw new RuntimeException("群组已满");
        }

        // 检查添加数量是否超过限制
        int availableSlots = group.getMaxMembers() - group.getMemberCount();
        if (request.getUserIds().size() > availableSlots) {
            throw new RuntimeException("超出群组人数限制，还可添加" + availableSlots + "人");
        }

        // 过滤掉已经在群中的用户
        List<Long> newUserIds = request.getUserIds().stream()
                .filter(userId -> !groupMemberRepository.existsByGroupIdAndUserId(groupId, userId))
                .collect(Collectors.toList());

        if (newUserIds.isEmpty()) {
            throw new RuntimeException("所选用户已经在群组中");
        }

        // 添加成员
        addMembersInternal(groupId, newUserIds, operatorId, request.getJoinType());

        // 更新成员数
        group.setMemberCount(group.getMemberCount() + newUserIds.size());
        groupRepository.updateMemberCount(groupId, group.getMemberCount());

        log.info("Members added to group: groupId={}, count={}, by={}", groupId, newUserIds.size(), operatorId);

        // 返回添加的成员信息
        return newUserIds.stream()
                .map(userId -> getMemberInfo(groupId, userId))
                .collect(Collectors.toList());
    }

    /**
     * 移除群成员
     *
     * @param groupId 群组ID
     * @param userId  要移除的用户ID
     * @param operatorId 操作者ID
     */
    @Transactional
    public void removeMember(Long groupId, Long userId, Long operatorId) {
        // 不能移除自己
        if (userId.equals(operatorId)) {
            throw new RuntimeException("不能移除自己，请使用退出群组功能");
        }

        // 检查权限
        if (!canRemoveMember(groupId, operatorId, userId)) {
            throw new RuntimeException("没有权限移除该成员");
        }

        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null || !member.isInGroup()) {
            throw new RuntimeException("成员不在群组中");
        }

        member.markAsRemoved();
        groupMemberRepository.updateById(member);

        // 减少成员数
        groupRepository.decrementMemberCount(groupId);

        log.info("Member removed from group: groupId={}, userId={}, by={}", groupId, userId, operatorId);
    }

    /**
     * 退出群组
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     */
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        Group group = groupRepository.selectById(groupId);
        if (group == null || group.getDeleted()) {
            throw new RuntimeException("群组不存在");
        }

        // 群主不能退出，必须先转让群主
        if (groupRepository.isOwner(groupId, userId)) {
            throw new RuntimeException("群主不能退出群组，请先转让群主身份");
        }

        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null || !member.isInGroup()) {
            throw new RuntimeException("您不在该群组中");
        }

        member.markAsLeft();
        groupMemberRepository.updateById(member);

        // 减少成员数
        groupRepository.decrementMemberCount(groupId);

        log.info("Member left group: groupId={}, userId={}", groupId, userId);
    }

    /**
     * 获取群成员列表
     *
     * @param groupId  群组ID
     * @param userId   当前用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public IPage<GroupMemberResponse> getGroupMembers(Long groupId, Long userId, int pageNum, int pageSize) {
        // 检查用户是否在群组中
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("您不在该群组中");
        }

        Page<GroupMember> page = new Page<>(pageNum, pageSize);
        IPage<GroupMember> memberPage = groupMemberRepository.findByGroupIdPage(page, groupId);

        List<GroupMemberResponse> responses = memberPage.getRecords().stream()
                .map(this::convertToMemberResponse)
                .collect(Collectors.toList());

        Page<GroupMemberResponse> resultPage = new Page<>(pageNum, pageSize, memberPage.getTotal());
        resultPage.setRecords(responses);
        return resultPage;
    }

    /**
     * 获取群成员详情
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return 成员信息
     */
    public GroupMemberResponse getMemberInfo(Long groupId, Long userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }
        return convertToMemberResponse(member);
    }

    /**
     * 更新成员角色
     *
     * @param groupId   群组ID
     * @param userId    用户ID
     * @param request   角色更新请求
     * @param operatorId 操作者ID
     * @return 更新后的成员信息
     */
    @Transactional
    public GroupMemberResponse updateMemberRole(Long groupId, Long userId, UpdateMemberRoleRequest request, Long operatorId) {
        Group group = groupRepository.selectById(groupId);
        if (group == null || group.getDeleted()) {
            throw new RuntimeException("群组不存在");
        }

        // 只有群主可以设置管理员
        if (request.getRole() == GroupMember.Role.ADMIN && !groupRepository.isOwner(groupId, operatorId)) {
            throw new RuntimeException("只有群主可以设置管理员");
        }

        // 群主不能被修改角色
        if (groupMemberRepository.isOwner(groupId, userId)) {
            throw new RuntimeException("不能修改群主的角色");
        }

        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null || !member.isInGroup()) {
            throw new RuntimeException("成员不在群组中");
        }

        member.setRole(request.getRole());

        // 更新自定义权限
        if (request.getCanInvite() != null) {
            member.setCanInvite(request.getCanInvite());
        }
        if (request.getCanMute() != null) {
            member.setCanMute(request.getCanMute());
        }
        if (request.getCanModifyInfo() != null) {
            member.setCanModifyInfo(request.getCanModifyInfo());
        }
        if (request.getCanRemoveMember() != null) {
            member.setCanRemoveMember(request.getCanRemoveMember());
        }

        groupMemberRepository.updateById(member);

        log.info("Member role updated: groupId={}, userId={}, role={}, by={}",
                groupId, userId, request.getRole(), operatorId);

        return convertToMemberResponse(member);
    }

    /**
     * 转让群主
     *
     * @param groupId    群组ID
     * @param newOwnerId 新群主ID
     * @param operatorId 操作者ID
     */
    @Transactional
    public void transferOwnership(Long groupId, Long newOwnerId, Long operatorId) {
        // 只有群主可以转让
        if (!groupRepository.isOwner(groupId, operatorId)) {
            throw new RuntimeException("只有群主可以转让群主身份");
        }

        GroupMember newOwner = groupMemberRepository.findByGroupIdAndUserId(groupId, newOwnerId);
        if (newOwner == null || !newOwner.isInGroup()) {
            throw new RuntimeException("新群主不在群组中");
        }

        // 更新新群主角色
        newOwner.setRole(GroupMember.Role.OWNER);
        groupMemberRepository.updateById(newOwner);

        // 更新原群主角色为管理员
        GroupMember oldOwner = groupMemberRepository.findByGroupIdAndUserId(groupId, operatorId);
        oldOwner.setRole(GroupMember.Role.ADMIN);
        groupMemberRepository.updateById(oldOwner);

        // 更新群组ownerId
        groupRepository.transferOwnership(groupId, newOwnerId);

        log.info("Group ownership transferred: groupId={}, from={}, to={}", groupId, operatorId, newOwnerId);
    }

    /**
     * 禁言成员
     *
     * @param groupId    群组ID
     * @param userId     用户ID
     * @param request    禁言请求
     * @param operatorId 操作者ID
     */
    @Transactional
    public void muteMember(Long groupId, Long userId, MuteMemberRequest request, Long operatorId) {
        if (!canMuteMember(groupId, operatorId, userId)) {
            throw new RuntimeException("没有权限禁言该成员");
        }

        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null || !member.isInGroup()) {
            throw new RuntimeException("成员不在群组中");
        }

        LocalDateTime muteEndTime = null;
        if (request.getDurationMinutes() != null && request.getDurationMinutes() > 0) {
            muteEndTime = LocalDateTime.now().plusMinutes(request.getDurationMinutes());
        }

        member.mute(request.getDurationMinutes(), request.getReason(), operatorId);
        groupMemberRepository.updateById(member);

        log.info("Member muted: groupId={}, userId={}, duration={}, by={}",
                groupId, userId, request.getDurationMinutes(), operatorId);
    }

    /**
     * 解除禁言
     *
     * @param groupId    群组ID
     * @param userId     用户ID
     * @param operatorId 操作者ID
     */
    @Transactional
    public void unmuteMember(Long groupId, Long userId, Long operatorId) {
        if (!canMuteMember(groupId, operatorId, userId)) {
            throw new RuntimeException("没有权限解除禁言");
        }

        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null || !member.isInGroup()) {
            throw new RuntimeException("成员不在群组中");
        }

        member.unmute();
        groupMemberRepository.updateById(member);

        log.info("Member unmuted: groupId={}, userId={}, by={}", groupId, userId, operatorId);
    }

    /**
     * 更新成员群昵称
     *
     * @param groupId  群组ID
     * @param userId   用户ID
     * @param nickname 昵称
     * @param operatorId 操作者ID
     * @return 更新后的成员信息
     */
    @Transactional
    public GroupMemberResponse updateMemberNickname(Long groupId, Long userId, String nickname, Long operatorId) {
        // 只能修改自己的昵称，或者管理员可以修改其他人的
        if (!userId.equals(operatorId)) {
            checkAdminPermission(groupId, operatorId);
        }

        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null || !member.isInGroup()) {
            throw new RuntimeException("成员不在群组中");
        }

        member.updateNickname(nickname);
        groupMemberRepository.updateById(member);

        return convertToMemberResponse(member);
    }

    /**
     * 设置消息免打扰
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @param mute    true-开启免打扰，false-关闭免打扰
     */
    @Transactional
    public void setMuteNotifications(Long groupId, Long userId, boolean mute) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null || !member.isInGroup()) {
            throw new RuntimeException("您不在该群组中");
        }

        member.setMuteNotifications(mute);
        groupMemberRepository.updateById(member);
    }

    /**
     * 置顶/取消置顶群聊
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @param pin     true-置顶，false-取消置顶
     */
    @Transactional
    public void setPinned(Long groupId, Long userId, boolean pin) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null || !member.isInGroup()) {
            throw new RuntimeException("您不在该群组中");
        }

        member.setPinned(pin);
        groupMemberRepository.updateById(member);
    }

    // ==================== 权限检查 ====================

    /**
     * 检查是否有修改群信息权限
     */
    private void checkModifyPermission(Long groupId, Long userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null || !member.isInGroup()) {
            throw new RuntimeException("您不在该群组中");
        }

        if (!member.isAdminOrAbove() && !Boolean.TRUE.equals(member.getCanModifyInfo())) {
            throw new RuntimeException("没有权限修改群信息");
        }
    }

    /**
     * 检查是否有管理员权限
     */
    private void checkAdminPermission(Long groupId, Long userId) {
        if (!groupMemberRepository.isAdminOrAbove(groupId, userId)) {
            throw new RuntimeException("没有管理员权限");
        }
    }

    /**
     * 检查是否有邀请成员权限
     */
    private boolean canInviteMember(Long groupId, Long userId) {
        Group group = groupRepository.selectById(groupId);
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);

        if (member == null || !member.isInGroup()) {
            return false;
        }

        // 群主和管理员可以邀请
        if (member.isAdminOrAbove()) {
            return true;
        }

        // 普通成员需要群组允许成员邀请
        return Boolean.TRUE.equals(group.getAllowMemberInvite()) && Boolean.TRUE.equals(member.getCanInvite());
    }

    /**
     * 检查是否有移除成员权限
     */
    private boolean canRemoveMember(Long groupId, Long operatorId, Long targetUserId) {
        // 不能移除群主
        if (groupMemberRepository.isOwner(groupId, targetUserId)) {
            return false;
        }

        GroupMember operator = groupMemberRepository.findByGroupIdAndUserId(groupId, operatorId);
        if (operator == null || !operator.isInGroup()) {
            return false;
        }

        // 群主可以移除任何人
        if (operator.isOwner()) {
            return true;
        }

        // 管理员可以移除普通成员
        if (operator.isAdmin() && groupMemberRepository.findByGroupIdAndUserId(groupId, targetUserId).isMember()) {
            return true;
        }

        // 检查自定义权限
        return Boolean.TRUE.equals(operator.getCanRemoveMember());
    }

    /**
     * 检查是否有禁言成员权限
     */
    private boolean canMuteMember(Long groupId, Long operatorId, Long targetUserId) {
        // 不能禁言群主
        if (groupMemberRepository.isOwner(groupId, targetUserId)) {
            return false;
        }

        // 不能禁言自己
        if (operatorId.equals(targetUserId)) {
            return false;
        }

        GroupMember operator = groupMemberRepository.findByGroupIdAndUserId(groupId, operatorId);
        if (operator == null || !operator.isInGroup()) {
            return false;
        }

        // 群主可以禁言任何人
        if (operator.isOwner()) {
            return true;
        }

        // 管理员可以禁言普通成员
        GroupMember target = groupMemberRepository.findByGroupIdAndUserId(groupId, targetUserId);
        if (operator.isAdmin() && target.isMember()) {
            return true;
        }

        // 检查自定义权限
        return Boolean.TRUE.equals(operator.getCanMute());
    }

    // ==================== 私有方法 ====================

    /**
     * 内部方法：添加成员
     */
    private void addMembersInternal(Long groupId, List<Long> userIds, Long invitedBy, Integer joinType) {
        for (Long userId : userIds) {
            GroupMember member = GroupMember.createMember(groupId, userId, joinType, invitedBy);
            groupMemberRepository.insert(member);
        }
    }

    /**
     * 转换群组实体为响应DTO
     */
    private GroupResponse convertToGroupResponse(Group group, Long currentUserId) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setAvatar(group.getAvatar());
        response.setDescription(group.getDescription());
        response.setOwnerId(group.getOwnerId());
        response.setType(group.getType());
        response.setTypeName(getGroupTypeName(group.getType()));
        response.setJoinType(group.getJoinType());
        response.setJoinTypeName(getJoinTypeName(group.getJoinType()));
        response.setSpeakPermission(group.getSpeakPermission());
        response.setAllowMemberInvite(group.getAllowMemberInvite());
        response.setAllowMemberModifyName(group.getAllowMemberModifyName());
        response.setEnableVerify(group.getEnableVerify());
        response.setMaxMembers(group.getMaxMembers());
        response.setMemberCount(group.getMemberCount());
        response.setAnnouncement(group.getAnnouncement());
        response.setAnnouncementPublisherId(group.getAnnouncementPublisherId());
        response.setAnnouncementTime(group.getAnnouncementTime());
        response.setAnnouncementPinned(group.getAnnouncementPinned());
        response.setAllMuted(group.getAllMuted());
        response.setMuteEndTime(group.getMuteEndTime());
        response.setCurrentlyMuted(group.isCurrentlyMuted());
        response.setStatus(group.getStatus());
        response.setStatusName(getGroupStatusName(group.getStatus()));
        response.setCreateTime(group.getCreateTime());
        response.setUpdateTime(group.getUpdateTime());

        // 填充当前用户相关信息
        if (currentUserId != null) {
            GroupMember myMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), currentUserId);
            if (myMember != null) {
                response.setMyRole(myMember.getRole());
                response.setMyRoleName(getRoleName(myMember.getRole()));
                response.setMyMuted(myMember.getMuted());
                response.setMyMuteEndTime(myMember.getMuteEndTime());
                response.setMyMuteNotifications(myMember.getMuteNotifications());
                response.setMyPinned(myMember.getPinned());
                response.setMyNickname(myMember.getNickname());
            }
        }

        return response;
    }

    /**
     * 转换成员实体为响应DTO
     */
    private GroupMemberResponse convertToMemberResponse(GroupMember member) {
        GroupMemberResponse response = new GroupMemberResponse();
        response.setId(member.getId());
        response.setGroupId(member.getGroupId());
        response.setUserId(member.getUserId());
        response.setNickname(member.getNickname());
        response.setRole(member.getRole());
        response.setRoleName(getRoleName(member.getRole()));
        response.setCanInvite(member.getCanInvite());
        response.setCanMute(member.getCanMute());
        response.setCanModifyInfo(member.getCanModifyInfo());
        response.setCanRemoveMember(member.getCanRemoveMember());
        response.setMuted(member.getMuted());
        response.setMuteEndTime(member.getMuteEndTime());
        response.setCurrentlyMuted(member.isCurrentlyMuted());
        response.setMuteReason(member.getMuteReason());
        response.setMutedBy(member.getMutedBy());
        response.setJoinType(member.getJoinType());
        response.setJoinTypeName(getMemberJoinTypeName(member.getJoinType()));
        response.setInvitedBy(member.getInvitedBy());
        response.setJoinTime(member.getJoinTime());
        response.setMuteNotifications(member.getMuteNotifications());
        response.setPinned(member.getPinned());
        response.setShowNickname(member.getShowNickname());
        response.setStatus(member.getStatus());
        response.setStatusName(getMemberStatusName(member.getStatus()));
        response.setLeaveTime(member.getLeaveTime());
        response.setLastSpeakTime(member.getLastSpeakTime());
        response.setCreateTime(member.getCreateTime());
        response.setUpdateTime(member.getUpdateTime());
        return response;
    }

    // ==================== 辅助方法 ====================

    private String getGroupTypeName(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 0 -> "普通群";
            case 1 -> "企业群";
            case 2 -> "班级群";
            case 3 -> "兴趣群";
            case 4 -> "临时群";
            default -> "未知";
        };
    }

    private String getJoinTypeName(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 0 -> "自由加入";
            case 1 -> "需验证";
            case 2 -> "邀请加入";
            case 3 -> "禁止加入";
            default -> "未知";
        };
    }

    private String getGroupStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "正常";
            case 1 -> "已解散";
            default -> "未知";
        };
    }

    private String getRoleName(Integer role) {
        if (role == null) return "未知";
        return switch (role) {
            case 0 -> "成员";
            case 1 -> "管理员";
            case 2 -> "群主";
            default -> "未知";
        };
    }

    private String getMemberJoinTypeName(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 0 -> "创建";
            case 1 -> "邀请";
            case 2 -> "扫码";
            case 3 -> "链接";
            case 4 -> "搜索加入";
            default -> "未知";
        };
    }

    private String getMemberStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "正常";
            case 1 -> "已退出";
            case 2 -> "被移除";
            default -> "未知";
        };
    }
}
