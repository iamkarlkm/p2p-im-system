package com.im.backend.modules.miniprogram.service;

import com.im.backend.modules.miniprogram.dto.SubmitVersionRequest;
import com.im.backend.modules.miniprogram.dto.VersionResponse;

import java.util.List;

/**
 * 小程序版本服务接口
 */
public interface IMiniProgramVersionService {

    /**
     * 提交版本
     */
    VersionResponse submitVersion(Long developerId, SubmitVersionRequest request);

    /**
     * 获取版本列表
     */
    List<VersionResponse> getVersions(Long appId);

    /**
     * 获取版本详情
     */
    VersionResponse getVersion(Long versionId);

    /**
     * 提交审核
     */
    void submitAudit(Long developerId, Long versionId);

    /**
     * 审核版本
     */
    void auditVersion(Long versionId, boolean approved, String reason);

    /**
     * 发布版本
     */
    void releaseVersion(Long developerId, Long versionId);

    /**
     * 回滚版本
     */
    void rollbackVersion(Long developerId, Long versionId);
}
