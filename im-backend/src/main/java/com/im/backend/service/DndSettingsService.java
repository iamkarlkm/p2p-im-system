package com.im.backend.service;

import com.im.backend.entity.DndSettings;
import com.im.backend.repository.DndSettingsRepository;
import com.im.backend.dto.DndSettingsRequest;
import com.im.backend.dto.DndSettingsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DndSettingsService {

    private final DndSettingsRepository dndSettingsRepository;

    @Transactional
    public DndSettingsResponse saveOrUpdate(Long userId, DndSettingsRequest request) {
        DndSettings settings = dndSettingsRepository.findByUserId(userId)
                .orElse(DndSettings.builder()
                        .userId(userId)
                        .enabled(false)
                        .allowMentions(true)
                        .allowStarred(true)
                        .timezone("Asia/Shanghai")
                        .repeatDays("1,2,3,4,5,6,7")
                        .build());

        if (request.getEnabled() != null) {
            settings.setEnabled(request.getEnabled());
        }
        if (request.getStartTime() != null) {
            settings.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            settings.setEndTime(request.getEndTime());
        }
        if (request.getTimezone() != null) {
            settings.setTimezone(request.getTimezone());
        }
        if (request.getRepeatDays() != null) {
            settings.setRepeatDays(request.getRepeatDays());
        }
        if (request.getAllowMentions() != null) {
            settings.setAllowMentions(request.getAllowMentions());
        }
        if (request.getAllowStarred() != null) {
            settings.setAllowStarred(request.getAllowStarred());
        }
        if (request.getCustomMessage() != null) {
            settings.setCustomMessage(request.getCustomMessage());
        }

        DndSettings saved = dndSettingsRepository.save(settings);
        log.info("DND settings saved for user: {}", userId);
        return toResponse(saved);
    }

    public Optional<DndSettingsResponse> getByUserId(Long userId) {
        return dndSettingsRepository.findByUserId(userId)
                .map(this::toResponse);
    }

    public boolean isInDndPeriod(Long userId) {
        Optional<DndSettings> optSettings = dndSettingsRepository.findByUserId(userId);
        if (optSettings.isEmpty()) {
            return false;
        }
        DndSettings settings = optSettings.get();
        if (!settings.getEnabled()) {
            return false;
        }

        String timezone = settings.getTimezone() != null ? settings.getTimezone() : "Asia/Shanghai";
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalTime currentTime = now.toLocalTime();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        int dayValue = dayOfWeek.getValue();

        String repeatDays = settings.getRepeatDays();
        if (repeatDays != null && !repeatDays.isEmpty()) {
            boolean dayAllowed = false;
            for (String d : repeatDays.split(",")) {
                try {
                    if (Integer.parseInt(d.trim()) == dayValue) {
                        dayAllowed = true;
                        break;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            if (!dayAllowed) {
                return false;
            }
        }

        LocalTime startTime = settings.getStartTime();
        LocalTime endTime = settings.getEndTime();
        if (startTime == null || endTime == null) {
            return false;
        }

        if (startTime.isBefore(endTime)) {
            return !currentTime.isBefore(startTime) && currentTime.isBefore(endTime);
        } else if (startTime.isAfter(endTime)) {
            return !currentTime.isBefore(startTime) || currentTime.isBefore(endTime);
        }
        return false;
    }

    public boolean shouldAllowMention(Long userId) {
        return dndSettingsRepository.findByUserId(userId)
                .map(s -> !isInDndPeriod(userId) || s.getAllowMentions())
                .orElse(true);
    }

    public boolean shouldAllowStarred(Long userId) {
        return dndSettingsRepository.findByUserId(userId)
                .map(s -> !isInDndPeriod(userId) || s.getAllowStarred())
                .orElse(true);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        dndSettingsRepository.findByUserId(userId)
                .ifPresent(settings -> {
                    dndSettingsRepository.delete(settings);
                    log.info("DND settings deleted for user: {}", userId);
                });
    }

    private DndSettingsResponse toResponse(DndSettings settings) {
        return DndSettingsResponse.builder()
                .id(settings.getId())
                .userId(settings.getUserId())
                .enabled(settings.getEnabled())
                .startTime(settings.getStartTime())
                .endTime(settings.getEndTime())
                .timezone(settings.getTimezone())
                .repeatDays(settings.getRepeatDays())
                .allowMentions(settings.getAllowMentions())
                .allowStarred(settings.getAllowStarred())
                .customMessage(settings.getCustomMessage())
                .createdAt(settings.getCreatedAt())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}
