package com.im.backend.controller;

import com.im.backend.service.ContactPinnedService;
import com.im.backend.dto.ContactPinnedRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contacts/pinned")
@RequiredArgsConstructor
@Slf4j
public class ContactPinnedController {

    private final ContactPinnedService contactPinnedService;

    @GetMapping
    public ResponseEntity<?> getPinnedContacts(@RequestHeader("X-User-Id") Long userId) {
        var contacts = contactPinnedService.getPinnedContacts(userId);
        return ResponseEntity.ok(Map.of("pinned", contacts));
    }

    @PostMapping
    public ResponseEntity<?> pinContact(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ContactPinnedRequest request) {
        var pinned = contactPinnedService.pinContact(userId, request.getContactId(), request.getPinOrder());
        return ResponseEntity.ok(Map.of("pinned", pinned));
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<?> unpinContact(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long contactId) {
        contactPinnedService.unpinContact(userId, contactId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/reorder")
    public ResponseEntity<?> reorderPins(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, List<Long>> body) {
        List<Long> contactIds = body.get("contactIds");
        if (contactIds != null) {
            contactPinnedService.reorderPins(userId, contactIds);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
