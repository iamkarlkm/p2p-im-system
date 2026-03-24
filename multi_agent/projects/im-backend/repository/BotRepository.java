package com.im.backend.repository;

import com.im.backend.entity.BotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BotRepository extends JpaRepository<BotEntity, Long> {

    Optional<BotEntity> findByBotId(String botId);

    Optional<BotEntity> findByAccessToken(String accessToken);

    List<BotEntity> findByOwnerIdOrderByCreatedAtDesc(String ownerId);

    List<BotEntity> findByOwnerIdAndStatusOrderByCreatedAtDesc(String ownerId, String status);

    List<BotEntity> findByStatusAndIsPublicTrueOrderByCreatedAtDesc(String status);

    List<BotEntity> findByBotTypeOrderByCreatedAtDesc(String botType);

    List<BotEntity> findByOwnerIdAndBotTypeOrderByCreatedAtDesc(String ownerId, String botType);

    long countByOwnerId(String ownerId);

    long countByOwnerIdAndStatus(String ownerId, String status);

    long countByStatus(String status);

    boolean existsByBotId(String botId);

    boolean existsByOwnerIdAndNameAndStatusNot(String ownerId, String name, String status);

    List<BotEntity> findByOwnerIdAndIsPublicTrue(String ownerId);

    @Query("SELECT b FROM BotEntity b WHERE b.status = 'ACTIVE' ORDER BY b.messageCount DESC")
    List<BotEntity> findMostActiveBots();

    @Query("SELECT b.botType, COUNT(b) FROM BotEntity b WHERE b.status = 'ACTIVE' GROUP BY b.botType")
    List<Object[]> countByBotType();

    @Query("SELECT b.ownerId, COUNT(b) FROM BotEntity b WHERE b.status = 'ACTIVE' GROUP BY b.ownerId ORDER BY COUNT(b) DESC")
    List<Object[]> countByOwner();

    @Query("SELECT COALESCE(SUM(b.totalTokensUsed), 0) FROM BotEntity b WHERE b.ownerId = :ownerId")
    Long sumTokensByOwner(@Param("ownerId") String ownerId);

    @Query("SELECT b FROM BotEntity b WHERE b.status = 'ACTIVE' AND (b.isPublic = true OR b.ownerId = :userId) ORDER BY b.messageCount DESC")
    List<BotEntity> findAccessibleBots(@Param("userId") String userId);

    @Query("SELECT b FROM BotEntity b WHERE b.status = 'ACTIVE' AND b.enableImageGen = true")
    List<BotEntity> findImageGenEnabledBots();
}
