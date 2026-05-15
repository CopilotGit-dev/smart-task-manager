package com.kizora.taskmanager.repository;

import com.kizora.taskmanager.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignee(User assignee);
    List<Task> findByProjectAndStatus(Project project, TaskStatus status);
    List<Task> findByProjectAndDueDateBeforeAndStatusNot(Project project, LocalDate date, TaskStatus status);
}
