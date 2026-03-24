package com.im.backend.repository;

import com.im.backend.entity.ConversationBatchOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationBatchOperationRepository extends JpaRepository<ConversationBatchOperation, Long> {
    
    List<ConversationBatchOperation> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<ConversationBatchOperation> findByStatusOrderByCreatedAtDesc(String status);
    
    List<ConversationBatchOperation> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
}
