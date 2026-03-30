package com.im.backend.repository;

import com.im.backend.model.entity.VoiceMessage;
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
 * 语音消息数据访问层
 */
@Repository
public interface VoiceMessageRepository extends JpaRepository<VoiceMessage, Long> {
    
    Optional<VoiceMessage> findByMessageId(String messageId);
    
    List<VoiceMessage> findBySenderIdAndReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId);
    
    List<VoiceMessage> findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(Long receiverId);
    
    Page<VoiceMessage> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId, Pageable pageable);
    
    @Query("SELECT vm FROM VoiceMessage vm WHERE vm.groupId = :groupId ORDER BY vm.createdAt DESC")
    List<VoiceMessage> findByGroupIdOrderByCreatedAtDesc(@Param("groupId") Long groupId);
    
    @Modifying
    @Query("UPDATE VoiceMessage vm SET vm.isRead = true, vm.updatedAt = :now WHERE vm.id = :id")
    int markAsRead(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE VoiceMessage vm SET vm.isPlayed = true, vm.playCount = vm.playCount + 1, vm.updatedAt = :now WHERE vm.id = :id")
    int recordPlay(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(vm) FROM VoiceMessage vm WHERE vm.receiverId = :receiverId AND vm.isRead = false")
    long countUnreadByReceiverId(@Param("receiverId") Long receiverId);
    
    List<VoiceMessage> findByVoiceFileId(String voiceFileId);
}
