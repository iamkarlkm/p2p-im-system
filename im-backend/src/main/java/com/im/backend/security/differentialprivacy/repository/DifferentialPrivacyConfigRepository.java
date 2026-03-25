package com.im.backend.security.differentialprivacy.repository;

import com.im.backend.security.differentialprivacy.entity.DifferentialPrivacyConfigEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 差分隐私配置仓储接口
 */
@Repository
public interface DifferentialPrivacyConfigRepository extends JpaRepository<DifferentialPrivacyConfigEntity, Long> {
    
    Optional<DifferentialPrivacyConfigEntity> findByConfigKey(String configKey);
    
    List<DifferentialPrivacyConfigEntity> findByIsActiveTrue();
    
    List<DifferentialPrivacyConfigEntity> findByDataType(String dataType);
    
    List<DifferentialPrivacyConfigEntity> findByIsSensitiveTrue();
    
    Page<DifferentialPrivacyConfigEntity> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT c FROM DifferentialPrivacyConfigEntity c WHERE c.configKey LIKE %:keyword% AND c.isActive = true")
    List<DifferentialPrivacyConfigEntity> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT c FROM DifferentialPrivacyConfigEntity c WHERE c.noiseMechanism = :noiseMechanism AND c.isActive = true")
    List<DifferentialPrivacyConfigEntity> findByNoiseMechanism(@Param("noiseMechanism") String noiseMechanism);
    
    @Query("SELECT c FROM DifferentialPrivacyConfigEntity c WHERE c.privacyBudgetLimit <= :maxBudget AND c.isActive = true")
    List<DifferentialPrivacyConfigEntity> findByMaxPrivacyBudget(@Param("maxBudget") Double maxBudget);
    
    @Query("SELECT COUNT(c) FROM DifferentialPrivacyConfigEntity c WHERE c.isActive = true")
    Long countActiveConfigs();
    
    @Query("SELECT COUNT(c) FROM DifferentialPrivacyConfigEntity c WHERE c.isSensitive = true AND c.isActive = true")
    Long countSensitiveConfigs();
    
    List<DifferentialPrivacyConfigEntity> findByApprovalStatus(String approvalStatus);
    
    @Query("SELECT c FROM DifferentialPrivacyConfigEntity c WHERE c.requiresApproval = true AND c.approvalStatus = 'PENDING'")
    List<DifferentialPrivacyConfigEntity> findPendingApprovals();
    
    @Query("SELECT DISTINCT c.dataType FROM DifferentialPrivacyConfigEntity c WHERE c.isActive = true")
    List<String> findAllActiveDataTypes();
    
    @Query("SELECT DISTINCT c.noiseMechanism FROM DifferentialPrivacyConfigEntity c WHERE c.isActive = true")
    List<String> findAllActiveNoiseMechanisms();
}