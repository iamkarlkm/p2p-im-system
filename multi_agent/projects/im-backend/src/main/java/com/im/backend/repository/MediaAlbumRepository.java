package com.im.backend.repository;

import com.im.backend.entity.MediaAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MediaAlbumRepository extends JpaRepository<MediaAlbum, Long> {
    
    @Query("SELECT a FROM MediaAlbum a WHERE a.userId = :userId ORDER BY a.updatedAt DESC")
    List<MediaAlbum> findByUserId(@Param("userId") String userId);

    @Query("SELECT a FROM MediaAlbum a WHERE a.conversationId = :convId ORDER BY a.updatedAt DESC")
    List<MediaAlbum> findByConversationId(@Param("convId") String conversationId);
}
