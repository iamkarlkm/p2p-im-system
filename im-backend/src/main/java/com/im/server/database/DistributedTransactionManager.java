package com.im.server.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 分布式事务管理器
 * 支持：本地事务、分布式事务（两阶段提交）、补偿事务（TCC）
 */
@Component
public class DistributedTransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(DistributedTransactionManager.class);

    // 事务上下文存储
    private final Map<String, TransactionContext> transactionContexts = new ConcurrentHashMap<>();

    // 事务计数器
    private final AtomicInteger transactionCounter = new AtomicInteger(0);

    // 事务超时时间（毫秒）
    private static final long TRANSACTION_TIMEOUT_MS = 30000;

    // 最大重试次数
    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 开启分布式事务
     * @return 事务ID
     */
    public String beginDistributedTransaction() {
        String transactionId = generateTransactionId();
        TransactionContext context = new TransactionContext(transactionId);
        context.setStartTime(System.currentTimeMillis());
        transactionContexts.put(transactionId, context);
        logger.info("开始分布式事务: {}", transactionId);
        return transactionId;
    }

    /**
     * 提交事务
     */
    public boolean commit(String transactionId) {
        TransactionContext context = transactionContexts.get(transactionId);
        if (context == null) {
            logger.error("事务不存在: {}", transactionId);
            return false;
        }

        try {
            // 执行两阶段提交
            boolean prepared = prepare(transactionId);
            if (!prepared) {
                rollback(transactionId);
                return false;
            }

            boolean committed = doCommit(transactionId);
            if (committed) {
                context.setStatus(TransactionStatus.COMMITTED);
                logger.info("事务提交成功: {}", transactionId);
            } else {
                rollback(transactionId);
                return false;
            }

            return true;
        } finally {
            context.setEndTime(System.currentTimeMillis());
            transactionContexts.remove(transactionId);
        }
    }

    /**
     * 回滚事务
     */
    public boolean rollback(String transactionId) {
        TransactionContext context = transactionContexts.get(transactionId);
        if (context == null) {
            logger.error("事务不存在: {}", transactionId);
            return false;
        }

        try {
            // 执行回滚
            boolean rolledBack = doRollback(transactionId);
            if (rolledBack) {
                context.setStatus(TransactionStatus.ROLLED_BACK);
                logger.info("事务回滚成功: {}", transactionId);
            }
            return rolledBack;
        } finally {
            context.setEndTime(System.currentTimeMillis());
            transactionContexts.remove(transactionId);
        }
    }

    /**
     * 两阶段提交 - 准备阶段
     */
    private boolean prepare(String transactionId) {
        TransactionContext context = transactionContexts.get(transactionId);
        if (context == null) return false;

        logger.info("执行两阶段提交 - 准备阶段: {}", transactionId);

        // 检查超时
        if (isTimedOut(context)) {
            logger.error("事务准备超时: {}", transactionId);
            return false;
        }

        // 所有参与者准备提交
        for (TransactionParticipant participant : context.getParticipants()) {
            try {
                boolean result = participant.prepare();
                if (!result) {
                    logger.error("参与者准备失败: {}", participant.getParticipantId());
                    return false;
                }
            } catch (Exception e) {
                logger.error("参与者准备异常: {}", participant.getParticipantId(), e);
                return false;
            }
        }

        context.setStatus(TransactionStatus.PREPARED);
        return true;
    }

    /**
     * 两阶段提交 - 提交阶段
     */
    private boolean doCommit(String transactionId) {
        TransactionContext context = transactionContexts.get(transactionId);
        if (context == null) return false;

        logger.info("执行两阶段提交 - 提交阶段: {}", transactionId);

        for (TransactionParticipant participant : context.getParticipants()) {
            try {
                participant.commit();
            } catch (Exception e) {
                logger.error("参与者提交失败: {}", participant.getParticipantId(), e);
                // 记录失败，但继续提交其他参与者
            }
        }

        return true;
    }

    /**
     * 执行回滚
     */
    private boolean doRollback(String transactionId) {
        TransactionContext context = transactionContexts.get(transactionId);
        if (context == null) return false;

        logger.info("执行事务回滚: {}", transactionId);

        for (TransactionParticipant participant : context.getParticipants()) {
            try {
                participant.rollback();
            } catch (Exception e) {
                logger.error("参与者回滚失败: {}", participant.getParticipantId(), e);
                // 记录失败，但继续回滚其他参与者
            }
        }

        return true;
    }

    /**
     * 注册事务参与者
     */
    public void registerParticipant(String transactionId, TransactionParticipant participant) {
        TransactionContext context = transactionContexts.get(transactionId);
        if (context != null) {
            context.addParticipant(participant);
            logger.debug("注册事务参与者: {} -> {}", transactionId, participant.getParticipantId());
        }
    }

    /**
     * TCC 补偿事务 - Try
     */
    public boolean tryExecute(String transactionId, TCCOperation operation) {
        TransactionContext context = transactionContexts.get(transactionId);
        if (context == null) {
            transactionId = beginDistributedTransaction();
            context = transactionContexts.get(transactionId);
        }

        try {
            boolean result = operation.tryExecute();
            if (result) {
                TCCParticipant tccParticipant = new TCCParticipant(operation);
                context.addParticipant(tccParticipant);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("TCC Try执行失败", e);
            return false;
        }
    }

    /**
     * TCC 补偿事务 - Confirm
     */
    public boolean confirm(String transactionId) {
        return commit(transactionId);
    }

    /**
     * TCC 补偿事务 - Cancel
     */
    public boolean cancel(String transactionId) {
        return rollback(transactionId);
    }

    /**
     * 检查事务是否超时
     */
    private boolean isTimedOut(TransactionContext context) {
        long elapsed = System.currentTimeMillis() - context.getStartTime();
        return elapsed > TRANSACTION_TIMEOUT_MS;
    }

    /**
     * 生成事务ID
     */
    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis() + "-" + transactionCounter.getAndIncrement();
    }

    /**
     * 获取事务状态
     */
    public TransactionStatus getTransactionStatus(String transactionId) {
        TransactionContext context = transactionContexts.get(transactionId);
        return context != null ? context.getStatus() : null;
    }

    // ==================== 内部类 ====================

    /**
     * 事务上下文
     */
    public static class TransactionContext {
        private final String transactionId;
        private TransactionStatus status = TransactionStatus.ACTIVE;
        private long startTime;
        private long endTime;
        private final ConcurrentHashMap<String, Object> resources = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, TransactionParticipant> participants = new ConcurrentHashMap<>();

        public TransactionContext(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public TransactionStatus getStatus() {
            return status;
        }

        public void setStatus(TransactionStatus status) {
            this.status = status;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public Map<String, Object> getResources() {
            return resources;
        }

        public ConcurrentHashMap<String, TransactionParticipant> getParticipants() {
            return participants;
        }

        public void addParticipant(TransactionParticipant participant) {
            participants.put(participant.getParticipantId(), participant);
        }
    }

    /**
     * 事务参与者接口
     */
    public interface TransactionParticipant {
        String getParticipantId();
        boolean prepare();
        void commit();
        void rollback();
    }

    /**
     * TCC参与者
     */
    public static class TCCParticipant implements TransactionParticipant {
        private final TCCOperation operation;
        private final String participantId;

        public TCCParticipant(TCCOperation operation) {
            this.operation = operation;
            this.participantId = "TCC-" + UUID.randomUUID().toString().substring(0, 8);
        }

        @Override
        public String getParticipantId() {
            return participantId;
        }

        @Override
        public boolean prepare() {
            // Try已经执行，这里直接返回成功
            return true;
        }

        @Override
        public void commit() {
            operation.confirm();
        }

        @Override
        public void rollback() {
            operation.cancel();
        }
    }

    /**
     * TCC操作接口
     */
    public interface TCCOperation {
        boolean tryExecute();
        void confirm();
        void cancel();
    }

    /**
     * 事务状态枚举
     */
    public enum TransactionStatus {
        ACTIVE,
        PREPARED,
        COMMITTED,
        ROLLED_BACK,
        TIMED_OUT
    }
}
