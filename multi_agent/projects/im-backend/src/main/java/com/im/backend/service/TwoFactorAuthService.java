package com.im.backend.service;

import com.im.backend.dto.*;
import com.im.backend.entity.TwoFactorAuth;
import com.im.backend.entity.User;
import com.im.backend.repository.TwoFactorAuthRepository;
import com.im.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwoFactorAuthService {

    private final TwoFactorAuthRepository twoFactorAuthRepository;
    private final UserRepository userRepository;

    private static final String ALGORITHM = "HmacSHA1";
    private static final int TIME_STEP = 30;
    private static final int CODE_DIGITS = 6;
    private static final int BACKUP_CODE_COUNT = 10;

    @Transactional
    public TwoFactorSetupResponse setup2FA(Long userId, TwoFactorSetupRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String secret = generateSecret();
        String issuer = request.getIssuerName() != null ? request.getIssuerName() : "IMSystem";
        String account = request.getAccountName() != null ? request.getAccountName() : user.getUsername();
        String provisioningUri = generateProvisioningUri(secret, issuer, account);

        List<String> backupCodes = generateBackupCodes();

        TwoFactorAuth tfa = TwoFactorAuth.builder()
                .userId(userId)
                .secret(secret)
                .qrCodeUrl(provisioningUri)
                .issuerName(issuer)
                .accountName(account)
                .isEnabled(false)
                .isVerified(false)
                .backupCodes(backupCodes)
                .backupCodesUsed(0)
                .build();

        twoFactorAuthRepository.save(tfa);

        String manualEntryKey = formatManualEntryKey(secret);

        return TwoFactorSetupResponse.builder()
                .secret(secret)
                .qrCodeUrl(provisioningUri)
                .manualEntryKey(manualEntryKey)
                .backupCodes(backupCodes)
                .provisioningUri(provisioningUri)
                .appInfo(Map.of(
                        "name", issuer,
                        "account", account,
                        "algorithm", "SHA1",
                        "digits", CODE_DIGITS,
                        "period", TIME_STEP
                ))
                .build();
    }

    @Transactional
    public TwoFactorVerifyResponse verify2FA(Long userId, TwoFactorVerifyRequest request) {
        TwoFactorAuth tfa = twoFactorAuthRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("2FA not configured"));

        if (!tfa.getIsEnabled()) {
            throw new RuntimeException("2FA is not enabled");
        }

        boolean isValid;
        boolean isBackupCode = Boolean.TRUE.equals(request.getIsBackupCode());

        if (isBackupCode) {
            isValid = verifyBackupCode(tfa, request.getCode());
        } else {
            isValid = verifyTOTPCode(tfa.getSecret(), request.getCode());
        }

        if (isValid) {
            tfa.setLastVerifiedAt(LocalDateTime.now());
            tfa.setIsVerified(true);
            if (isBackupCode) {
                tfa.setBackupCodesUsed(tfa.getBackupCodesUsed() + 1);
            }
            twoFactorAuthRepository.save(tfa);

            String token = generate2FAToken(userId, request.getDeviceName());
            int remainingCodes = tfa.getBackupCodes().size() - tfa.getBackupCodesUsed();

            return TwoFactorVerifyResponse.builder()
                    .success(true)
                    .token(token)
                    .remainingBackupCodes(remainingCodes)
                    .message("Verification successful")
                    .expiresIn(3600L)
                    .build();
        } else {
            return TwoFactorVerifyResponse.builder()
                    .success(false)
                    .message("Invalid code")
                    .expiresIn(0L)
                    .build();
        }
    }

    @Transactional
    public TwoFactorSetupResponse enable2FA(Long userId, String code) {
        TwoFactorAuth tfa = twoFactorAuthRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("2FA not configured"));

        if (!verifyTOTPCode(tfa.getSecret(), code)) {
            throw new RuntimeException("Invalid verification code");
        }

        tfa.setIsEnabled(true);
        tfa.setIsVerified(true);
        tfa.setLastVerifiedAt(LocalDateTime.now());
        twoFactorAuthRepository.save(tfa);

        return TwoFactorSetupResponse.builder()
                .secret(tfa.getSecret())
                .qrCodeUrl(tfa.getQrCodeUrl())
                .manualEntryKey(formatManualEntryKey(tfa.getSecret()))
                .provisioningUri(tfa.getQrCodeUrl())
                .build();
    }

    @Transactional
    public void disable2FA(Long userId, String password, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        TwoFactorAuth tfa = twoFactorAuthRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("2FA not configured"));

        if (!verifyTOTPCode(tfa.getSecret(), code)) {
            throw new RuntimeException("Invalid 2FA code");
        }

        twoFactorAuthRepository.delete(tfa);
    }

    @Transactional
    public List<String> regenerateBackupCodes(Long userId, String code) {
        TwoFactorAuth tfa = twoFactorAuthRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("2FA not configured"));

        if (!verifyTOTPCode(tfa.getSecret(), code)) {
            throw new RuntimeException("Invalid verification code");
        }

        List<String> newCodes = generateBackupCodes();
        tfa.setBackupCodes(newCodes);
        tfa.setBackupCodesUsed(0);
        twoFactorAuthRepository.save(tfa);

        return newCodes;
    }

    public TwoFactorStatusResponse getStatus(Long userId) {
        TwoFactorAuth tfa = twoFactorAuthRepository.findByUserId(userId).orElse(null);

        if (tfa == null) {
            return TwoFactorStatusResponse.builder()
                    .isEnabled(false)
                    .isVerified(false)
                    .backupCodesRemaining(0)
                    .isRequired(false)
                    .build();
        }

        return TwoFactorStatusResponse.builder()
                .isEnabled(tfa.getIsEnabled())
                .isVerified(tfa.getIsVerified())
                .backupCodesRemaining(tfa.getBackupCodes().size() - tfa.getBackupCodesUsed())
                .lastVerifiedAt(tfa.getLastVerifiedAt() != null ?
                        tfa.getLastVerifiedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .issuerName(tfa.getIssuerName())
                .accountName(tfa.getAccountName())
                .isRequired(false)
                .build();
    }

    public boolean is2FAEnabled(Long userId) {
        return twoFactorAuthRepository.findByUserId(userId)
                .map(TwoFactorAuth::getIsEnabled)
                .orElse(false);
    }

    private String generateSecret() {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        return Base32.encode(bytes);
    }

    private String generateProvisioningUri(String secret, String issuer, String account) {
        String encodedIssuer = issuer.replace(":", "");
        String encodedAccount = account.replace(":", "");
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=%d&period=%d",
                encodedIssuer, encodedAccount, secret, encodedIssuer, CODE_DIGITS, TIME_STEP
        );
    }

    private String formatManualEntryKey(String secret) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < secret.length(); i++) {
            sb.append(secret.charAt(i));
            if ((i + 1) % 4 == 0 && i < secret.length() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private boolean verifyTOTPCode(String secret, String code) {
        try {
            long counter = Instant.now().getEpochSecond() / TIME_STEP;
            for (int i = -1; i <= 1; i++) {
                String expectedCode = generateTOTP(secret, counter + i);
                if (expectedCode.equals(code)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Error verifying TOTP code", e);
            return false;
        }
    }

    private String generateTOTP(String secret, long counter) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] key = Base32.decode(secret);
        byte[] data = ByteBuffer.allocate(8).putLong(counter).array();
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(key, ALGORITHM));
        byte[] hash = mac.doFinal(data);
        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24) |
                ((hash[offset + 1] & 0xFF) << 16) |
                ((hash[offset + 2] & 0xFF) << 8) |
                (hash[offset + 3] & 0xFF);
        int otp = binary % (int) Math.pow(10, CODE_DIGITS);
        return String.format("%0" + CODE_DIGITS + "d", otp);
    }

    private boolean verifyBackupCode(TwoFactorAuth tfa, String code) {
        List<String> codes = tfa.getBackupCodes();
        if (codes == null || codes.isEmpty()) return false;
        for (int i = 0; i < codes.size(); i++) {
            if (codes.get(i).equals(code)) {
                codes.set(i, "USED:" + code);
                return true;
            }
        }
        return false;
    }

    private List<String> generateBackupCodes() {
        List<String> codes = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < BACKUP_CODE_COUNT; i++) {
            int code = random.nextInt(100000000);
            codes.add(String.format("%08d", code));
        }
        return codes;
    }

    private String generate2FAToken(Long userId, String deviceName) {
        String data = userId + ":" + (deviceName != null ? deviceName : "unknown") + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    static class Base32 {
        private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

        public static String encode(byte[] data) {
            StringBuilder sb = new StringBuilder();
            int bits = 0;
            int value = 0;
            for (byte b : data) {
                value = (value << 8) | (b & 0xFF);
                bits += 8;
                while (bits >= 5) {
                    sb.append(ALPHABET.charAt((value >>> (bits - 5)) & 31));
                    bits -= 5;
                }
            }
            if (bits > 0) {
                sb.append(ALPHABET.charAt((value << (5 - bits)) & 31));
            }
            return sb.toString();
        }

        public static byte[] decode(String s) {
            s = s.toUpperCase().replaceAll("[^A-Z2-7]", "");
            List<Byte> bits = new ArrayList<>();
            int buffer = 0;
            int bitsLeft = 0;
            for (char c : s.toCharArray()) {
                int v = ALPHABET.indexOf(c);
                if (v < 0) continue;
                buffer = (buffer << 5) | v;
                bitsLeft += 5;
                if (bitsLeft >= 8) {
                    bits.add((byte) ((buffer >>> (bitsLeft - 8)) & 0xFF));
                    bitsLeft -= 8;
                }
            }
            byte[] result = new byte[bits.size()];
            for (int i = 0; i < result.length; i++) result[i] = bits.get(i);
            return result;
        }
    }
}
