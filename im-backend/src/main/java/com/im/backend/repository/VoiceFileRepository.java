package com.im.backend.repository;

import com.im.backend.model.entity.VoiceFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 语音文件数据访问层
 */
@Repository
public interface VoiceFileRepository extends JpaRepository<VoiceFile, Long> {
    
    Optional<VoiceFile> findByFileId(String fileId);
    
    List<VoiceFile> findByUploaderIdOrderByCreatedAtDesc(Long uploaderId);
    
    Page<VoiceFile> findByUploaderIdAndIsDeletedFalseOrderByCreatedAtDesc(Long uploaderId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE VoiceFile vf SET vf.isDeleted = true WHERE vf.fileId = :fileId")
    int softDeleteByFileId(@Param("fileId") String fileId);
    
    @Query("SELECT vf FROM VoiceFile vf WHERE vf.isDeleted = false AND vf.expiredAt < :now")
    List<VoiceFile> findExpiredFiles(@Param("now") LocalDateTime now);
    
    @Query("SELECT SUM(vf.fileSize) FROM VoiceFile vf WHERE vf.uploaderId = :uploaderId AND vf.isDeleted = false")
    Long sumFileSizeByUploaderId(@Param("uploaderId") Long uploaderId);
    
    List<VoiceFile> findByFormatAndIsDeletedFalse(String format);
}
