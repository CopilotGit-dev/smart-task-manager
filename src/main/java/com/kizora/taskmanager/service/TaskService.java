package com.kizora.taskmanager.service;

import com.kizora.taskmanager.domain.*;
import com.kizora.taskmanager.dto.*;
import com.kizora.taskmanager.exception.AccessDeniedException;
import com.kizora.taskmanager.exception.BusinessException;
import com.kizora.taskmanager.exception.ResourceNotFoundException;
import com.kizora.taskmanager.repository.ProjectMemberRepository;
import com.kizora.taskmanager.repository.TaskRepository;
import com.kizora.taskmanager.repository.TaskStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusHistoryRepository statusHistoryRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectService projectService;
    private final UserService userService;

    private static final Map<TaskStatus, List<TaskStatus>> allowedTransitions = new HashMap<>();

    static {
        allowedTransitions.put(TaskStatus.TODO, List.of(TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED));
        allowedTransitions.put(TaskStatus.IN_PROGRESS, List.of(TaskStatus.IN_REVIEW, TaskStatus.TODO, TaskStatus.CANCELLED));
        allowedTransitions.put(TaskStatus.IN_REVIEW, List.of(TaskStatus.DONE, TaskStatus.IN_PROGRESS));
        allowedTransitions.put(TaskStatus.DONE, List.of());
        allowedTransitions.put(TaskStatus.CANCELLED, List.of());
    }

    @Transactional
    public TaskDto createTask(Long projectId, CreateTaskRequest request, Long actingUserId) {
        Project project = projectService.findById(projectId);
        if (project.getStatus() == ProjectStatus.ON_HOLD || project.getStatus() == ProjectStatus.COMPLETED || project.getStatus() == ProjectStatus.ARCHIVED) {
            throw new BusinessException("Cannot create tasks for project in status: " + project.getStatus());
        }
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .project(project)
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .estimatedHours(request.getEstimatedHours())
                .status(TaskStatus.TODO)
                .build();

        if (request.getAssigneeId() != null) {
            assignTaskInternal(task, request.getAssigneeId(), actingUserId, true);
        }

        task = taskRepository.save(task);

        TaskStatusHistory history = TaskStatusHistory.builder()
                .task(task)
                .fromStatus(null)
                .toStatus(task.getStatus())
                .changedAt(LocalDateTime.now())
                .changedBy(userService.findById(actingUserId))
                .build();
        statusHistoryRepository.save(history);

        return toDto(task);
    }

    public TaskDto getTask(Long taskId) {
        return toDto(findById(taskId));
    }

    public List<TaskDto> getTasksByProject(Long projectId) {
        Project project = projectService.findById(projectId);
        return taskRepository.findByProject(project).stream().map(this::toDto).collect(Collectors.toList());
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    @Transactional
    public TaskDto changeStatus(Long taskId, ChangeTaskStatusRequest request, Long actingUserId) {
        Task task = findById(taskId);
        if (!Objects.equals(task.getVersion(), request.getVersion())) {
            throw new BusinessException("Stale task version. Please reload before updating.");
        }
        TaskStatus current = task.getStatus();
        TaskStatus target = request.getStatus();
        List<TaskStatus> allowed = allowedTransitions.getOrDefault(current, List.of());
        if (!allowed.contains(target)) {
            throw new BusinessException("Invalid status transition from " + current + " to " + target);
        }
        task.setStatus(target);
        Task saved = taskRepository.save(task);

        TaskStatusHistory history = TaskStatusHistory.builder()
                .task(saved)
                .fromStatus(current)
                .toStatus(target)
                .changedAt(LocalDateTime.now())
                .changedBy(userService.findById(actingUserId))
                .build();
        statusHistoryRepository.save(history);

        return toDto(saved);
    }

    @Transactional
    public TaskDto assignTask(Long taskId, AssignTaskRequest request, Long actingUserId) {
        Task task = findById(taskId);
        if (!Objects.equals(task.getVersion(), request.getVersion())) {
            throw new BusinessException("Stale task version. Please reload before updating.");
        }
        assignTaskInternal(task, request.getAssigneeId(), actingUserId, false);
        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    private void assignTaskInternal(Task task, Long assigneeId, Long actingUserId, boolean allowOnCreate) {
        User actingUser = userService.findById(actingUserId);
        if (!allowOnCreate) {
            if (actingUser.getRole() != UserRole.MANAGER && actingUser.getRole() != UserRole.ADMIN) {
                throw new AccessDeniedException("Only MANAGER or ADMIN can assign or reassign tasks");
            }
        }
        if (task.getStatus() == TaskStatus.DONE || task.getStatus() == TaskStatus.CANCELLED) {
            throw new BusinessException("DONE or CANCELLED tasks cannot be reassigned");
        }
        User assignee = userService.findById(assigneeId);
        if (assignee.getRole() != UserRole.DEVELOPER) {
            throw new BusinessException("Task can only be assigned to a DEVELOPER");
        }
        Project project = task.getProject();
        projectMemberRepository.findByProjectAndUser(project, assignee)
                .orElseThrow(() -> new BusinessException("Assignee must be a member of the project"));
        task.setAssignee(assignee);
    }

    public List<TaskStatusHistory> getHistory(Long taskId) {
        Task task = findById(taskId);
        return statusHistoryRepository.findByTaskOrderByChangedAtAsc(task);
    }

    public List<DeveloperWorkloadItem> getDeveloperWorkload(Long projectId) {
        Project project = projectService.findById(projectId);
        List<Task> tasks = taskRepository.findByProject(project);
        Map<Long, DeveloperWorkloadItem> map = new HashMap<>();
        LocalDate today = LocalDate.now();
        for (Task t : tasks) {
            if (t.getAssignee() == null) continue;
            Long devId = t.getAssignee().getId();
            DeveloperWorkloadItem item = map.computeIfAbsent(devId, id -> new DeveloperWorkloadItem(
                    id,
                    t.getAssignee().getFullName(),
                    0,0,0,0,0,0,
                    java.math.BigDecimal.ZERO
            ));
            switch (t.getStatus()) {
                case TODO -> item.setTodoCount(item.getTodoCount() + 1);
                case IN_PROGRESS -> item.setInProgressCount(item.getInProgressCount() + 1);
                case IN_REVIEW -> item.setInReviewCount(item.getInReviewCount() + 1);
                case DONE -> item.setDoneCount(item.getDoneCount() + 1);
                case CANCELLED -> item.setCancelledCount(item.getCancelledCount() + 1);
            }
            if (t.getDueDate() != null && t.getDueDate().isBefore(today) && t.getStatus() != TaskStatus.DONE && t.getStatus() != TaskStatus.CANCELLED) {
                item.setOverdueCount(item.getOverdueCount() + 1);
            }
            if (t.getEstimatedHours() != null) {
                item.setTotalEstimatedHours(item.getTotalEstimatedHours().add(t.getEstimatedHours()));
            }
        }
        return map.values().stream().collect(Collectors.toList());
    }

    public ProjectSummaryDto getProjectSummary(Long projectId) {
        Project project = projectService.findById(projectId);
        List<Task> tasks = taskRepository.findByProject(project);
        long todo = 0, inProgress = 0, inReview = 0, done = 0, cancelled = 0, overdue = 0;
        LocalDate today = LocalDate.now();
        for (Task t : tasks) {
            switch (t.getStatus()) {
                case TODO -> todo++;
                case IN_PROGRESS -> inProgress++;
                case IN_REVIEW -> inReview++;
                case DONE -> done++;
                case CANCELLED -> cancelled++;
            }
            if (t.getDueDate() != null && t.getDueDate().isBefore(today) && t.getStatus() != TaskStatus.DONE && t.getStatus() != TaskStatus.CANCELLED) {
                overdue++;
            }
        }
        return new ProjectSummaryDto(project.getId(), project.getName(), todo, inProgress, inReview, done, cancelled, overdue);
    }

    private TaskDto toDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setProjectId(task.getProject().getId());
        dto.setAssigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null);
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setEstimatedHours(task.getEstimatedHours());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setVersion(task.getVersion());
        return dto;
    }
}
