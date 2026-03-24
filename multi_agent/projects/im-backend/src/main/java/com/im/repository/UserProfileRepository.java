package com.im.repository;

import com.im.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 用户资料仓储层
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

    Optional<UserProfileEntity> findByUserId(String userId);

    boolean existsByUserId(String userId);

    @Query("SELECT u FROM UserProfileEntity u WHERE u.userId IN :userIds")
    List<UserProfileEntity> findByUserIds(@Param("userIds") List<String> userIds);

    @Query("SELECT u.userId FROM UserProfileEntity u WHERE u.nickname LIKE %:keyword% OR u.bio LIKE %:keyword%")
    List<String> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT u FROM UserProfileEntity u WHERE u.country = :country AND u.profileVisibility = 'public'")
    List<UserProfileEntity> findByCountryPublic(@Param("country") String country);

    void deleteByUserId(String userId);
}
