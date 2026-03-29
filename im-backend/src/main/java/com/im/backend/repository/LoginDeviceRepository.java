package com.im.backend.repository;

import com.im.backend.entity.LoginDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoginDeviceRepository extends JpaRepository<LoginDevice, Long> {
    List<LoginDevice> findByUserIdOrderByLastActiveTimeDesc(Long userId);
    Optional<LoginDevice> findByDeviceId(String deviceId);
    Optional<LoginDevice> findByUserIdAndDeviceId(Long userId, String deviceId);
    List<LoginDevice> findByUserIdAndIsRemoteTerminatedFalseOrderByLastActiveTimeDesc(Long userId);
    void deleteByUserIdAndDeviceId(Long userId, String deviceId);
}
