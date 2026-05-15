package com.kizora.taskmanager.controller;

import com.kizora.taskmanager.dto.ScheduledTaskRequest;
import com.kizora.taskmanager.dto.ScheduledTaskResponse;
import com.kizora.taskmanager.service.TaskScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class TaskSchedulerController {

	private final TaskScheduler taskScheduler;

    public List<ScheduledTaskResponse> schedule(List<ScheduledTaskRequest> tasks) {
        Comparator<ScheduledTaskRequest> comparator =
                Comparator.comparing(
                                (ScheduledTaskRequest t) -> t.getDueDate(),
                                Comparator.nullsLast(LocalDate::compareTo)
                        )
                        .thenComparing(
                                (ScheduledTaskRequest t) -> t.getPriorityScore(),
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                        .thenComparing(
                                (ScheduledTaskRequest t) -> t.getEstimatedHours(),
                                Comparator.nullsLast(Double::compareTo)
                        );

        PriorityQueue<ScheduledTaskRequest> minHeap = new PriorityQueue<>(comparator);
        minHeap.addAll(tasks);

        List<ScheduledTaskResponse> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            ScheduledTaskRequest t = minHeap.poll();
            result.add(ScheduledTaskResponse.builder()
                    .taskId(t.getTaskId())
                    .title(t.getTitle())
                    .priorityScore(t.getPriorityScore())
                    .dueDate(t.getDueDate())
                    .estimatedHours(t.getEstimatedHours())
                    .build());
        }
        return result;
    }

}
