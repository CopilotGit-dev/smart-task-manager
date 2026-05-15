package com.kizora.taskmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignTaskRequest {
    @NotNull
    private Long assigneeId;
    @NotNull
    private Long version;
}
