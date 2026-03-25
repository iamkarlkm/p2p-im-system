package com.im.backend.service;

import com.im.backend.entity.ContactGroupEntity;
import com.im.backend.repository.ContactGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 联系人好友分组服务
 */
@Service
@Transactional
public class ContactGroupService {

    @Autowired
    private ContactGroupRepository groupRepository;

    // 基础 CRUD

    public ContactGroupEntity createGroup(Long userId, String groupName, String description, String icon, String color) {
        if (groupRepository.existsByUserIdAndGroupName(userId, groupName)) {
            throw new IllegalArgumentException("分组名称已存在: " + groupName);
        }

        Integer maxIndex = groupRepository.findMaxSortIndexByUserId(userId);
        int newIndex = (maxIndex == null) ? 0 : maxIndex + 1;

        ContactGroupEntity group = new ContactGroupEntity(userId, groupName, description, icon, color);
        group.setSortIndex(newIndex);
        return groupRepository.save(group);
    }

    public ContactGroupEntity updateGroup(Long userId, Long groupId, String groupName, String description, String icon, String color) {
        ContactGroupEntity group = getGroupById(userId, groupId);
        
        if (groupName != null && !groupName.equals(group.getGroupName())) {
            if (groupRepository.existsByUserIdAndGroupName(userId, groupName)) {
                throw new IllegalArgumentException("分组名称已存在: " + groupName);
            }
            group.setGroupName(groupName);
        }
        
        if (description != null) group.setDescription(description);
        if (icon != null) group.setIcon(icon);
        if (color != null) group.setColor(color);
        
        return groupRepository.save(group);
    }

    public void deleteGroup(Long userId, Long groupId) {
        ContactGroupEntity group = getGroupById(userId, groupId);
        if (group.getIsDefault()) {
            throw new IllegalArgumentException("不能删除默认分组");
        }
        if (group.getContactCount() > 0) {
            throw new IllegalArgumentException("分组内还有好友，无法删除");
        }
        groupRepository.delete(group);
    }

    public ContactGroupEntity getGroupById(Long userId, Long groupId) {
        return groupRepository.findByUserIdAndId(userId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("分组不存在: " + groupId));
    }

    public List<ContactGroupEntity> getUserGroups(Long userId) {
        return groupRepository.findByUserIdOrderBySortIndexAsc(userId);
    }

    public List<ContactGroupEntity> getCustomGroups(Long userId) {
        return groupRepository.findCustomGroupsByUserId(userId);
    }

    public List<ContactGroupEntity> getDefaultGroups(Long userId) {
        return groupRepository.findDefaultGroupsByUserId(userId);
    }

    // 分组管理

    public ContactGroupEntity updateGroupSortIndex(Long userId, Long groupId, Integer newIndex) {
        ContactGroupEntity group = getGroupById(userId, groupId);
        group.setSortIndex(newIndex);
        return groupRepository.save(group);
    }

    public void updateContactCount(Long userId, Long groupId, int delta) {
        groupRepository.updateContactCount(groupId, userId, delta);
    }

    public long getGroupCount(Long userId) {
        return groupRepository.countByUserId(userId);
    }

    public long getTotalContactCount(Long userId) {
        Long count = groupRepository.sumContactCountByUserId(userId);
        return count == null ? 0 : count;
    }

    public void cleanupEmptyGroups(Long userId) {
        groupRepository.deleteEmptyHiddenGroupsByUserId(userId);
    }

    public void deleteAllCustomGroups(Long userId) {
        groupRepository.deleteCustomGroupsByUserId(userId);
    }

    public Optional<ContactGroupEntity> getGroupByName(Long userId, String groupName) {
        return groupRepository.findByUserIdAndGroupName(userId, groupName);
    }
}