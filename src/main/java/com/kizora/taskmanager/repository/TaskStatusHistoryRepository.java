package com.kizora.taskmanager.repository;

import com.kizora.taskmanager.domain.Task;
import com.kizora.taskmanager.domain.TaskStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistory, Long> {
    List<TaskStatusHistory> findByTaskOrderByChangedAtAsc(Task task);
}
