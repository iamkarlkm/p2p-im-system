package com.im.backend.dto;

import com.im.backend.entity.SharedMedia.MediaType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedMediaRequest {
    private String conversationId;
    private MediaType mediaType;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortOrder = "desc";
    private Long startTime;
    private Long endTime;
    private String senderId;
    private String keyword;
}
