package com.kizora.taskmanager.dto;

import com.kizora.taskmanager.domain.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private TaskPriority priority;
    private LocalDate dueDate;
    private BigDecimal estimatedHours;
    private Long assigneeId;
}
