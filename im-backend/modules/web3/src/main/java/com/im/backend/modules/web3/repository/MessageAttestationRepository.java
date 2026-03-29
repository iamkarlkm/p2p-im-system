package com.im.backend.modules.web3.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.web3.entity.MessageAttestationEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息存证数据访问层
 */
@Repository
@Mapper
public interface MessageAttestationRepository extends BaseMapper<MessageAttestationEntity> {

    /**
     * 根据消息ID查询存证
     */
    @Select("SELECT * FROM message_attestation WHERE message_id = #{messageId} AND deleted = 0")
    Optional<MessageAttestationEntity> findByMessageId(@Param("messageId") String messageId);

    /**
     * 根据交易哈希查询存证
     */
    @Select("SELECT * FROM message_attestation WHERE tx_hash = #{txHash} AND deleted = 0")
    Optional<MessageAttestationEntity> findByTxHash(@Param("txHash") String txHash);

    /**
     * 根据发送者查询存证列表
     */
    @Select("SELECT * FROM message_attestation WHERE sender_id = #{senderId} AND deleted = 0 ORDER BY create_time DESC")
    List<MessageAttestationEntity> findBySenderId(@Param("senderId") Long senderId);

    /**
     * 分页查询发送者的存证
     */
    IPage<MessageAttestationEntity> findPageBySenderId(@Param("senderId") Long senderId, Page<MessageAttestationEntity> page);

    /**
     * 根据会话ID查询存证
     */
    @Select("SELECT * FROM message_attestation WHERE conversation_id = #{conversationId} AND deleted = 0 ORDER BY create_time DESC")
    List<MessageAttestationEntity> findByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据状态查询存证列表
     */
    @Select("SELECT * FROM message_attestation WHERE attestation_status = #{status} AND deleted = 0 ORDER BY create_time ASC")
    List<MessageAttestationEntity> findByStatus(@Param("status") String status);

    /**
     * 查询待确认的交易
     */
    @Select("SELECT * FROM message_attestation WHERE attestation_status IN ('SUBMITTING', 'CONFIRMING') AND deleted = 0 ORDER BY create_time ASC")
    List<MessageAttestationEntity> findPendingConfirmations();

    /**
     * 查询超时的待确认交易
     */
    @Select("SELECT * FROM message_attestation WHERE attestation_status IN ('SUBMITTING', 'CONFIRMING') AND update_time < #{timeoutTime} AND deleted = 0")
    List<MessageAttestationEntity> findTimeoutConfirmations(@Param("timeoutTime") LocalDateTime timeoutTime);

    /**
     * 根据区块号查询存证
     */
    @Select("SELECT * FROM message_attestation WHERE block_number = #{blockNumber} AND chain_network = #{chainNetwork} AND deleted = 0")
    List<MessageAttestationEntity> findByBlockNumber(@Param("blockNumber") Long blockNumber, @Param("chainNetwork") String chainNetwork);

    /**
     * 查询时间范围内的存证
     */
    List<MessageAttestationEntity> findByTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("senderId") Long senderId);

    /**
     * 统计存证数量
     */
    @Select("SELECT COUNT(*) FROM message_attestation WHERE sender_id = #{senderId} AND deleted = 0")
    Long countBySenderId(@Param("senderId") Long senderId);

    /**
     * 统计各状态的存证数量
     */
    @Select("SELECT attestation_status, COUNT(*) as count FROM message_attestation WHERE sender_id = #{senderId} AND deleted = 0 GROUP BY attestation_status")
    List<java.util.Map<String, Object>> countByStatus(@Param("senderId") Long senderId);

    /**
     * 更新存证状态
     */
    @Update("UPDATE message_attestation SET attestation_status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新交易确认信息
     */
    @Update("UPDATE message_attestation SET tx_hash = #{txHash}, block_number = #{blockNumber}, block_timestamp = #{blockTimestamp}, " +
            "gas_fee = #{gasFee}, gas_price = #{gasPrice}, attestation_status = 'CONFIRMED', update_time = NOW() WHERE id = #{id}")
    int updateConfirmation(@Param("id") Long id,
                           @Param("txHash") String txHash,
                           @Param("blockNumber") Long blockNumber,
                           @Param("blockTimestamp") LocalDateTime blockTimestamp,
                           @Param("gasFee") java.math.BigDecimal gasFee,
                           @Param("gasPrice") java.math.BigDecimal gasPrice);

    /**
     * 更新失败信息
     */
    @Update("UPDATE message_attestation SET attestation_status = 'FAILED', fail_reason = #{failReason}, " +
            "retry_count = retry_count + 1, update_time = NOW() WHERE id = #{id}")
    int updateFailure(@Param("id") Long id, @Param("failReason") String failReason);

    /**
     * 更新验证次数
     */
    @Update("UPDATE message_attestation SET verify_count = verify_count + 1, last_verify_time = NOW(), update_time = NOW() WHERE id = #{id}")
    int incrementVerifyCount(@Param("id") Long id);

    /**
     * 更新证明数据
     */
    @Update("UPDATE message_attestation SET proof_data = #{proofData, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}, update_time = NOW() WHERE id = #{id}")
    int updateProofData(@Param("id") Long id, @Param("proofData") MessageAttestationEntity.AttestationProof proofData);

    /**
     * 批量插入
     */
    int batchInsert(@Param("list") List<MessageAttestationEntity> entities);

    /**
     * 查询需要重试的失败记录
     */
    @Select("SELECT * FROM message_attestation WHERE attestation_status = 'FAILED' AND retry_count < 3 AND deleted = 0 ORDER BY update_time ASC LIMIT #{limit}")
    List<MessageAttestationEntity> findRetryable(@Param("limit") Integer limit);

    /**
     * 搜索存证
     */
    List<MessageAttestationEntity> search(
            @Param("keyword") String keyword,
            @Param("senderId") Long senderId,
            @Param("status") String status,
            @Param("chainNetwork") String chainNetwork,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 获取链上统计
     */
    @Select("SELECT chain_network, COUNT(*) as count, SUM(gas_fee) as total_gas FROM message_attestation WHERE sender_id = #{senderId} AND attestation_status = 'CONFIRMED' AND deleted = 0 GROUP BY chain_network")
    List<java.util.Map<String, Object>> getChainStatistics(@Param("senderId") Long senderId);

    /**
     * 检查消息是否已存证
     */
    @Select("SELECT EXISTS(SELECT 1 FROM message_attestation WHERE message_id = #{messageId} AND deleted = 0)")
    boolean existsByMessageId(@Param("messageId") String messageId);
}
