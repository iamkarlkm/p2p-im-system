package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "im_call_record",
       indexes = {
           @Index(name = "idx_call_caller", columnList = "caller_id"),
           @Index(name = "idx_call_callee", columnList = "callee_id"),
           @Index(name = "idx_call_conversation", columnList = "conversation_id"),
           @Index(name = "idx_call_time", columnList = "start_time")
       })
public class CallRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 通话会话ID */
    @Column(name = "call_id", nullable = false, unique = true, length = 64)
    private String callId;

    /** 发起人ID */
    @Column(name = "caller_id", nullable = false, length = 64)
    private String callerId;

    /** 接听人ID */
    @Column(name = "callee_id", nullable = false, length = 64)
    private String calleeId;

    /** 对应会话ID */
    @Column(name = "conversation_id", nullable = false, length = 64)
    private String conversationId;

    /** 通话类型: AUDIO / VIDEO */
    @Column(name = "call_type", nullable = false, length = 16)
    private String callType;

    /** 通话状态: INITIATED / RINGING / ANSWERED / ENDED / MISSED / REJECTED / FAILED */
    @Column(name = "status", nullable = false, length = 16)
    private String status;

    /** 开始时间 */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /** 接听时间 */
    @Column(name = "answer_time")
    private LocalDateTime answerTime;

    /** 结束时间 */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /** 通话时长(秒) */
    @Column(name = "duration")
    private Integer duration;

    /** 发起人是否主动结束 */
    @Column(name = "ended_by_caller")
    private Boolean endedByCaller;

    @PrePersist
    public void prePersist() {
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "INITIATED";
        }
    }
}
