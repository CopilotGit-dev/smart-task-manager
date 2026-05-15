package com.kizora.taskmanager.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledTaskResponse {
	
	 private Long taskId;
	 private String title;
	 private Integer priorityScore;
	 private LocalDate dueDate;
	 private Double estimatedHours;
}
