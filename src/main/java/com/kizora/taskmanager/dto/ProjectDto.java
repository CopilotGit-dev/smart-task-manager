package com.kizora.taskmanager.dto;

import com.kizora.taskmanager.domain.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}
