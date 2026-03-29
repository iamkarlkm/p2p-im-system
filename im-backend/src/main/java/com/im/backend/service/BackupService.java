package com.im.backend.service;

import com.im.backend.dto.BackupRequest;
import com.im.backend.dto.BackupResponse;
import com.im.backend.entity.BackupRecord;
import com.im.backend.entity.BackupStrategy;
import com.im.backend.repository.BackupRecordRepository;
import com.im.backend.repository.BackupStrategyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.*;

@Service
public class BackupService {

    private final BackupStrategyRepository strategyRepository;
    private final BackupRecordRepository recordRepository;

    public BackupService(BackupStrategyRepository strategyRepository,
                         BackupRecordRepository recordRepository) {
        this.strategyRepository = strategyRepository;
        this.recordRepository = recordRepository;
    }

    public List<BackupResponse> getAllStrategies() {
        List<BackupStrategy> strategies = strategyRepository.findAll();
        List<BackupResponse> responses = new ArrayList<>();
        for (BackupStrategy s : strategies) {
            responses.add(toStrategyResponse(s));
        }
        return responses;
    }

    public BackupResponse getStrategy(Long id) {
        BackupStrategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Backup strategy not found: " + id));
        return toStrategyResponse(strategy);
    }

    @Transactional
    public BackupResponse createStrategy(BackupRequest request) {
        if (strategyRepository.existsByName(request.getName())) {
            throw new RuntimeException("Strategy with name already exists: " + request.getName());
        }
        BackupStrategy strategy = new BackupStrategy();
        strategy.setName(request.getName());
        strategy.setComponent(request.getComponent() != null ? request.getComponent() : "FULL");
        strategy.setType(request.getType() != null ? request.getType() : "FULL");
        strategy.setStorageType(request.getStorageType() != null ? request.getStorageType() : "LOCAL");
        strategy.setStoragePath(request.getStoragePath() != null ? request.getStoragePath() : "/backup");
        strategy.setCronExpression(request.getCronExpression());
        strategy.setRetentionDays(request.getRetentionDays() != null ? request.getRetentionDays() : 30);
        strategy.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        strategy.setEncryptionEnabled(request.getEncryptionEnabled() != null ? request.getEncryptionEnabled() : false);
        strategy.setCompressionType(request.getCompressionType() != null ? request.getCompressionType() : "GZIP");
        strategy.setMaxBackupSize(request.getMaxBackupSize());
        strategy.setMaxConcurrentBackups(request.getMaxConcurrentBackups());
        strategy.setDescription(request.getDescription());
        strategy = strategyRepository.save(strategy);
        return toStrategyResponse(strategy);
    }

    @Transactional
    public BackupResponse updateStrategy(Long id, BackupRequest request) {
        BackupStrategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Backup strategy not found: " + id));
        if (request.getName() != null) strategy.setName(request.getName());
        if (request.getComponent() != null) strategy.setComponent(request.getComponent());
        if (request.getType() != null) strategy.setType(request.getType());
        if (request.getStorageType() != null) strategy.setStorageType(request.getStorageType());
        if (request.getStoragePath() != null) strategy.setStoragePath(request.getStoragePath());
        if (request.getCronExpression() != null) strategy.setCronExpression(request.getCronExpression());
        if (request.getRetentionDays() != null) strategy.setRetentionDays(request.getRetentionDays());
        if (request.getEnabled() != null) strategy.setEnabled(request.getEnabled());
        if (request.getEncryptionEnabled() != null) strategy.setEncryptionEnabled(request.getEncryptionEnabled());
        if (request.getCompressionType() != null) strategy.setCompressionType(request.getCompressionType());
        if (request.getMaxBackupSize() != null) strategy.setMaxBackupSize(request.getMaxBackupSize());
        if (request.getMaxConcurrentBackups() != null) strategy.setMaxConcurrentBackups(request.getMaxConcurrentBackups());
        if (request.getDescription() != null) strategy.setDescription(request.getDescription());
        strategy = strategyRepository.save(strategy);
        return toStrategyResponse(strategy);
    }

