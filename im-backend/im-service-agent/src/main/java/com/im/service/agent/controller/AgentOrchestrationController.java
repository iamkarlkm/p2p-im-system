package com.im.service.agent.controller;

import com.im.service.agent.dto.*;
import com.im.service.agent.service.AgentOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 多Agent协作编排系统控制器
 * 功能#349: Agent任务分解、分配与协作
 */
@Slf4j
@RestController
@RequestMapping("/api/agent/orchestration")
@RequiredArgsConstructor
public class AgentOrchestrationController {

    private final AgentOrchestrationService orchestrationService;

    /**
     * 创建编排工作流
     */
    @PostMapping("/workflow")
    public ResponseEntity<WorkflowResponse> createWorkflow(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody WorkflowCreateRequest request) {
        log.info("商家 {} 创建Agent编排工作流: {}", merchantId, request.getWorkflowName());
        WorkflowResponse response = orchestrationService.createWorkflow(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 启动工作流执行
     */
    @PostMapping("/workflow/{workflowId}/execute")
    public ResponseEntity<WorkflowExecutionResponse> executeWorkflow(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String workflowId,
            @RequestBody Map<String, Object> params) {
        log.info("商家 {} 启动工作流执行: {}", merchantId, workflowId);
        WorkflowExecutionResponse response = orchestrationService.executeWorkflow(merchantId, workflowId, params);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取工作流状态
     */
    @GetMapping("/workflow/{workflowId}/status")
    public ResponseEntity<WorkflowStatusResponse> getWorkflowStatus(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String workflowId) {
        WorkflowStatusResponse response = orchestrationService.getWorkflowStatus(merchantId, workflowId);
        return ResponseEntity.ok(response);
    }

    /**
     * 取消工作流执行
     */
    @PostMapping("/workflow/{executionId}/cancel")
    public ResponseEntity<Void> cancelWorkflow(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String executionId) {
        log.info("商家 {} 取消工作流执行: {}", merchantId, executionId);
        orchestrationService.cancelWorkflow(merchantId, executionId);
        return ResponseEntity.ok().build();
    }

    /**
     * 分解复杂任务
     */
    @PostMapping("/task/decompose")
    public ResponseEntity<TaskDecompositionResponse> decomposeTask(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody TaskDecompositionRequest request) {
        log.info("商家 {} 分解任务: {}", merchantId, request.getTaskDescription());
        TaskDecompositionResponse response = orchestrationService.decomposeTask(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 分配子任务给Agent
     */
    @PostMapping("/task/{taskId}/assign")
    public ResponseEntity<TaskAssignmentResponse> assignTask(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String taskId,
            @Valid @RequestBody TaskAssignmentRequest request) {
        log.info("商家 {} 分配任务 {} 给Agent {}", merchantId, taskId, request.getAgentId());
        TaskAssignmentResponse response = orchestrationService.assignTask(merchantId, taskId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 发送Agent间消息（ACL协议）
     */
    @PostMapping("/agent/message")
    public ResponseEntity<AgentMessageResponse> sendAgentMessage(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody AgentMessageRequest request) {
        log.debug("Agent {} 发送消息给 Agent {}", request.getSenderAgentId(), request.getReceiverAgentId());
        AgentMessageResponse response = orchestrationService.sendAgentMessage(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 注册Agent能力
     */
    @PostMapping("/agent/register")
    public ResponseEntity<AgentRegistrationResponse> registerAgent(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody AgentRegistrationRequest request) {
        log.info("商家 {} 注册Agent: {}", merchantId, request.getAgentName());
        AgentRegistrationResponse response = orchestrationService.registerAgent(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 发现可用Agent
     */
    @GetMapping("/agent/discover")
    public ResponseEntity<List<AgentCapabilityResponse>> discoverAgents(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam(required = false) String capability) {
        List<AgentCapabilityResponse> agents = orchestrationService.discoverAgents(merchantId, capability);
        return ResponseEntity.ok(agents);
    }

    /**
     * 检测冲突
     */
    @PostMapping("/conflict/detect")
    public ResponseEntity<ConflictDetectionResponse> detectConflicts(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestBody List<String> agentIds) {
        log.info("商家 {} 检测Agent冲突", merchantId);
        ConflictDetectionResponse response = orchestrationService.detectConflicts(merchantId, agentIds);
        return ResponseEntity.ok(response);
    }

    /**
     * 仲裁决策
     */
    @PostMapping("/conflict/arbitrate")
    public ResponseEntity<ArbitrationResponse> arbitrateConflict(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody ArbitrationRequest request) {
        log.info("商家 {} 仲裁冲突: {}", merchantId, request.getConflictId());
        ArbitrationResponse response = orchestrationService.arbitrateConflict(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取编排可视化数据
     */
    @GetMapping("/workflow/{workflowId}/visualization")
    public ResponseEntity<WorkflowVisualizationResponse> getWorkflowVisualization(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String workflowId) {
        WorkflowVisualizationResponse response = orchestrationService.getWorkflowVisualization(merchantId, workflowId);
        return ResponseEntity.ok(response);
    }
}
