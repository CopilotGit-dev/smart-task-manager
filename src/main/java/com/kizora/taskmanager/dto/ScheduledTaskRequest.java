package com.kizora.taskmanager.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskRequest {
	
	private Long taskId;
    private String title;
    private Integer priorityScore;
    private LocalDate dueDate;
    private Double estimatedHours;

}
