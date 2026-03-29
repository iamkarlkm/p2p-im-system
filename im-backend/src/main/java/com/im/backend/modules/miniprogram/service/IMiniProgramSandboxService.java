package com.im.backend.modules.miniprogram.service;

import com.im.backend.modules.miniprogram.dto.CreateSandboxRequest;
import com.im.backend.modules.miniprogram.dto.SandboxResponse;

import java.util.List;

/**
 * 沙箱环境服务接口
 */
public interface IMiniProgramSandboxService {

    /**
     * 创建沙箱环境
     */
    SandboxResponse createSandbox(Long developerId, CreateSandboxRequest request);

    /**
     * 获取沙箱列表
     */
    List<SandboxResponse> getSandboxes(Long appId);

    /**
     * 停止沙箱环境
     */
    void stopSandbox(Long developerId, String sandboxId);

    /**
     * 重启沙箱环境
     */
    SandboxResponse restartSandbox(Long developerId, String sandboxId);

    /**
     * 获取沙箱调试信息
     */
    SandboxResponse getSandboxDebugInfo(String sandboxId);

    /**
     * 清理过期沙箱
     */
    int cleanupExpiredSandboxes();
}
