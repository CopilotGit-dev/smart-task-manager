package com.kizora.taskmanager.dto;

import java.time.LocalDate;

import com.kizora.taskmanager.domain.TaskPriority;
import com.kizora.taskmanager.domain.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverdueTaskResponse {
	private Long taskId;
    private String title;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long assigneeId;
    private String assigneeName;

}
