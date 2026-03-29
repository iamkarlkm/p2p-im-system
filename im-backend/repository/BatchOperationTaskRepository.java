// 批量操作任务Repository
package com.im.backend.repository;

import com.im.backend.model.BatchOperationTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchOperationTaskRepository extends JpaRepository<BatchOperationTask, String> {

    List<BatchOperationTask> findByOperatorIdOrderByCreatedAtDesc(String operatorId);

    Page<BatchOperationTask> findByOperatorId(String operatorId, Pageable pageable);

    Optional<BatchOperationTask> findByIdAndOperatorId(String id, String operatorId);

    List<BatchOperationTask> findByStatusAndCreatedAtBefore(BatchOperationTask.TaskStatus status, LocalDateTime time);

    @Query("SELECT t FROM BatchOperationTask t WHERE t.operatorId = ?1 AND t.status IN ?2 ORDER BY t.createdAt DESC")
    List<BatchOperationTask> findByOperatorIdAndStatusIn(String operatorId, List<BatchOperationTask.TaskStatus> statuses);

    long countByOperatorIdAndStatus(String operatorId, BatchOperationTask.TaskStatus status);

    @Query("SELECT t FROM BatchOperationTask t WHERE t.status = 'RUNNING' AND t.startTime < ?1")
    List<BatchOperationTask> findRunningTasksStartedBefore(LocalDateTime time);
}
