package com.im.service.agent.controller;

import com.im.service.agent.dto.*;
import com.im.service.agent.service.AgentDAOGovernanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 自主决策与DAO治理控制器
 * 功能#351: Agent决策透明化与社区治理
 */
@Slf4j
@RestController
@RequestMapping("/api/agent/dao")
@RequiredArgsConstructor
public class AgentDAOGovernanceController {

    private final AgentDAOGovernanceService daoService;

    /**
     * 记录Agent决策链
     */
    @PostMapping("/decision/record")
    public ResponseEntity<DecisionRecordResponse> recordDecision(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody DecisionRecordRequest request) {
        log.info("商家 {} 记录Agent决策: {}", merchantId, request.getDecisionType());
        DecisionRecordResponse response = daoService.recordDecision(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取决策链详情
     */
    @GetMapping("/decision/{decisionId}")
    public ResponseEntity<DecisionChainResponse> getDecisionChain(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String decisionId) {
        DecisionChainResponse response = daoService.getDecisionChain(merchantId, decisionId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取决策可解释性
     */
    @GetMapping("/decision/{decisionId}/explainability")
    public ResponseEntity<DecisionExplainabilityResponse> getDecisionExplainability(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String decisionId) {
        DecisionExplainabilityResponse response = daoService.getDecisionExplainability(merchantId, decisionId);
        return ResponseEntity.ok(response);
    }

    /**
     * 提交关键决策审批
     */
    @PostMapping("/decision/{decisionId}/submit-approval")
    public ResponseEntity<ApprovalResponse> submitForApproval(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String decisionId,
            @Valid @RequestBody ApprovalRequest request) {
        log.info("商家 {} 提交决策 {} 审批", merchantId, decisionId);
        ApprovalResponse response = daoService.submitForApproval(merchantId, decisionId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 审批决策
     */
    @PostMapping("/decision/{decisionId}/approve")
    public ResponseEntity<ApprovalResponse> approveDecision(
            @RequestHeader("X-Approver-Id") Long approverId,
            @PathVariable String decisionId,
            @Valid @RequestBody ApprovalActionRequest request) {
        log.info("审批人 {} 审批决策 {}: {}", approverId, decisionId, request.getAction());
        ApprovalResponse response = daoService.approveDecision(approverId, decisionId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 创建DAO提案
     */
    @PostMapping("/proposal")
    public ResponseEntity<ProposalResponse> createProposal(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody ProposalCreateRequest request) {
        log.info("商家 {} 创建DAO提案: {}", merchantId, request.getTitle());
        ProposalResponse response = daoService.createProposal(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 投票
     */
    @PostMapping("/proposal/{proposalId}/vote")
    public ResponseEntity<VoteResponse> vote(
            @RequestHeader("X-Voter-Id") Long voterId,
            @PathVariable String proposalId,
            @Valid @RequestBody VoteRequest request) {
        log.info("投票人 {} 对提案 {} 投票: {}", voterId, proposalId, request.getVote());
        VoteResponse response = daoService.vote(voterId, proposalId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取提案详情
     */
    @GetMapping("/proposal/{proposalId}")
    public ResponseEntity<ProposalDetailResponse> getProposalDetail(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String proposalId) {
        ProposalDetailResponse response = daoService.getProposalDetail(merchantId, proposalId);
        return ResponseEntity.ok(response);
    }

    /**
     * 执行提案
     */
    @PostMapping("/proposal/{proposalId}/execute")
    public ResponseEntity<ProposalExecutionResponse> executeProposal(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String proposalId) {
        log.info("商家 {} 执行提案 {}", merchantId, proposalId);
        ProposalExecutionResponse response = daoService.executeProposal(merchantId, proposalId);
        return ResponseEntity.ok(response);
    }

    /**
     * 存证决策日志
     */
    @PostMapping("/audit/log")
    public ResponseEntity<AuditLogResponse> recordAuditLog(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody AuditLogRequest request) {
        log.info("商家 {} 存证审计日志", merchantId);
        AuditLogResponse response = daoService.recordAuditLog(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取审计日志
     */
    @GetMapping("/audit/logs")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String agentId) {
        List<AuditLogResponse> logs = daoService.getAuditLogs(merchantId, startDate, endDate, agentId);
        return ResponseEntity.ok(logs);
    }

    /**
     * 验证审计日志
     */
    @PostMapping("/audit/verify")
    public ResponseEntity<AuditVerificationResponse> verifyAuditLog(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody AuditVerificationRequest request) {
        AuditVerificationResponse response = daoService.verifyAuditLog(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取Agent贡献度
     */
    @GetMapping("/contribution/{agentId}")
    public ResponseEntity<ContributionResponse> getAgentContribution(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String agentId) {
        ContributionResponse response = daoService.getAgentContribution(merchantId, agentId);
        return ResponseEntity.ok(response);
    }

    /**
     * 分发代币激励
     */
    @PostMapping("/incentive/distribute")
    public ResponseEntity<IncentiveDistributionResponse> distributeIncentive(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody IncentiveDistributionRequest request) {
        log.info("商家 {} 分发代币激励", merchantId);
        IncentiveDistributionResponse response = daoService.distributeIncentive(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取治理统计
     */
    @GetMapping("/governance/stats")
    public ResponseEntity<GovernanceStatsResponse> getGovernanceStats(
            @RequestHeader("X-Merchant-Id") Long merchantId) {
        GovernanceStatsResponse response = daoService.getGovernanceStats(merchantId);
        return ResponseEntity.ok(response);
    }
}
