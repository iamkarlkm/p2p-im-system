package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenshotSettings {
    private Long userId;
    private boolean enableScreenshotNotification;
    private boolean notifyOnCapture;
    private boolean receiveScreenshotAlerts;
    private boolean alertForContacts;
    private boolean alertForGroups;
    private boolean silentMode;

    public static ScreenshotSettings defaultSettings(Long userId) {
        ScreenshotSettings settings = new ScreenshotSettings();
        settings.setUserId(userId);
        settings.setEnableScreenshotNotification(true);
        settings.setNotifyOnCapture(true);
        settings.setReceiveScreenshotAlerts(true);
        settings.setAlertForContacts(true);
        settings.setAlertForGroups(true);
        settings.setSilentMode(false);
        return settings;
    }
}
