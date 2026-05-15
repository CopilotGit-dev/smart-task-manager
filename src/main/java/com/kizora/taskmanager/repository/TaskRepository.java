package com.kizora.taskmanager.repository;

import com.kizora.taskmanager.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignee(User assignee);
    List<Task> findByProjectAndStatus(Project project, TaskStatus status);
    List<Task> findByProjectAndDueDateBeforeAndStatusNot(Project project, LocalDate date, TaskStatus status);
    @Query("""
            SELECT t
            FROM Task t
            WHERE t.project.id = :projectId
              AND t.dueDate < :today
              AND t.status NOT IN :excludedStatuses
            ORDER BY
              CASE
                WHEN t.priority = com.kizora.taskmanager.domain.TaskPriority.CRITICAL THEN 4
                WHEN t.priority = com.kizora.taskmanager.domain.TaskPriority.HIGH THEN 3
                WHEN t.priority = com.kizora.taskmanager.domain.TaskPriority.MEDIUM THEN 2
                WHEN t.priority = com.kizora.taskmanager.domain.TaskPriority.LOW THEN 1
                ELSE 0
              END DESC,
              t.dueDate ASC
            """)
        List<Task> findOverdueTasksByProject(@Param("projectId") Long projectId,
                                             @Param("today") LocalDate today,
                                             @Param("excludedStatuses") List<TaskStatus> excludedStatuses);

        default List<Task> findOverdueTasksByProject(Long projectId, LocalDate today) {
            return findOverdueTasksByProject(
                    projectId,
                    today,
                    List.of(TaskStatus.DONE, TaskStatus.CANCELLED)
            );
        }
}
