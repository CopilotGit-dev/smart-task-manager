package com.kizora.taskmanager.repository;

import com.kizora.taskmanager.domain.Task;
import com.kizora.taskmanager.domain.TaskStatus;
import com.kizora.taskmanager.domain.TaskStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistory, Long> {
    List<TaskStatusHistory> findByTaskOrderByChangedAtAsc(Task task);
    @Query("""
            SELECT h
            FROM TaskStatusHistory h
            WHERE h.task.project.id = :projectId
              AND h.toStatus = :doneStatus
              AND h.changedAt >= :since
            ORDER BY h.changedAt ASC
            """)
        List<TaskStatusHistory> findDoneTransitionsByProjectSince(@Param("projectId") Long projectId,
                                                                  @Param("doneStatus") TaskStatus doneStatus,
                                                                  @Param("since") LocalDateTime since);

        @Query("""
            SELECT h.changedBy.id, h.changedBy.username, h.changedBy.fullName, COUNT(h.id)
            FROM TaskStatusHistory h
            WHERE h.toStatus = :doneStatus
              AND h.changedAt >= :since
            GROUP BY h.changedBy.id, h.changedBy.username, h.changedBy.fullName
            ORDER BY COUNT(h.id) DESC
            """)
        List<Object[]> findTopCompletedUsersSince(@Param("doneStatus") TaskStatus doneStatus,
                                                  @Param("since") LocalDateTime since);
}
