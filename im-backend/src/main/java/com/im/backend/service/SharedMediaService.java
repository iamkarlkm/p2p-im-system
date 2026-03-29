package com.im.backend.service;

import com.im.backend.entity.SharedMedia;
import com.im.backend.entity.MediaLink;
import com.im.backend.entity.MediaAlbum;
import com.im.backend.repository.SharedMediaRepository;
import com.im.backend.repository.MediaLinkRepository;
import com.im.backend.repository.MediaAlbumRepository;
import com.im.backend.dto.SharedMediaRequest;
import com.im.backend.dto.SharedMediaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SharedMediaService {
    private final SharedMediaRepository mediaRepository;
    private final MediaLinkRepository linkRepository;
    private final MediaAlbumRepository albumRepository;

    @Transactional(readOnly = true)
    public SharedMediaResponse.MediaPage getSharedMedia(SharedMediaRequest request) {
        List<SharedMedia> mediaList = mediaRepository.findByConversationIdAndFilters(
            request.getConversationId(),
            request.getMediaType(),
            request.getSenderId(),
            request.getStartTime(),
            request.getEndTime(),
            request.getPage() * request.getSize(),
            request.getSize()
        );
        
        Long total = mediaRepository.countByConversationIdAndType(
            request.getConversationId(), request.getMediaType());
        
        List<SharedMediaResponse> items = mediaList.stream()
            .map(this::toMediaResponse)
            .collect(Collectors.toList());

        SharedMediaResponse.MediaStatistics stats = getMediaStatistics(request.getConversationId());

        return SharedMediaResponse.MediaPage.builder()
            .items(items)
            .page(request.getPage())
            .size(request.getSize())
            .total(total)
            .totalPages((int) Math.ceil((double) total / request.getSize()))
            .mediaType(request.getMediaType())
            .statistics(stats)
            .build();
    }

    @Transactional(readOnly = true)
    public List<SharedMediaResponse> getMediaTimeline(String conversationId, Integer page, Integer size) {
        List<SharedMedia> timeline = mediaRepository.findTimelineByConversation(conversationId, page * size, size);
        return timeline.stream().map(this::toMediaResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SharedMediaResponse.MediaStatistics getMediaStatistics(String conversationId) {
        Long imageCount = mediaRepository.countByConversationAndType(conversationId, SharedMedia.MediaType.IMAGE);
        Long videoCount = mediaRepository.countByConversationAndType(conversationId, SharedMedia.MediaType.VIDEO);
        Long audioCount = mediaRepository.countByConversationAndType(conversationId, SharedMedia.MediaType.AUDIO);
        Long fileCount = mediaRepository.countByConversationAndType(conversationId, SharedMedia.MediaType.FILE);
        Long linkCount = linkRepository.countByConversationId(conversationId);
        Long totalSize = mediaRepository.sumFileSizeByConversation(conversationId);

        return SharedMediaResponse.MediaStatistics.builder()
            .imageCount(imageCount)
            .videoCount(videoCount)
            .audioCount(audioCount)
            .fileCount(fileCount)
            .linkCount(linkCount)
            .totalSize(totalSize != null ? totalSize : 0L)
            .build();
    }

    @Transactional(readOnly = true)
    public List<SharedMediaResponse.LinkPreview> getSharedLinks(String conversationId, Integer page, Integer size) {
        List<MediaLink> links = linkRepository.findByConversationId(conversationId, page * size, size);
        return links.stream().map(link -> SharedMediaResponse.LinkPreview.builder()
            .id(link.getId())
            .conversationId(link.getConversationId())
            .messageId(link.getMessageId())
            .url(link.getUrl())
            .title(link.getTitle())
            .description(link.getDescription())
            .image(link.getImage())
            .domain(link.getDomain())
            .linkType(link.getLinkType())
            .createdAt(link.getCreatedAt())
            .build()).collect(Collectors.toList());
    }

    @Transactional
    public void saveMedia(SharedMedia media) {
        mediaRepository.save(media);
    }

    @Transactional
    public void saveLink(MediaLink link) {
        linkRepository.save(link);
    }

    @Transactional
    public void deleteMedia(Long mediaId, String userId) {
        mediaRepository.findById(mediaId).ifPresent(media -> {
            if (media.getSenderId().equals(userId)) {
                media.setIsDeleted(true);
                mediaRepository.save(media);
            }
        });
    }

    @Transactional(readOnly = true)
    public SharedMediaResponse.MediaPage getAlbumMedia(String conversationId, SharedMedia.MediaType albumType, Integer page, Integer size) {
        List<SharedMedia> media = mediaRepository.findByConversationIdAndType(conversationId, albumType, page * size, size);
        Long total = mediaRepository.countByConversationAndType(conversationId, albumType);
        
        List<SharedMediaResponse> items = media.stream().map(this::toMediaResponse).collect(Collectors.toList());
        return SharedMediaResponse.MediaPage.builder()
            .items(items).page(page).size(size).total(total)
            .totalPages((int) Math.ceil((double) total / size))
            .mediaType(albumType).build();
    }

    private SharedMediaResponse toMediaResponse(SharedMedia media) {
        return SharedMediaResponse.builder()
            .id(media.getId())
            .conversationId(media.getConversationId())
            .messageId(media.getMessageId())
            .senderId(media.getSenderId())
            .mediaType(media.getMediaType())
            .fileName(media.getFileName())
            .fileUrl(media.getFileUrl())
            .thumbnailUrl(media.getThumbnailUrl())
            .fileSize(media.getFileSize())
            .mimeType(media.getMimeType())
            .width(media.getWidth())
            .height(media.getHeight())
            .duration(media.getDuration())
            .description(media.getDescription())
            .createdAt(media.getCreatedAt())
            .canDelete(!media.getIsDeleted())
            .build();
    }
}
