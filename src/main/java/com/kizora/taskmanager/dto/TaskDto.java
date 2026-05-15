package com.kizora.taskmanager.dto;

import com.kizora.taskmanager.domain.TaskPriority;
import com.kizora.taskmanager.domain.TaskStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Long projectId;
    private Long assigneeId;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private BigDecimal estimatedHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
