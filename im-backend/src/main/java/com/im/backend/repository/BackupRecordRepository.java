package com.im.backend.repository;

import com.im.backend.entity.BackupRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BackupRecordRepository extends JpaRepository<BackupRecord, Long> {

    List<BackupRecord> findByStrategyIdOrderByStartTimeDesc(Long strategyId);

    List<BackupRecord> findByStatus(String status);

    List<BackupRecord> findByComponent(String component);

    @Query("SELECT br FROM BackupRecord br WHERE br.status = 'SUCCESS' AND br.strategyId = :strategyId ORDER BY br.startTime DESC")
    List<BackupRecord> findSuccessfulBackupsByStrategy(Long strategyId);

    @Query("SELECT br FROM BackupRecord br WHERE br.startTime >= :since ORDER BY br.startTime DESC")
    List<BackupRecord> findBackupsSince(LocalDateTime since);

    @Query("SELECT br FROM BackupRecord br WHERE br.strategyId = :strategyId AND br.status = 'SUCCESS' ORDER BY br.startTime DESC LIMIT 1")
    Optional<BackupRecord> findLatestSuccessfulByStrategy(Long strategyId);

    long countByStatus(String status);

    long countByStrategyId(Long strategyId);

    long countByStrategyIdAndStatus(Long strategyId, String status);

    @Query("SELECT SUM(br.fileSize) FROM BackupRecord br WHERE br.status = 'SUCCESS'")
    Long getTotalStorageUsed();

    void deleteByStrategyId(Long strategyId);

    void deleteByStartTimeBefore(LocalDateTime before);
}
