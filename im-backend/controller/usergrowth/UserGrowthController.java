package com.im.controller.usergrowth;

import com.im.dto.usergrowth.*;
import com.im.service.usergrowth.UserGrowthService;
import com.im.service.usergrowth.UserPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 用户成长与积分系统控制器
 */
@RestController
@RequestMapping("/api/v1/usergrowth")
public class UserGrowthController {

    @Autowired
    private UserGrowthService userGrowthService;
    
    @Autowired
    private UserPointsService userPointsService;

    /**
     * 获取用户等级信息
     */
    @GetMapping("/level/info/{userId}")
    public UserLevelInfoResponseDTO getUserLevelInfo(@PathVariable Long userId) {
        // Implementation placeholder
        return new UserLevelInfoResponseDTO();
    }

    /**
     * 获取用户等级进度
     */
    @GetMapping("/level/progress/{userId}")
    public UserGrowthService.LevelProgressDTO getLevelProgress(@PathVariable Long userId) {
        return userGrowthService.calculateLevelProgress(userId);
    }

    /**
     * 获取所有等级定义
     */
    @GetMapping("/level/definitions")
    public List<com.im.entity.usergrowth.UserLevelDefinition> getLevelDefinitions() {
        return userGrowthService.getAllLevelDefinitions();
    }

    /**
     * 获取用户特权列表
     */
    @GetMapping("/level/privileges/{userId}")
    public List<com.im.entity.usergrowth.UserLevelDefinition.LevelPrivilege> getUserPrivileges(@PathVariable Long userId) {
        return userGrowthService.getUserPrivileges(userId);
    }

    /**
     * 检查用户特权
     */
    @GetMapping("/level/check-privilege")
    public Boolean checkPrivilege(@RequestParam Long userId, @RequestParam String privilegeType) {
        return userGrowthService.hasPrivilege(userId, privilegeType);
    }

    /**
     * 获取积分信息
     */
    @GetMapping("/points/info/{userId}")
    public UserPointsInfoResponseDTO getPointsInfo(@PathVariable Long userId) {
        // Implementation placeholder
        return new UserPointsInfoResponseDTO();
    }

    /**
     * 获取积分流水
     */
    @GetMapping("/points/transactions/{userId}")
    public List<com.im.entity.usergrowth.PointsTransactionLog> getPointsTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false) String transactionType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return userPointsService.getPointsTransactionLogs(userId, transactionType, page, size);
    }

    /**
     * 签到
     */
    @PostMapping("/points/sign-in/{userId}")
    public UserPointsService.SignInResult signIn(@PathVariable Long userId) {
        return userPointsService.signIn(userId);
    }

    /**
     * 获取签到日历
     */
    @GetMapping("/points/sign-in-calendar/{userId}")
    public List<UserPointsService.SignInCalendarItem> getSignInCalendar(
            @PathVariable Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return userPointsService.getSignInCalendar(userId, year, month);
    }

    /**
     * 增加成长值 (内部API)
     */
    @PostMapping("/internal/growth/add")
    public Boolean addGrowthValue(@RequestBody GrowthAddRequest request) {
        return userGrowthService.addGrowthValue(
            request.getUserId(),
            request.getValue(),
            request.getSourceType(),
            request.getSourceDesc(),
            request.getBizType(),
            request.getBizId()
        );
    }

    /**
     * 增加积分 (内部API)
     */
    @PostMapping("/internal/points/add")
    public Boolean addPoints(@RequestBody PointsAddRequest request) {
        return userPointsService.addPoints(
            request.getUserId(),
            request.getPoints(),
            request.getSourceType(),
            request.getSourceDesc(),
            request.getBizType(),
            request.getBizId()
        );
    }

    /**
     * 消耗积分 (内部API)
     */
    @PostMapping("/internal/points/deduct")
    public UserPointsService.PointsDeductResult deductPoints(@RequestBody PointsDeductRequest request) {
        return userPointsService.deductPoints(
            request.getUserId(),
            request.getPoints(),
            request.getSpendType(),
            request.getSourceDesc(),
            request.getBizType(),
            request.getBizId()
        );
    }

    /**
     * 成长值增加请求
     */
    @lombok.Data
    public static class GrowthAddRequest {
        private Long userId;
        private Long value;
        private String sourceType;
        private String sourceDesc;
        private String bizType;
        private Long bizId;
    }

    /**
     * 积分增加请求
     */
    @lombok.Data
    public static class PointsAddRequest {
        private Long userId;
        private Long points;
        private String sourceType;
        private String sourceDesc;
        private String bizType;
        private Long bizId;
    }

    /**
     * 积分消耗请求
     */
    @lombok.Data
    public static class PointsDeductRequest {
        private Long userId;
        private Long points;
        private String spendType;
        private String sourceDesc;
        private String bizType;
        private Long bizId;
    }
}
