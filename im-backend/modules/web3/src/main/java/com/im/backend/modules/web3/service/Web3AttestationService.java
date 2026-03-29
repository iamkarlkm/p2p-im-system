package com.im.backend.modules.web3.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.web3.entity.MessageAttestationEntity;
import com.im.backend.modules.web3.entity.MessageAttestationEntity.AttestationStatus;
import com.im.backend.modules.web3.repository.MessageAttestationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Web3消息存证服务
 * 处理消息哈希上链、存证查询、链上验证
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Web3AttestationService {

    private final MessageAttestationRepository attestationRepository;

    private Web3j web3j;
    private ExecutorService executorService;

    // 区块链配置
    private static final String DEFAULT_CHAIN_NETWORK = "sepolia";
    private static final String CONTRACT_ADDRESS = "0x1234567890abcdef1234567890abcdef12345678";
    private static final long CONFIRMATION_BLOCKS = 12;
    private static final long TIMEOUT_MINUTES = 30;

    @PostConstruct
    public void init() {
        // 初始化Web3连接
        String rpcUrl = System.getenv("WEB3_RPC_URL");
        if (rpcUrl == null) {
            rpcUrl = "https://sepolia.infura.io/v3/demo-key";
        }
        this.web3j = Web3j.build(new HttpService(rpcUrl));
        this.executorService = Executors.newFixedThreadPool(10);
        log.info("Web3AttestationService initialized with RPC: {}", rpcUrl);
    }

    @PreDestroy
    public void destroy() {
        if (web3j != null) {
            web3j.shutdown();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
        log.info("Web3AttestationService destroyed");
    }

    /**
     * 创建消息存证
     */
    @Transactional
    public MessageAttestationEntity createAttestation(
            String messageId,
            Long senderId,
            Long receiverId,
            String conversationId,
            String messageContent,
            String messageType,
            Long messageSize,
            String clientInfo,
            String ipAddress) {

        // 检查是否已存证
        if (attestationRepository.existsByMessageId(messageId)) {
            throw new IllegalStateException("Message already attested: " + messageId);
        }

        // 计算消息哈希
        String messageHash = calculateMessageHash(messageContent);

        // 构建元数据
        MessageAttestationEntity.AttestationMetadata metadata = MessageAttestationEntity.AttestationMetadata.builder()
                .messageType(messageType)
                .messageSize(messageSize)
                .attachmentCount(0)
                .clientInfo(clientInfo)
                .ipAddress(ipAddress)
                .extraProperties(new java.util.HashMap<>())
                .build();

        // 创建存证实体
        MessageAttestationEntity attestation = MessageAttestationEntity.builder()
                .messageId(messageId)
                .senderId(senderId)
                .receiverId(receiverId)
                .conversationId(conversationId)
                .messageHash(messageHash)
                .chainNetwork(DEFAULT_CHAIN_NETWORK)
                .contractAddress(CONTRACT_ADDRESS)
                .status(AttestationStatus.PENDING)
                .verifyCount(0)
                .retryCount(0)
                .metadata(metadata)
                .deleted(false)
                .build();

        attestationRepository.insert(attestation);
        log.info("Created attestation for message: {}, hash: {}", messageId, messageHash);

        // 异步提交上链
        submitAttestationAsync(attestation.getId(), messageHash);

        return attestation;
    }

    /**
     * 计算消息哈希 (SHA-256)
     */
    public String calculateMessageHash(String content) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        byte[] hashBytes = Hash.sha256(content.getBytes(StandardCharsets.UTF_8));
        return "0x" + org.web3j.utils.Numeric.toHexStringNoPrefix(hashBytes);
    }

    /**
     * 异步提交存证到区块链
     */
    @Async
    public void submitAttestationAsync(Long attestationId, String messageHash) {
        CompletableFuture.runAsync(() -> {
            try {
                submitToBlockchain(attestationId, messageHash);
            } catch (Exception e) {
                log.error("Failed to submit attestation {}: {}", attestationId, e.getMessage());
                handleSubmissionFailure(attestationId, e.getMessage());
            }
        }, executorService);
    }

    /**
     * 提交到区块链
     */
    private void submitToBlockchain(Long attestationId, String messageHash) throws Exception {
        // 更新状态为提交中
        attestationRepository.updateStatus(attestationId, AttestationStatus.SUBMITTING.name());

        // 模拟区块链交互（实际项目中替换为真实的智能合约调用）
        log.info("Submitting attestation {} to blockchain, hash: {}", attestationId, messageHash);

        // 模拟交易哈希
        String txHash = "0x" + generateRandomTxHash();

        // 更新交易哈希
        MessageAttestationEntity entity = attestationRepository.selectById(attestationId);
        entity.setTxHash(txHash);
        entity.setStatus(AttestationStatus.CONFIRMING);
        attestationRepository.updateById(entity);

        // 等待交易确认
        waitForConfirmation(attestationId, txHash);
    }

    /**
     * 等待交易确认
     */
    private void waitForConfirmation(Long attestationId, String txHash) throws Exception {
        log.info("Waiting for confirmation of tx: {}", txHash);

        int attempts = 0;
        int maxAttempts = 60; // 30分钟超时

        while (attempts < maxAttempts) {
            try {
                Thread.sleep(30000); // 30秒轮询

                // 模拟获取交易收据
                TransactionReceipt receipt = simulateGetReceipt(txHash);

                if (receipt != null && receipt.isStatusOK()) {
                    // 获取区块信息
                    long blockNumber = receipt.getBlockNumber().longValue();
                    BigInteger gasUsed = receipt.getGasUsed();
                    BigDecimal gasFee = calculateGasFee(gasUsed);

                    // 等待足够确认数
                    long currentBlock = web3j.ethBlockNumber().send().getBlockNumber().longValue();
                    if (currentBlock - blockNumber >= CONFIRMATION_BLOCKS) {
                        // 更新确认状态
                        LocalDateTime blockTimestamp = LocalDateTime.now(); // 实际应从区块获取
                        attestationRepository.updateConfirmation(
                                attestationId, txHash, blockNumber, blockTimestamp,
                                gasFee, BigDecimal.valueOf(20)); // 假设20 Gwei

                        // 生成证明数据
                        generateProofData(attestationId, messageHash);

                        log.info("Attestation {} confirmed at block {}", attestationId, blockNumber);
                        return;
                    }
                }

                attempts++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Confirmation wait interrupted", e);
            }
        }

        throw new RuntimeException("Confirmation timeout after " + maxAttempts + " attempts");
    }

    /**
     * 生成证明数据
     */
    private void generateProofData(Long attestationId, String messageHash) {
        // 模拟生成Merkle证明
        MessageAttestationEntity.AttestationProof proof = MessageAttestationEntity.AttestationProof.builder()
                .merkleRoot("0x" + generateRandomTxHash())
                .merklePath(java.util.List.of("0xabc", "0xdef", "0x123"))
                .leafIndex((int) (Math.random() * 1000))
                .signature("0x" + generateRandomTxHash())
                .validatorAddresses(java.util.List.of(
                        "0xValidator1...",
                        "0xValidator2...",
                        "0xValidator3..."
                ))
                .build();

        attestationRepository.updateProofData(attestationId, proof);
    }

    /**
     * 处理提交失败
     */
    private void handleSubmissionFailure(Long attestationId, String errorMessage) {
        attestationRepository.updateFailure(attestationId, errorMessage);
    }

    /**
     * 根据消息ID查询存证
     */
    public Optional<MessageAttestationEntity> getAttestationByMessageId(String messageId) {
        return attestationRepository.findByMessageId(messageId);
    }

    /**
     * 根据交易哈希查询存证
     */
    public Optional<MessageAttestationEntity> getAttestationByTxHash(String txHash) {
        return attestationRepository.findByTxHash(txHash);
    }

    /**
     * 获取用户的存证列表
     */
    public List<MessageAttestationEntity> getUserAttestations(Long senderId) {
        return attestationRepository.findBySenderId(senderId);
    }

    /**
     * 分页获取用户存证
     */
    public IPage<MessageAttestationEntity> getUserAttestationsPage(Long senderId, int page, int size) {
        Page<MessageAttestationEntity> pageParam = new Page<>(page, size);
        return attestationRepository.findPageBySenderId(senderId, pageParam);
    }

    /**
     * 验证存证
     */
    @Transactional
    public AttestationVerificationResult verifyAttestation(Long attestationId) {
        MessageAttestationEntity attestation = attestationRepository.selectById(attestationId);
        if (attestation == null) {
            return AttestationVerificationResult.failed("Attestation not found");
        }

        if (!attestation.isConfirmed()) {
            return AttestationVerificationResult.failed("Attestation not confirmed on blockchain");
        }

        try {
            // 链上验证
            boolean valid = verifyOnChain(attestation);

            // 更新验证次数
            attestationRepository.incrementVerifyCount(attestationId);

            if (valid) {
                return AttestationVerificationResult.success(attestation);
            } else {
                return AttestationVerificationResult.failed("On-chain verification failed");
            }
        } catch (Exception e) {
            log.error("Verification failed for attestation {}: {}", attestationId, e.getMessage());
            return AttestationVerificationResult.failed("Verification error: " + e.getMessage());
        }
    }

    /**
     * 链上验证
     */
    private boolean verifyOnChain(MessageAttestationEntity attestation) throws Exception {
        // 模拟链上验证
        log.info("Verifying attestation {} on chain, tx: {}", attestation.getId(), attestation.getTxHash());

        // 实际应调用智能合约验证函数
        // 检查交易是否存在、是否成功、消息哈希是否匹配
        return attestation.getTxHash() != null && attestation.getTxHash().startsWith("0x");
    }

    /**
     * 获取存证统计
     */
    public AttestationStatistics getStatistics(Long senderId) {
        long totalCount = attestationRepository.countBySenderId(senderId);
        List<java.util.Map<String, Object>> statusCounts = attestationRepository.countByStatus(senderId);
        List<java.util.Map<String, Object>> chainStats = attestationRepository.getChainStatistics(senderId);

        AttestationStatistics stats = new AttestationStatistics();
        stats.setTotalCount(totalCount);
        stats.setStatusDistribution(statusCounts);
        stats.setChainDistribution(chainStats);

        // 计算总Gas费用
        BigDecimal totalGas = chainStats.stream()
                .map(m -> (BigDecimal) m.get("total_gas"))
                .filter(g -> g != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalGasFee(totalGas);

        return stats;
    }

    /**
     * 重试失败的存证
     */
    @Transactional
    public void retryFailedAttestations(int batchSize) {
        List<MessageAttestationEntity> retryable = attestationRepository.findRetryable(batchSize);

        for (MessageAttestationEntity attestation : retryable) {
            log.info("Retrying attestation: {}", attestation.getId());
            attestationRepository.updateStatus(attestation.getId(), AttestationStatus.PENDING.name());
            submitAttestationAsync(attestation.getId(), attestation.getMessageHash());
        }
    }

    /**
     * 检查待确认交易状态
     */
    public void checkPendingConfirmations() {
        List<MessageAttestationEntity> pending = attestationRepository.findPendingConfirmations();
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);

        for (MessageAttestationEntity attestation : pending) {
            if (attestation.getUpdateTime().isBefore(timeout)) {
                log.warn("Attestation {} timed out, marking as failed", attestation.getId());
                attestationRepository.updateFailure(attestation.getId(), "Confirmation timeout");
            }
        }
    }

    // Helper methods
    private String generateRandomTxHash() {
        StringBuilder sb = new StringBuilder(64);
        String chars = "0123456789abcdef";
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 64; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private TransactionReceipt simulateGetReceipt(String txHash) {
        // 模拟交易收据
        if (Math.random() > 0.3) { // 70%概率成功
            TransactionReceipt receipt = new TransactionReceipt();
            receipt.setStatus("0x1");
            receipt.setBlockNumber(BigInteger.valueOf(System.currentTimeMillis() / 10000));
            receipt.setGasUsed(BigInteger.valueOf(21000));
            return receipt;
        }
        return null;
    }

    private BigDecimal calculateGasFee(BigInteger gasUsed) {
        BigDecimal gasPriceGwei = BigDecimal.valueOf(20);
        BigDecimal gasPriceWei = gasPriceGwei.multiply(BigDecimal.TEN.pow(9));
        return new BigDecimal(gasUsed).multiply(gasPriceWei)
                .divide(BigDecimal.TEN.pow(18), 8, BigDecimal.ROUND_HALF_UP);
    }

    // DTO classes
    @lombok.Data
    public static class AttestationVerificationResult {
        private boolean valid;
        private String message;
        private MessageAttestationEntity attestation;
        private LocalDateTime verifyTime;

        public static AttestationVerificationResult success(MessageAttestationEntity attestation) {
            AttestationVerificationResult result = new AttestationVerificationResult();
            result.valid = true;
            result.message = "Verification successful";
            result.attestation = attestation;
            result.verifyTime = LocalDateTime.now();
            return result;
        }

        public static AttestationVerificationResult failed(String message) {
            AttestationVerificationResult result = new AttestationVerificationResult();
            result.valid = false;
            result.message = message;
            result.verifyTime = LocalDateTime.now();
            return result;
        }
    }

    @lombok.Data
    public static class AttestationStatistics {
        private long totalCount;
        private BigDecimal totalGasFee;
        private List<java.util.Map<String, Object>> statusDistribution;
        private List<java.util.Map<String, Object>> chainDistribution;
    }
}
