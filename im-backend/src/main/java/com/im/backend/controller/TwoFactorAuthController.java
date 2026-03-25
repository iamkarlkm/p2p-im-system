package com.im.backend.controller;

import com.im.backend.dto.*;
import com.im.backend.service.TwoFactorAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;

    @PostMapping("/setup")
    public ResponseEntity<TwoFactorSetupResponse> setup2FA(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody TwoFactorSetupRequest request) {
        return ResponseEntity.ok(twoFactorAuthService.setup2FA(userId, request));
    }

    @PostMapping("/verify")
    public ResponseEntity<TwoFactorVerifyResponse> verify2FA(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody TwoFactorVerifyRequest request) {
        return ResponseEntity.ok(twoFactorAuthService.verify2FA(userId, request));
    }

    @PostMapping("/enable")
    public ResponseEntity<TwoFactorSetupResponse> enable2FA(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, String> body) {
        String code = body.get("code");
        return ResponseEntity.ok(twoFactorAuthService.enable2FA(userId, code));
    }

    @PostMapping("/disable")
    public ResponseEntity<Map<String, String>> disable2FA(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, String> body) {
        String password = body.get("password");
        String code = body.get("code");
        twoFactorAuthService.disable2FA(userId, password, code);
        return ResponseEntity.ok(Map.of("message", "2FA has been disabled"));
    }

    @PostMapping("/backup-codes/regenerate")
    public ResponseEntity<List<String>> regenerateBackupCodes(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, String> body) {
        String code = body.get("code");
        return ResponseEntity.ok(twoFactorAuthService.regenerateBackupCodes(userId, code));
    }

    @GetMapping("/status")
    public ResponseEntity<TwoFactorStatusResponse> getStatus(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(twoFactorAuthService.getStatus(userId));
    }

    @GetMapping("/check/{userId}")
    public ResponseEntity<Map<String, Boolean>> check2FARequired(@PathVariable Long userId) {
        boolean required = twoFactorAuthService.is2FAEnabled(userId);
        return ResponseEntity.ok(Map.of("required", required));
    }
}
