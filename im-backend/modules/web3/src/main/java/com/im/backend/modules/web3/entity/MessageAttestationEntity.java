package com.im.backend.modules.web3.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Web3消息存证实体类
 * 存储消息哈希上链存证信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("message_attestation")
public class MessageAttestationEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息唯一ID
     */
    @TableField("message_id")
    private String messageId;

    /**
     * 发送者ID
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 接收者ID
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 会话ID
     */
    @TableField("conversation_id")
    private String conversationId;

    /**
     * 消息内容哈希 (SHA-256)
     */
    @TableField("message_hash")
    private String messageHash;

    /**
     * 交易哈希 (区块链上链交易)
     */
    @TableField("tx_hash")
    private String txHash;

    /**
     * 区块链网络类型
     */
    @TableField("chain_network")
    private String chainNetwork;

    /**
     * 智能合约地址
     */
    @TableField("contract_address")
    private String contractAddress;

    /**
     * 区块高度
     */
    @TableField("block_number")
    private Long blockNumber;

    /**
     * 区块时间戳
     */
    @TableField("block_timestamp")
    private LocalDateTime blockTimestamp;

    /**
     * Gas费用
     */
    @TableField("gas_fee")
    private BigDecimal gasFee;

    /**
     * Gas价格 (Gwei)
     */
    @TableField("gas_price")
    private BigDecimal gasPrice;

    /**
     * 存证状态
     */
    @TableField("attestation_status")
    @EnumValue
    private AttestationStatus status;

    /**
     * 验证次数
     */
    @TableField("verify_count")
    private Integer verifyCount;

    /**
     * 最后验证时间
     */
    @TableField("last_verify_time")
    private LocalDateTime lastVerifyTime;

    /**
     * 存证证明数据 (Merkle Proof等)
     */
    @TableField(value = "proof_data", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private AttestationProof proofData;

    /**
     * 元数据
     */
    @TableField(value = "metadata", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private AttestationMetadata metadata;

    /**
     * 失败原因
     */
    @TableField("fail_reason")
    private String failReason;

    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;

    /**
     * 存证状态枚举
     */
    public enum AttestationStatus {
        PENDING("待处理", 0),
        SUBMITTING("提交中", 1),
        CONFIRMING("确认中", 2),
        CONFIRMED("已确认", 3),
        FAILED("失败", 4),
        EXPIRED("过期", 5);

        private final String desc;
        private final int code;

        AttestationStatus(String desc, int code) {
            this.desc = desc;
            this.code = code;
        }

        public String getDesc() { return desc; }
        public int getCode() { return code; }
    }

    /**
     * 存证证明数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttestationProof {
        /**
         * Merkle树根哈希
         */
        private String merkleRoot;

        /**
         * Merkle路径
         */
        private java.util.List<String> merklePath;

        /**
         * 叶子索引
         */
        private Integer leafIndex;

        /**
         * 签名数据
         */
        private String signature;

        /**
         * 验证者地址列表
         */
        private java.util.List<String> validatorAddresses;
    }

    /**
     * 存证元数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttestationMetadata {
        /**
         * 消息类型
         */
        private String messageType;

        /**
         * 消息大小 (字节)
         */
        private Long messageSize;

        /**
         * 附件数量
         */
        private Integer attachmentCount;

        /**
         * 客户端信息
         */
        private String clientInfo;

        /**
         * IP地址
         */
        private String ipAddress;

        /**
         * 额外属性
         */
        private java.util.Map<String, Object> extraProperties;
    }

    /**
     * 是否已确认上链
     */
    public boolean isConfirmed() {
        return status == AttestationStatus.CONFIRMED;
    }

    /**
     * 是否可以重试
     */
    public boolean canRetry() {
        return status == AttestationStatus.FAILED && retryCount < 3;
    }

    /**
     * 增加验证次数
     */
    public void incrementVerifyCount() {
        this.verifyCount = (this.verifyCount == null ? 0 : this.verifyCount) + 1;
        this.lastVerifyTime = LocalDateTime.now();
    }

    /**
     * 获取区块浏览器链接
     */
    public String getBlockExplorerUrl() {
        if (txHash == null || chainNetwork == null) {
            return null;
        }
        return switch (chainNetwork.toLowerCase()) {
            case "ethereum", "mainnet" -> "https://etherscan.io/tx/" + txHash;
            case "sepolia" -> "https://sepolia.etherscan.io/tx/" + txHash;
            case "goerli" -> "https://goerli.etherscan.io/tx/" + txHash;
            case "polygon" -> "https://polygonscan.com/tx/" + txHash;
            case "bsc", "binance" -> "https://bscscan.com/tx/" + txHash;
            case "arbitrum" -> "https://arbiscan.io/tx/" + txHash;
            case "optimism" -> "https://optimistic.etherscan.io/tx/" + txHash;
            default -> null;
        };
    }
}
