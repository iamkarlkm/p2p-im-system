package com.im.backend.repository;

import com.im.backend.model.EncryptionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 加密密钥数据层
 */
@Repository
public interface EncryptionKeyRepository extends JpaRepository<EncryptionKey, Long> {

    /**
     * 根据用户ID查找激活的加密密钥
     */
    @Query("SELECT ek FROM EncryptionKey ek WHERE ek.userId = ?1 AND ek.isActive = true")
    List<EncryptionKey> findActiveKeysByUserId(Long userId);

    /**
     * 根据用户ID和密钥类型查找激活的密钥
     */
    @Query("SELECT ek FROM EncryptionKey ek WHERE ek.userId = ?1 AND ek.keyType = ?2 AND ek.isActive = true")
    Optional<EncryptionKey> findActiveKeyByUserIdAndType(Long userId, String keyType);

    /**
     * 根据用户ID查找最新的加密密钥
     */
    @Query("SELECT ek FROM EncryptionKey ek WHERE ek.userId = ?1 ORDER BY ek.createdAt DESC")
    List<EncryptionKey> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据用户ID和密钥版本查找密钥
     */
    Optional<EncryptionKey> findByUserIdAndKeyVersion(Long userId, Integer keyVersion);

    /**
     * 查找所有即将过期的密钥
     */
    @Query("SELECT ek FROM EncryptionKey ek WHERE ek.expiresAt < CURRENT_TIMESTAMP AND ek.isActive = true")
    List<EncryptionKey> findExpiringKeys();

    /**
     * 停用用户的所有密钥
     */
    @Query("UPDATE EncryptionKey ek SET ek.isActive = false WHERE ek.userId = ?1")
    void deactivateAllKeysByUserId(Long userId);
}
