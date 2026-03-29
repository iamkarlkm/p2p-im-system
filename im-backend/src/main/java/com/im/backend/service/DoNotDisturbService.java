package com.im.backend.service;

import com.im.backend.dto.DoNotDisturbPeriodDTO;
import java.util.List;

public interface DoNotDisturbService {
    
    List<DoNotDisturbPeriodDTO> getUserPeriods(String userId);
    
    DoNotDisturbPeriodDTO createPeriod(String userId, DoNotDisturbPeriodDTO dto);
    
    DoNotDisturbPeriodDTO updatePeriod(String userId, String periodId, DoNotDisturbPeriodDTO dto);
    
    void deletePeriod(String userId, String periodId);
    
    DoNotDisturbPeriodDTO togglePeriod(String userId, String periodId, Boolean isEnabled);
    
    boolean isInDoNotDisturbMode(String userId);
    
    boolean shouldAllowCalls(String userId);
    
    boolean shouldAllowMentions(String userId);
}