    @Transactional
    public void deleteStrategy(Long id) {
        if (!strategyRepository.existsById(id)) {
            throw new RuntimeException("Backup strategy not found: " + id);
        }
        strategyRepository.deleteById(id);
    }

    @Transactional
    public BackupResponse triggerBackup(Long strategyId) {
        BackupStrategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new RuntimeException("Backup strategy not found: " + strategyId));

        BackupRecord record = new BackupRecord();
        record.setStrategyId(strategyId);
        record.setStrategyName(strategy.getName());
        record.setComponent(strategy.getComponent());
        record.setType(strategy.getType());
        record.setStatus("RUNNING");
        record.setStartTime(LocalDateTime.now());
        record.setEncrypted(strategy.getEncryptionEnabled());
        record.setCompressionType(strategy.getCompressionType());
        record = recordRepository.save(record);

        try {
            String backupFileName = generateBackupFileName(strategy);
            String backupPath = strategy.getStoragePath() + "/" + backupFileName;
            Path path = Paths.get(backupPath);
            Files.createDirectories(path.getParent());

            byte[] backupData = performBackup(strategy);
            byte[] compressedData = compressData(backupData, strategy.getCompressionType());
            Files.write(path, compressedData);

            long fileSize = compressedData.length;
            String checksum = calculateChecksum(compressedData);

            record.setFileName(backupFileName);
            record.setFilePath(backupPath);
            record.setFileSize(fileSize);
            record.setChecksum(checksum);
            record.setChecksumAlgorithm("SHA256");
            record.setStatus("SUCCESS");
            record.setEndTime(LocalDateTime.now());
            record.setDurationMs(System.currentTimeMillis() - record.getStartTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
            record.setRecordCount(estimateRecordCount(strategy.getComponent()));
            record = recordRepository.save(record);

            return toRecordResponse(record, strategy);
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setErrorMessage(e.getMessage());
            record.setEndTime(LocalDateTime.now());
            record.setDurationMs(System.currentTimeMillis() - record.getStartTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
            recordRepository.save(record);
            throw new RuntimeException("Backup failed: " + e.getMessage(), e);
        }
    }

    public List<BackupResponse> getBackupRecords(Long strategyId) {
        List<BackupRecord> records;
        if (strategyId != null) {
            records = recordRepository.findByStrategyIdOrderByStartTimeDesc(strategyId);
        } else {
            records = recordRepository.findAll();
        }
        List<BackupResponse> responses = new ArrayList<>();
        for (BackupRecord r : records) {
            BackupStrategy strategy = strategyRepository.findById(r.getStrategyId()).orElse(null);
            responses.add(toRecordResponse(r, strategy));
        }
        return responses;
    }

    public BackupResponse getBackupRecord(Long recordId) {
        BackupRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Backup record not found: " + recordId));
        BackupStrategy strategy = strategyRepository.findById(record.getStrategyId()).orElse(null);
        return toRecordResponse(record, strategy);
    }

    @Transactional
    public void deleteBackupRecord(Long recordId) {
        BackupRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Backup record not found: " + recordId));
        if (record.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(record.getFilePath()));
            } catch (IOException ignored) {}
        }
        recordRepository.deleteById(recordId);
    }

    public Map<String, Object> getBackupStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStrategies", strategyRepository.count());
        stats.put("activeStrategies", strategyRepository.countByEnabled(true));
        stats.put("totalRecords", recordRepository.count());
        stats.put("successfulBackups", recordRepository.countByStatus("SUCCESS"));
        stats.put("failedBackups", recordRepository.countByStatus("FAILED"));
        stats.put("runningBackups", recordRepository.countByStatus("RUNNING"));
        stats.put("totalStorageUsed", recordRepository.getTotalStorageUsed());
        return stats;
    }

    // --- Private helper methods ---

    private byte[] performBackup(BackupStrategy strategy) {
        String component = strategy.getComponent();
        StringBuilder data = new StringBuilder();
        data.append("=== BACKUP DATA ===\n");
        data.append("Component: ").append(component).append("\n");
        data.append("Type: ").append(strategy.getType()).append("\n");
        data.append("Time: ").append(LocalDateTime.now()).append("\n");
        data.append("Strategy ID: ").append(strategy.getId()).append("\n");

        switch (component) {
            case "MESSAGE":
                data.append(getMessageBackupData());
                break;
            case "USER":
                data.append(getUserBackupData());
                break;
            case "FILE":
                data.append(getFileBackupData());
                break;
            case "FULL":
            default:
                data.append(getMessageBackupData());
                data.append(getUserBackupData());
                data.append(getFileBackupData());
                break;
        }
        return data.toString().getBytes();
    }

    private String getMessageBackupData() {
        return "[MESSAGE_BACKUP] Placeholder message data snapshot. " +
               "In production, this would export: messages, conversations, attachments metadata.\n";
    }

    private String getUserBackupData() {
        return "[USER_BACKUP] Placeholder user data snapshot. " +
               "In production, this would export: users, contacts, profiles, settings.\n";
    }

    private String getFileBackupData() {
        return "[FILE_BACKUP] Placeholder file metadata snapshot. " +
               "In production, this would export: file references, metadata, CDN URLs.\n";
    }

    private byte[] compressData(byte[] data, String compressionType) throws IOException {
        if ("GZIP".equals(compressionType)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
                gzos.write(data);
            }
            return baos.toByteArray();
        } else if ("ZIP".equals(compressionType)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                ZipEntry entry = new ZipEntry("backup.dat");
                zos.putNextEntry(entry);
                zos.write(data);
                zos.closeEntry();
            }
            return baos.toByteArray();
        }
        return data;
    }

    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private String generateBackupFileName(BackupStrategy strategy) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String ext = "GZIP".equals(strategy.getCompressionType()) ? ".gz" : ".zip";
        return String.format("backup_%s_%s_%s%s",
                strategy.getComponent().toLowerCase(),
                strategy.getType().toLowerCase(),
                timestamp,
                ext);
    }

    private Integer estimateRecordCount(String component) {
        return 1000;
    }

    private BackupResponse toStrategyResponse(BackupStrategy s) {
        BackupResponse r = new BackupResponse();
        r.setId(s.getId());
        r.setName(s.getName());
        r.setComponent(s.getComponent());
        r.setType(s.getType());
        r.setStorageType(s.getStorageType());
        r.setStoragePath(s.getStoragePath());
        r.setCronExpression(s.getCronExpression());
        r.setRetentionDays(s.getRetentionDays());
        r.setEnabled(s.getEnabled());
        r.setEncryptionEnabled(s.getEncryptionEnabled());
        r.setCompressionType(s.getCompressionType());
        r.setMaxBackupSize(s.getMaxBackupSize());
        r.setMaxConcurrentBackups(s.getMaxConcurrentBackups());
        r.setDescription(s.getDescription());
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());

        long total = recordRepository.countByStrategyId(s.getId());
        long success = recordRepository.countByStrategyIdAndStatus(s.getId(), "SUCCESS");
        long failed = recordRepository.countByStrategyIdAndStatus(s.getId(), "FAILED");
        r.setTotalBackups(total);
        r.setSuccessfulBackups(success);
        r.setFailedBackups(failed);
        return r;
    }

    private BackupResponse toRecordResponse(BackupRecord r, BackupStrategy s) {
        BackupResponse resp = new BackupResponse();
        if (s != null) {
            resp = toStrategyResponse(s);
        }
        resp.setStatus(r.getStatus());
        resp.setStartTime(r.getStartTime());
        resp.setEndTime(r.getEndTime());
        resp.setDurationMs(r.getDurationMs());
        resp.setFileSize(r.getFileSize());
        resp.setRecordCount(r.getRecordCount());
        resp.setErrorMessage(r.getErrorMessage());
        resp.setFileName(r.getFileName());
        resp.setChecksum(r.getChecksum());
        return resp;
    }
}
