package com.im.backend.repository;

import com.im.backend.entity.ConversationFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationFolderRepository extends JpaRepository<ConversationFolder, Long> {
    List<ConversationFolder> findByUserIdOrderBySortOrder(Long userId);
}
