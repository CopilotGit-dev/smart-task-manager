package com.kizora.taskmanager.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kizora.taskmanager.dto.ScheduledTaskRequest;
import com.kizora.taskmanager.dto.ScheduledTaskResponse;

@Component
public class TaskScheduler {
	
	public List<ScheduledTaskResponse> schedule(List<ScheduledTaskRequest> tasks) {
        PriorityQueue<ScheduledTaskRequest> minHeap = new PriorityQueue<>(
                Comparator
                        .comparing(ScheduledTaskRequest::getDueDate, Comparator.nullsLast(LocalDate::compareTo))
                        .thenComparing(ScheduledTaskRequest::getPriorityScore, Comparator.reverseOrder())
                        .thenComparing(ScheduledTaskRequest::getEstimatedHours, Comparator.nullsLast(Double::compareTo))
        );

        minHeap.addAll(tasks);

        return minHeap.stream()
                .sorted(
                        Comparator
                                .comparing(ScheduledTaskRequest::getDueDate, Comparator.nullsLast(LocalDate::compareTo))
                                .thenComparing(ScheduledTaskRequest::getPriorityScore, Comparator.reverseOrder())
                                .thenComparing(ScheduledTaskRequest::getEstimatedHours, Comparator.nullsLast(Double::compareTo))
                )
                .map(t -> ScheduledTaskResponse.builder()
                        .taskId(t.getTaskId())
                        .title(t.getTitle())
                        .priorityScore(t.getPriorityScore())
                        .dueDate(t.getDueDate())
                        .estimatedHours(t.getEstimatedHours())
                        .build())
                .collect(Collectors.toList());
    }


}
