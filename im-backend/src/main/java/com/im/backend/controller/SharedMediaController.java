package com.im.backend.controller;

import com.im.backend.dto.SharedMediaRequest;
import com.im.backend.dto.SharedMediaResponse;
import com.im.backend.service.SharedMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class SharedMediaController {
    private final SharedMediaService mediaService;

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<SharedMediaResponse.MediaPage> getSharedMedia(
            @PathVariable String conversationId,
            @RequestParam(required = false) String mediaType,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String senderId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        
        SharedMediaRequest request = new SharedMediaRequest();
        request.setConversationId(conversationId);
        request.setPage(page);
        request.setSize(size);
        request.setSenderId(senderId);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        if (mediaType != null) {
            request.setMediaType(com.im.backend.entity.SharedMedia.MediaType.valueOf(mediaType.toUpperCase()));
        }
        return ResponseEntity.ok(mediaService.getSharedMedia(request));
    }

    @GetMapping("/timeline/{conversationId}")
    public ResponseEntity<List<SharedMediaResponse>> getMediaTimeline(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(mediaService.getMediaTimeline(conversationId, page, size));
    }

    @GetMapping("/statistics/{conversationId}")
    public ResponseEntity<SharedMediaResponse.MediaStatistics> getStatistics(@PathVariable String conversationId) {
        return ResponseEntity.ok(mediaService.getMediaStatistics(conversationId));
    }

    @GetMapping("/links/{conversationId}")
    public ResponseEntity<List<SharedMediaResponse.LinkPreview>> getSharedLinks(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(mediaService.getSharedLinks(conversationId, page, size));
    }

    @GetMapping("/album/{conversationId}")
    public ResponseEntity<SharedMediaResponse.MediaPage> getAlbumMedia(
            @PathVariable String conversationId,
            @RequestParam String albumType,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size) {
        return ResponseEntity.ok(mediaService.getAlbumMedia(
            conversationId,
            com.im.backend.entity.SharedMedia.MediaType.valueOf(albumType.toUpperCase()),
            page, size));
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> deleteMedia(
            @PathVariable Long mediaId,
            @RequestHeader("X-User-Id") String userId) {
        mediaService.deleteMedia(mediaId, userId);
        return ResponseEntity.noContent().build();
    }
}
