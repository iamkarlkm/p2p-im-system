package com.im.backend.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class ReactionDTO {
    private Long messageId;
    private List<EmojiCount> reactions;

    @Data
    @Builder
    public static class EmojiCount {
        private String emoji;
        private Boolean isCustom;
        private Integer count;
        private Boolean userReacted;  // 当前用户是否已添加此表情
    }
}
