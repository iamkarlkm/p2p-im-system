package com.im.backend.repository;

import com.im.backend.entity.NoteTagMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NoteTagMappingRepository extends JpaRepository<NoteTagMapping, Long> {
    List<NoteTagMapping> findByNoteId(Long noteId);

    @Modifying
    void deleteByNoteId(Long noteId);

    @Modifying
    void deleteByTagId(Long tagId);
}
