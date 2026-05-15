package com.kizora.taskmanager.dto;

import com.kizora.taskmanager.domain.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateProjectRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Long ownerId;
    @NotNull
    private ProjectStatus status;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
}
