package com.kizora.taskmanager.service;

import com.kizora.taskmanager.domain.Task;
import com.kizora.taskmanager.domain.TaskStatus;
import com.kizora.taskmanager.domain.TaskStatusHistory;
import com.kizora.taskmanager.dto.LeaderboardResponse;
import com.kizora.taskmanager.dto.OverdueTaskResponse;
import com.kizora.taskmanager.dto.WeeklyVelocityResponse;
import com.kizora.taskmanager.repository.TaskRepository;
import com.kizora.taskmanager.repository.TaskStatusHistoryRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {
	
	private final TaskRepository taskRepository;
    private final TaskStatusHistoryRepository taskStatusHistoryRepository;

    public List<OverdueTaskResponse> getOverdueTasks(Long projectId) {
        List<Task> tasks = taskRepository.findOverdueTasksByProject(projectId, LocalDate.now());

        return tasks.stream()
                .map(task -> OverdueTaskResponse.builder()
                        .taskId(task.getId())
                        .title(task.getTitle())
                        .status(task.getStatus())
                        .priority(task.getPriority())
                        .dueDate(task.getDueDate())
                        .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                        .assigneeName(task.getAssignee() != null ? task.getAssignee().getFullName() : null)
                        .build())
                .toList();
    }

    public List<WeeklyVelocityResponse> getProjectVelocity(Long projectId) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusWeeks(3).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDateTime startDateTime = startDate.atStartOfDay();

        List<TaskStatusHistory> history = taskStatusHistoryRepository
                .findDoneTransitionsByProjectSince(projectId, TaskStatus.DONE, startDateTime);

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        Map<String, Long> grouped = history.stream()
                .collect(Collectors.groupingBy(
                        h -> {
                            int week = h.getChangedAt().get(weekFields.weekOfWeekBasedYear());
                            int year = h.getChangedAt().get(weekFields.weekBasedYear());
                            return year + "-W" + week;
                        },
                        Collectors.counting()
                ));

        List<WeeklyVelocityResponse> result = new ArrayList<>();
        for (int i = 3; i >= 0; i--) {
            LocalDate weekDate = today.minusWeeks(i);
            int week = weekDate.get(weekFields.weekOfWeekBasedYear());
            int year = weekDate.get(weekFields.weekBasedYear());
            String key = year + "-W" + week;

            result.add(WeeklyVelocityResponse.builder()
                    .weekLabel(key)
                    .completedCount(grouped.getOrDefault(key, 0L))
                    .build());
        }

        return result;
    }

    public List<LeaderboardResponse> getLeaderboard() {
        LocalDateTime since = LocalDateTime.now().minusDays(30);

        return taskStatusHistoryRepository.findTopCompletedUsersSince(TaskStatus.DONE, since)
                .stream()
                .limit(5)
                .map(row -> LeaderboardResponse.builder()
                        .userId((Long) row[0])
                        .username((String) row[1])
                        .fullName((String) row[2])
                        .completedTasks((Long) row[3])
                        .build())
                .sorted(Comparator.comparing(LeaderboardResponse::getCompletedTasks).reversed())
                .toList();
    }
	

}
