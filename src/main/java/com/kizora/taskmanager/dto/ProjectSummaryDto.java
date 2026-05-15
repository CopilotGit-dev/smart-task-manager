package com.kizora.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectSummaryDto {
    private Long projectId;
    private String projectName;
    private long todoCount;
    private long inProgressCount;
    private long inReviewCount;
    private long doneCount;
    private long cancelledCount;
    private long overdueCount;
}
