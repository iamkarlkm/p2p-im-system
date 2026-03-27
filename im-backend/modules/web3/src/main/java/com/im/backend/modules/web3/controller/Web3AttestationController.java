package com.im.backend.modules.web3.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.im.backend.common.dto.PageResult;
import com.im.backend.common.dto.Result;
import com.im.backend.modules.web3.dto.*;
import com.im.backend.modules.web3.entity.MessageAttestationEntity;
import com.im.backend.modules.web3.service.Web3AttestationService;
import com.im.backend.security.CurrentUser;
import com.im.backend.security.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Web3消息存证控制器
 * 提供消息哈希上链、存证查询、链上验证API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/web3/attestation")
@RequiredArgsConstructor
@Tag(name = "Web3消息存证", description = "消息哈希上链存证与验证")
public class Web3AttestationController {

    private final Web3AttestationService attestationService;

    /**
     * 创建消息存证
     */
    @PostMapping("/create")
    @Operation(summary = "创建消息存证", description = "将消息哈希上链存证")
    public Result<MessageAttestationVO> createAttestation(
            @CurrentUser UserDetails user,
            @Valid @RequestBody CreateAttestationRequest request) {

        log.info("Creating attestation for message: {}, user: {}", request.getMessageId(), user.getId());

        MessageAttestationEntity attestation = attestationService.createAttestation(
                request.getMessageId(),
                user.getId(),
                request.getReceiverId(),
                request.getConversationId(),
                request.getMessageContent(),
                request.getMessageType(),
                request.getMessageSize(),
                request.getClientInfo(),
                request.getIpAddress()
        );

        return Result.success(convertToVO(attestation));
    }

    /**
     * 根据消息ID查询存证
     */
    @GetMapping("/message/{messageId}")
    @Operation(summary = "查询消息存证", description = "根据消息ID查询存证信息")
    public Result<MessageAttestationVO> getAttestationByMessageId(
            @Parameter(description = "消息ID") @PathVariable String messageId) {

        return attestationService.getAttestationByMessageId(messageId)
                .map(this::convertToVO)
                .map(Result::success)
                .orElse(Result.error(404, "Attestation not found"));
    }

    /**
     * 根据交易哈希查询存证
     */
    @GetMapping("/tx/{txHash}")
    @Operation(summary = "查询交易存证", description = "根据区块链交易哈希查询存证")
    public Result<MessageAttestationVO> getAttestationByTxHash(
            @Parameter(description = "交易哈希") @PathVariable String txHash) {

        return attestationService.getAttestationByTxHash(txHash)
                .map(this::convertToVO)
                .map(Result::success)
                .orElse(Result.error(404, "Attestation not found"));
    }

    /**
     * 获取用户存证列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取存证列表", description = "获取当前用户的所有存证")
    public Result<List<MessageAttestationVO>> getUserAttestations(@CurrentUser UserDetails user) {
        List<MessageAttestationEntity> attestations = attestationService.getUserAttestations(user.getId());
        List<MessageAttestationVO> vos = attestations.stream()
                .map(this::convertToVO)
                .toList();
        return Result.success(vos);
    }

    /**
     * 分页获取用户存证
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取存证", description = "分页查询用户存证记录")
    public Result<PageResult<MessageAttestationVO>> getUserAttestationsPage(
            @CurrentUser UserDetails user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        IPage<MessageAttestationEntity> pageResult = attestationService.getUserAttestationsPage(user.getId(), page, size);

        List<MessageAttestationVO> vos = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        PageResult<MessageAttestationVO> result = PageResult.of(
                vos,
                pageResult.getTotal(),
                pageResult.getCurrent(),
                pageResult.getSize()
        );

        return Result.success(result);
    }

    /**
     * 验证存证
     */
    @PostMapping("/{id}/verify")
    @Operation(summary = "验证存证", description = "对指定存证进行链上验证")
    public Result<AttestationVerificationVO> verifyAttestation(
            @Parameter(description = "存证ID") @PathVariable Long id) {

        Web3AttestationService.AttestationVerificationResult result = attestationService.verifyAttestation(id);

        AttestationVerificationVO vo = new AttestationVerificationVO();
        vo.setValid(result.isValid());
        vo.setMessage(result.getMessage());
        vo.setVerifyTime(result.getVerifyTime());

        if (result.getAttestation() != null) {
            vo.setAttestation(convertToVO(result.getAttestation()));
        }

        return Result.success(vo);
    }

    /**
     * 计算消息哈希
     */
    @PostMapping("/hash")
    @Operation(summary = "计算消息哈希", description = "计算消息内容的SHA-256哈希值")
    public Result<MessageHashVO> calculateHash(@RequestBody CalculateHashRequest request) {
        String hash = attestationService.calculateMessageHash(request.getContent());

        MessageHashVO vo = new MessageHashVO();
        vo.setHash(hash);
        vo.setAlgorithm("SHA-256");

        return Result.success(vo);
    }

