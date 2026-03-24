package com.im.backend.service;

import com.im.backend.entity.ContactPinned;
import com.im.backend.repository.ContactPinnedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactPinnedService {

    private final ContactPinnedRepository contactPinnedRepository;

    public List<ContactPinned> getPinnedContacts(Long userId) {
        return contactPinnedRepository.findByUserIdOrderByPinOrderAsc(userId);
    }

    @Transactional
    public ContactPinned pinContact(Long userId, Long contactId, Integer pinOrder) {
        ContactPinned existing = contactPinnedRepository
                .findByUserIdAndContactId(userId, contactId)
                .orElse(null);
        if (existing != null) {
            if (pinOrder != null) existing.setPinOrder(pinOrder);
            existing.setPinnedAt(LocalDateTime.now());
            log.info("Contact {} re-pinned by user {}", contactId, userId);
            return contactPinnedRepository.save(existing);
        }
        int nextOrder = pinOrder != null ? pinOrder : getNextPinOrder(userId);
        ContactPinned pinned = ContactPinned.builder()
                .userId(userId)
                .contactId(contactId)
                .pinOrder(nextOrder)
                .pinnedAt(LocalDateTime.now())
                .build();
        log.info("Contact {} pinned by user {} with order {}", contactId, userId, nextOrder);
        return contactPinnedRepository.save(pinned);
    }

    @Transactional
    public void unpinContact(Long userId, Long contactId) {
        contactPinnedRepository.deleteByUserIdAndContactId(userId, contactId);
        log.info("Contact {} unpinned by user {}", contactId, userId);
    }

    @Transactional
    public void reorderPins(Long userId, List<Long> contactIds) {
        for (int i = 0; i < contactIds.size(); i++) {
            contactPinnedRepository.findByUserIdAndContactId(userId, contactIds.get(i))
                    .ifPresent(pinned -> {
                        pinned.setPinOrder(i);
                        contactPinnedRepository.save(pinned);
                    });
        }
        log.info("Pinned contacts reordered for user {}", userId);
    }

    public boolean isContactPinned(Long userId, Long contactId) {
        return contactPinnedRepository.existsByUserIdAndContactId(userId, contactId);
    }

    private int getNextPinOrder(Long userId) {
        List<ContactPinned> pinned = contactPinnedRepository.findByUserIdOrderByPinOrderAsc(userId);
        if (pinned.isEmpty()) return 0;
        return pinned.get(pinned.size() - 1).getPinOrder() + 1;
    }
}
