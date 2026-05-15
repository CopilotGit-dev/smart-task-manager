package com.kizora.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DeveloperWorkloadItem {
    private Long developerId;
    private String developerName;
    private long todoCount;
    private long inProgressCount;
    private long inReviewCount;
    private long doneCount;
    private long cancelledCount;
    private long overdueCount;
    private BigDecimal totalEstimatedHours;
}
