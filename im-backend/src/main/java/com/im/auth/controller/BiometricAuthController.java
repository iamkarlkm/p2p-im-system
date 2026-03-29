package com.im.auth.controller;

import com.im.auth.dto.*;
import com.im.auth.entity.BiometricAuthEntity;
import com.im.auth.service.BiometricAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/biometric-auth")
public class BiometricAuthController {
    
    @Autowired
    private BiometricAuthService biometricAuthService;
    
    // Registration endpoints
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<BiometricAuthEntity>> registerBiometric(
            @RequestBody BiometricRegistrationRequest request) {
        try {
            BiometricAuthEntity entity = biometricAuthService.registerBiometric(request);
            return ResponseEntity.ok(ApiResponse.success("Biometric registered successfully", entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to register biometric: " + e.getMessage()));
        }
    }
    
    @PostMapping("/fido2/register")
    public ResponseEntity<ApiResponse<Fido2RegistrationResponse>> registerFido2(
            @RequestBody Fido2RegistrationRequest request) {
        try {
            // In real implementation, this would create FIDO2 registration options
            BiometricRegistrationRequest regRequest = convertToRegistrationRequest(request);
            BiometricAuthEntity entity = biometricAuthService.registerBiometric(regRequest);
            
            Fido2RegistrationResponse response = new Fido2RegistrationResponse();
            response.setCredentialId(entity.getCredentialId());
            response.setPublicKey(entity.getPublicKey());
            response.setTransports(entity.getTransports());
            
            return ResponseEntity.ok(ApiResponse.success("FIDO2 credential registered", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("FIDO2 registration failed: " + e.getMessage()));
        }
    }
    
    // Authentication endpoints
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResult>> authenticate(
            @RequestBody BiometricAuthenticationRequest request) {
        try {
            AuthenticationResult result = biometricAuthService.authenticate(request);
            if (result.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("Authentication successful", result));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(result.getErrorMessage(), result));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Authentication failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/fido2/authenticate")
    public ResponseEntity<ApiResponse<Fido2AuthenticationResponse>> authenticateFido2(
            @RequestBody Fido2AuthenticationRequest request) {
        try {
            BiometricAuthenticationRequest authRequest = convertToAuthenticationRequest(request);
            AuthenticationResult result = biometricAuthService.authenticate(authRequest);
            
            Fido2AuthenticationResponse response = new Fido2AuthenticationResponse();
            response.setSuccess(result.isSuccess());
            response.setUserId(result.getUserId());
            response.setWarning(result.getWarning());
            
            return ResponseEntity.ok(ApiResponse.success("FIDO2 authentication completed", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("FIDO2 authentication failed: " + e.getMessage()));
        }
    }
    
    // Management endpoints
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BiometricAuthEntity>>> getUserBiometrics(
            @PathVariable UUID userId) {
        try {
            List<BiometricAuthEntity> biometrics = biometricAuthService.getUserBiometrics(userId);
            return ResponseEntity.ok(ApiResponse.success("User biometrics retrieved", biometrics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve biometrics: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}/enabled")
    public ResponseEntity<ApiResponse<List<BiometricAuthEntity>>> getEnabledUserBiometrics(
            @PathVariable UUID userId) {
        try {
            List<BiometricAuthEntity> biometrics = biometricAuthService.getEnabledUserBiometrics(userId);
            return ResponseEntity.ok(ApiResponse.success("Enabled biometrics retrieved", biometrics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve enabled biometrics: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BiometricAuthEntity>> getBiometricById(
            @PathVariable UUID id) {
        try {
            return biometricAuthService.getBiometricById(id)
                    .map(entity -> ResponseEntity.ok(ApiResponse.success("Biometric found", entity)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Biometric not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve biometric: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<Void>> enableBiometric(
            @PathVariable UUID id,
            @RequestParam boolean enable) {
        try {
            boolean success = biometricAuthService.enableBiometric(id, enable);
            if (success) {
                String message = enable ? "Biometric enabled" : "Biometric disabled";
                return ResponseEntity.ok(ApiResponse.success(message));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Biometric not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update biometric: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBiometric(@PathVariable UUID id) {
        try {
            boolean success = biometricAuthService.deleteBiometric(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("Biometric deleted"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Biometric not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete biometric: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<DeleteCountResponse>> deleteUserBiometrics(
            @PathVariable UUID userId) {
        try {
            int count = biometricAuthService.deleteUserBiometrics(userId);
            DeleteCountResponse response = new DeleteCountResponse(count);
            return ResponseEntity.ok(ApiResponse.success("User biometrics deleted", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete user biometrics: " + e.getMessage()));
        }
    }
    
    // Security endpoints
    @GetMapping("/user/{userId}/cloned")
    public ResponseEntity<ApiResponse<List<BiometricAuthEntity>>> getClonedBiometrics(
            @PathVariable UUID userId) {
        try {
            List<BiometricAuthEntity> cloned = biometricAuthService.getClonedBiometrics(userId);
            return ResponseEntity.ok(ApiResponse.success("Cloned biometrics retrieved", cloned));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve cloned biometrics: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/clone-warning")
    public ResponseEntity<ApiResponse<Void>> markAsCloned(
            @PathVariable UUID id,
            @RequestParam boolean cloned) {
        try {
            biometricAuthService.markAsCloned(id, cloned);
            String message = cloned ? "Marked as cloned" : "Clone warning cleared";
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update clone warning: " + e.getMessage()));
        }
    }
    
    @PutMapping("/user/{userId}/disable-all")
    public ResponseEntity<ApiResponse<Void>> disableAllUserBiometrics(@PathVariable UUID userId) {
        try {
            biometricAuthService.disableAllUserBiometrics(userId);
            return ResponseEntity.ok(ApiResponse.success("All user biometrics disabled"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to disable biometrics: " + e.getMessage()));
        }
    }
    
    // Statistics endpoints
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<CountResponse>> countUserBiometrics(@PathVariable UUID userId) {
        try {
            long count = biometricAuthService.countEnabledBiometrics(userId);
            CountResponse response = new CountResponse(count);
            return ResponseEntity.ok(ApiResponse.success("Biometric count retrieved", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to count biometrics: " + e.getMessage()));
        }
    }
    
    @GetMapping("/count/total")
    public ResponseEntity<ApiResponse<CountResponse>> countTotalBiometrics() {
        try {
            long count = biometricAuthService.countTotalEnabledBiometrics();
            CountResponse response = new CountResponse(count);
            return ResponseEntity.ok(ApiResponse.success("Total biometric count retrieved", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to count total biometrics: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}/types")
    public ResponseEntity<ApiResponse<List<String>>> getUserBiometricTypes(@PathVariable UUID userId) {
        try {
            List<String> types = biometricAuthService.getUserBiometricTypes(userId);
            return ResponseEntity.ok(ApiResponse.success("Biometric types retrieved", types));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve biometric types: " + e.getMessage()));
        }
    }
    
    // Backup endpoints
    @PutMapping("/{id}/backup-state")
    public ResponseEntity<ApiResponse<Void>> updateBackupState(
            @PathVariable UUID id,
            @RequestParam boolean backupState) {
        try {
            biometricAuthService.updateBackupState(id, backupState);
            String message = backupState ? "Backup state enabled" : "Backup state disabled";
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update backup state: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}/backed-up")
    public ResponseEntity<ApiResponse<List<BiometricAuthEntity>>> getBackedUpBiometrics(
            @PathVariable UUID userId) {
        try {
            List<BiometricAuthEntity> backedUp = biometricAuthService.getBackedUpBiometrics(userId);
            return ResponseEntity.ok(ApiResponse.success("Backed up biometrics retrieved", backedUp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve backed up biometrics: " + e.getMessage()));
        }
    }
    
    // FIDO2 specific endpoints
    @GetMapping("/fido2/credential/{credentialId}")
    public ResponseEntity<ApiResponse<BiometricAuthEntity>> getFido2Credential(
            @PathVariable String credentialId) {
        try {
            return biometricAuthService.getCredentialById(credentialId)
                    .map(entity -> ResponseEntity.ok(ApiResponse.success("FIDO2 credential found", entity)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("FIDO2 credential not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve FIDO2 credential: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}/fido2/resident-keys")
    public ResponseEntity<ApiResponse<List<BiometricAuthEntity>>> getResidentKeyCredentials(
            @PathVariable UUID userId) {
        try {
            List<BiometricAuthEntity> residentKeys = biometricAuthService.getResidentKeyCredentials(userId);
            return ResponseEntity.ok(ApiResponse.success("Resident key credentials retrieved", residentKeys));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve resident key credentials: " + e.getMessage()));
        }
    }
    
    // Health check
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<HealthCheckResponse>> healthCheck() {
        try {
            long totalCount = biometricAuthService.countTotalEnabledBiometrics();
            HealthCheckResponse response = new HealthCheckResponse("OK", totalCount);
            return ResponseEntity.ok(ApiResponse.success("Service is healthy", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Service is unhealthy: " + e.getMessage()));
        }
    }
    
    // Helper conversion methods
    private BiometricRegistrationRequest convertToRegistrationRequest(Fido2RegistrationRequest fido2Request) {
        BiometricRegistrationRequest request = new BiometricRegistrationRequest();
        request.setUserId(fido2Request.getUserId());
        request.setDeviceId(fido2Request.getDeviceId());
        request.setBiometricType("FIDO2");
        request.setPublicKey(fido2Request.getPublicKey());
        request.setKeyHandle(fido2Request.getKeyHandle());
        request.setCredentialId(fido2Request.getCredentialId());
        request.setDeviceName(fido2Request.getDeviceName());
        request.setSecurityLevel("HIGH");
        request.setAttestationStatement(fido2Request.getAttestationStatement());
        request.setBackupEligible(fido2Request.isBackupEligible());
        request.setBackupState(fido2Request.isBackupState());
        request.setFlags(fido2Request.getFlags());
        request.setRpId(fido2Request.getRpId());
        request.setOrigin(fido2Request.getOrigin());
        request.setTransports(fido2Request.getTransports());
        request.setUserVerificationRequired(fido2Request.isUserVerificationRequired());
        request.setResidentKeyRequired(fido2Request.isResidentKeyRequired());
        return request;
    }
    
    private BiometricAuthenticationRequest convertToAuthenticationRequest(Fido2AuthenticationRequest fido2Request) {
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest();
        request.setCredentialId(fido2Request.getCredentialId());
        request.setSignature(fido2Request.getSignature());
        request.setChallenge(fido2Request.getChallenge());
        return request;
    }
}