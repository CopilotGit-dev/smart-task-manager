package com.kizora.taskmanager.dto;

import com.kizora.taskmanager.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeTaskStatusRequest {
    @NotNull
    private TaskStatus status;
    @NotNull
    private Long version;
}
