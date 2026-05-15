package com.kizora.taskmanager.controller;

import com.kizora.taskmanager.domain.ProjectStatus;
import com.kizora.taskmanager.dto.CreateProjectRequest;
import com.kizora.taskmanager.dto.ProjectDto;
import com.kizora.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping("/{id}")
    public ProjectDto get(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @GetMapping
    public List<ProjectDto> list() {
        return projectService.getAll();
    }

    @PutMapping("/{id}/status")
    public ProjectDto updateStatus(@PathVariable Long id, @RequestParam ProjectStatus status) {
        return projectService.updateStatus(id, status);
    }
}
