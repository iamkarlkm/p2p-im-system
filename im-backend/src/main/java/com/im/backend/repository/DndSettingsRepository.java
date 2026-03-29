package com.im.backend.repository;

import com.im.backend.entity.DndSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DndSettingsRepository extends JpaRepository<DndSettings, Long> {

    Optional<DndSettings> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
