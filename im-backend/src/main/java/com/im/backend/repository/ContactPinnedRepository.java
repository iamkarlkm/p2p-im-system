package com.im.backend.repository;

import com.im.backend.entity.ContactPinned;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContactPinnedRepository extends JpaRepository<ContactPinned, Long> {

    List<ContactPinned> findByUserIdOrderByPinOrderAsc(Long userId);

    Optional<ContactPinned> findByUserIdAndContactId(Long userId, Long contactId);

    boolean existsByUserIdAndContactId(Long userId, Long contactId);

    void deleteByUserIdAndContactId(Long userId, Long contactId);
}
