package com.kizora.taskmanager.controller;

import com.kizora.taskmanager.dto.DeveloperWorkloadItem;
import com.kizora.taskmanager.dto.ProjectSummaryDto;
import com.kizora.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final TaskService taskService;

    @GetMapping("/developer-workload")
    public List<DeveloperWorkloadItem> developerWorkload(@RequestParam Long projectId) {
        return taskService.getDeveloperWorkload(projectId);
    }

    @GetMapping("/project-summary")
    public ProjectSummaryDto projectSummary(@RequestParam Long projectId) {
        return taskService.getProjectSummary(projectId);
    }
}
