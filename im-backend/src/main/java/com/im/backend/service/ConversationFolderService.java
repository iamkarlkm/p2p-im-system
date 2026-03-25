package com.im.backend.service;

import com.im.backend.entity.ConversationFolder;
import com.im.backend.entity.ConversationFolderMembership;
import com.im.backend.repository.ConversationFolderRepository;
import com.im.backend.repository.ConversationFolderMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationFolderService {

    private final ConversationFolderRepository folderRepository;
    private final ConversationFolderMembershipRepository membershipRepository;

    public List<ConversationFolder> getUserFolders(Long userId) {
        return folderRepository.findByUserIdOrderBySortOrder(userId);
    }

    @Transactional
    public ConversationFolder createFolder(Long userId, String name, String icon, String color) {
        ConversationFolder folder = ConversationFolder.builder()
                .userId(userId)
                .name(name)
                .icon(icon)
                .color(color)
                .sortOrder(0)
                .isCollapsed(false)
                .build();
        return folderRepository.save(folder);
    }

    @Transactional
    public ConversationFolder updateFolder(Long folderId, Long userId, String name, String icon, String color, Integer sortOrder, Boolean isCollapsed) {
        ConversationFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        if (!folder.getUserId().equals(userId)) throw new RuntimeException("Unauthorized");
        if (name != null) folder.setName(name);
        if (icon != null) folder.setIcon(icon);
        if (color != null) folder.setColor(color);
        if (sortOrder != null) folder.setSortOrder(sortOrder);
        if (isCollapsed != null) folder.setIsCollapsed(isCollapsed);
        return folderRepository.save(folder);
    }

    @Transactional
    public void deleteFolder(Long folderId, Long userId) {
        ConversationFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        if (!folder.getUserId().equals(userId)) throw new RuntimeException("Unauthorized");
        membershipRepository.findByFolderId(folderId).forEach(membershipRepository::delete);
        folderRepository.delete(folder);
    }

    @Transactional
    public void addConversationToFolder(Long folderId, Long conversationId, Long userId) {
        ConversationFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        if (!folder.getUserId().equals(userId)) throw new RuntimeException("Unauthorized");
        membershipRepository.findByFolderIdAndConversationId(folderId, conversationId)
                .orElseGet(() -> {
                    ConversationFolderMembership m = ConversationFolderMembership.builder()
                            .folderId(folderId)
                            .conversationId(conversationId)
                            .userId(userId)
                            .build();
                    return membershipRepository.save(m);
                });
    }

    @Transactional
    public void removeConversationFromFolder(Long folderId, Long conversationId) {
        membershipRepository.deleteByFolderIdAndConversationId(folderId, conversationId);
    }

    public List<ConversationFolderMembership> getFolderMembers(Long folderId) {
        return membershipRepository.findByFolderId(folderId);
    }
}
