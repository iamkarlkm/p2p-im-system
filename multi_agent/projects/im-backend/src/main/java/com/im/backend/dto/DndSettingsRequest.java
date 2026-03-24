package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DndSettingsRequest {

    private Boolean enabled;
    private LocalTime startTime;
    private LocalTime endTime;
    private String timezone;
    private String repeatDays;
    private Boolean allowMentions;
    private Boolean allowStarred;
    private String customMessage;
}
