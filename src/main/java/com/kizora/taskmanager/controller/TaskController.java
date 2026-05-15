package com.kizora.taskmanager.controller;

import com.kizora.taskmanager.dto.*;
import com.kizora.taskmanager.domain.TaskStatusHistory;
import com.kizora.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@PathVariable Long projectId,
                              @Valid @RequestBody CreateTaskRequest request,
                              @RequestHeader("X-User-Id") Long actingUserId) {
        return taskService.createTask(projectId, request, actingUserId);
    }

    @GetMapping("/tasks/{taskId}")
    public TaskDto getTask(@PathVariable Long taskId) {
        return taskService.getTask(taskId);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskDto> getTasksByProject(@PathVariable Long projectId) {
        return taskService.getTasksByProject(projectId);
    }

    @PostMapping("/tasks/{taskId}/status")
    public TaskDto changeStatus(@PathVariable Long taskId,
                                @Valid @RequestBody ChangeTaskStatusRequest request,
                                @RequestHeader("X-User-Id") Long actingUserId) {
        return taskService.changeStatus(taskId, request, actingUserId);
    }

    @PostMapping("/tasks/{taskId}/assign")
    public TaskDto assignTask(@PathVariable Long taskId,
                              @Valid @RequestBody AssignTaskRequest request,
                              @RequestHeader("X-User-Id") Long actingUserId) {
        return taskService.assignTask(taskId, request, actingUserId);
    }

    @GetMapping("/tasks/{taskId}/history")
    public List<TaskStatusHistory> history(@PathVariable Long taskId) {
        return taskService.getHistory(taskId);
    }
}
