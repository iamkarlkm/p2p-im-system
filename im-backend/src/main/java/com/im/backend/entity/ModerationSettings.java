package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModerationSettings {
    private Long userId;
    private boolean enableAutoModeration;
    private boolean enableKeywordFilter;
    private boolean enableImageModeration;
    private boolean enableSpamDetection;
    private boolean allowAnonymousReports;
    private int maxReportsPerDay;

    public static ModerationSettings defaultSettings(Long userId) {
        ModerationSettings settings = new ModerationSettings();
        settings.setUserId(userId);
        settings.setEnableAutoModeration(true);
        settings.setEnableKeywordFilter(true);
        settings.setEnableImageModeration(false);
        settings.setEnableSpamDetection(true);
        settings.setAllowAnonymousReports(true);
        settings.setMaxReportsPerDay(10);
        return settings;
    }
}
