package com.im.backend.repository;

import com.im.backend.entity.AtMentionSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * @提及设置数据访问层
 */
@Repository
public interface AtMentionSettingsRepository extends JpaRepository<AtMentionSettings, Long> {

    /** 根据用户ID查询设置 */
    Optional<AtMentionSettings> findByUserId(Long userId);

    /** 存在性检查 */
    boolean existsByUserId(Long userId);
}
