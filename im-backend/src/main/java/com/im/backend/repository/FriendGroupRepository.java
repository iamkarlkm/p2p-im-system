package com.im.backend.repository;

import com.im.backend.model.FriendGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendGroupRepository extends JpaRepository<FriendGroup, Long> {

    List<FriendGroup> findAllByUserId(Long userId);

    List<FriendGroup> findAllByUserIdOrderBySortOrderAsc(Long userId);

    Optional<FriendGroup> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndName(Long userId, String name);

    long countByUserId(Long userId);
}
