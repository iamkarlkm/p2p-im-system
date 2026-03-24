package com.im.backend.repository;

import com.im.backend.entity.BackupStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BackupStrategyRepository extends JpaRepository<BackupStrategy, Long> {

    Optional<BackupStrategy> findByName(String name);

    List<BackupStrategy> findByEnabled(Boolean enabled);

    List<BackupStrategy> findByComponent(String component);

    List<BackupStrategy> findByComponentAndEnabled(String component, Boolean enabled);

    @Query("SELECT bs FROM BackupStrategy bs WHERE bs.enabled = true ORDER BY bs.component, bs.name")
    List<BackupStrategy> findAllActiveStrategies();

    boolean existsByName(String name);

    long countByEnabled(Boolean enabled);

    long countByComponent(String component);
}