    /**
     * 获取存证统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "存证统计", description = "获取用户存证统计数据")
    public Result<AttestationStatisticsVO> getStatistics(@CurrentUser UserDetails user) {
        Web3AttestationService.AttestationStatistics stats = attestationService.getStatistics(user.getId());

        AttestationStatisticsVO vo = new AttestationStatisticsVO();
        vo.setTotalCount(stats.getTotalCount());
        vo.setTotalGasFee(stats.getTotalGasFee());
        vo.setStatusDistribution(stats.getStatusDistribution());
        vo.setChainDistribution(stats.getChainDistribution());

        return Result.success(vo);
    }

    /**
     * 搜索存证
     */
    @PostMapping("/search")
    @Operation(summary = "搜索存证", description = "按条件搜索存证记录")
    public Result<PageResult<MessageAttestationVO>> searchAttestations(
            @CurrentUser UserDetails user,
            @RequestBody SearchAttestationRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        // 调用服务搜索
        IPage<MessageAttestationEntity> pageResult = attestationService.getUserAttestationsPage(user.getId(), page, size);

        List<MessageAttestationVO> vos = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return Result.success(PageResult.of(vos, pageResult.getTotal(), page, size));
    }

    /**
     * 批量创建存证
     */
    @PostMapping("/batch-create")
    @Operation(summary = "批量创建存证", description = "批量将多条消息哈希上链存证")
    public Result<BatchAttestationResultVO> batchCreateAttestation(
            @CurrentUser UserDetails user,
            @Valid @RequestBody BatchCreateAttestationRequest request) {

        List<MessageAttestationVO> created = new java.util.ArrayList<>();
        List<String> failed = new java.util.ArrayList<>();

        for (CreateAttestationRequest item : request.getItems()) {
            try {
                MessageAttestationEntity attestation = attestationService.createAttestation(
                        item.getMessageId(),
                        user.getId(),
                        item.getReceiverId(),
                        item.getConversationId(),
                        item.getMessageContent(),
                        item.getMessageType(),
                        item.getMessageSize(),
                        item.getClientInfo(),
                        item.getIpAddress()
                );
                created.add(convertToVO(attestation));
            } catch (Exception e) {
                log.error("Failed to create attestation for message {}: {}", item.getMessageId(), e.getMessage());
                failed.add(item.getMessageId());
            }
        }

        BatchAttestationResultVO result = new BatchAttestationResultVO();
        result.setCreated(created);
        result.setFailed(failed);
        result.setSuccessCount(created.size());
        result.setFailCount(failed.size());

        return Result.success(result);
    }

    /**
     * 获取支持的区块链网络
     */
    @GetMapping("/networks")
    @Operation(summary = "支持的链", description = "获取支持的区块链网络列表")
    public Result<List<ChainNetworkVO>> getSupportedNetworks() {
        List<ChainNetworkVO> networks = java.util.List.of(
                new ChainNetworkVO("ethereum", "Ethereum Mainnet", "1", true),
                new ChainNetworkVO("sepolia", "Sepolia Testnet", "11155111", true),
                new ChainNetworkVO("goerli", "Goerli Testnet", "5", false),
                new ChainNetworkVO("polygon", "Polygon", "137", true),
                new ChainNetworkVO("bsc", "BNB Smart Chain", "56", true),
                new ChainNetworkVO("arbitrum", "Arbitrum One", "42161", true),
                new ChainNetworkVO("optimism", "Optimism", "10", true)
        );
        return Result.success(networks);
    }

    /**
     * 转换实体为VO
     */
    private MessageAttestationVO convertToVO(MessageAttestationEntity entity) {
        if (entity == null) return null;

        MessageAttestationVO vo = new MessageAttestationVO();
        vo.setId(entity.getId());
        vo.setMessageId(entity.getMessageId());
        vo.setSenderId(entity.getSenderId());
        vo.setReceiverId(entity.getReceiverId());
        vo.setConversationId(entity.getConversationId());
        vo.setMessageHash(entity.getMessageHash());
        vo.setTxHash(entity.getTxHash());
        vo.setChainNetwork(entity.getChainNetwork());
        vo.setContractAddress(entity.getContractAddress());
        vo.setBlockNumber(entity.getBlockNumber());
        vo.setBlockTimestamp(entity.getBlockTimestamp());
        vo.setGasFee(entity.getGasFee());
        vo.setGasPrice(entity.getGasPrice());
        vo.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        vo.setStatusDesc(entity.getStatus() != null ? entity.getStatus().getDesc() : null);
        vo.setVerifyCount(entity.getVerifyCount());
        vo.setLastVerifyTime(entity.getLastVerifyTime());
        vo.setProofData(entity.getProofData());
        vo.setMetadata(entity.getMetadata());
        vo.setFailReason(entity.getFailReason());
        vo.setRetryCount(entity.getRetryCount());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        vo.setBlockExplorerUrl(entity.getBlockExplorerUrl());

        return vo;
    }
}
