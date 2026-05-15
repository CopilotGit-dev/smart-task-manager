package com.kizora.taskmanager.service;

import com.kizora.taskmanager.domain.*;
import com.kizora.taskmanager.dto.CreateProjectRequest;
import com.kizora.taskmanager.dto.ProjectDto;
import com.kizora.taskmanager.exception.BusinessException;
import com.kizora.taskmanager.exception.ResourceNotFoundException;
import com.kizora.taskmanager.repository.ProjectMemberRepository;
import com.kizora.taskmanager.repository.ProjectRepository;
import com.kizora.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserService userService;

    @Transactional
    public ProjectDto createProject(CreateProjectRequest request) {
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("Project endDate cannot be earlier than startDate");
        }
        User owner = userService.findById(request.getOwnerId());
        if (owner.getRole() != UserRole.MANAGER && owner.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Project owner must be MANAGER or ADMIN");
        }
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .status(request.getStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        project = projectRepository.save(project);

        // automatically add owner as project member
        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(owner)
                .build();
        projectMemberRepository.save(member);

        return toDto(project);
    }

    public ProjectDto getProject(Long id) {
        return toDto(findById(id));
    }

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    @Transactional
    public ProjectDto updateStatus(Long projectId, ProjectStatus newStatus) {
        Project project = findById(projectId);
        if (project.getEndDate() != null && project.getEndDate().isBefore(project.getStartDate())) {
            throw new BusinessException("Project endDate cannot be earlier than startDate");
        }
        if (newStatus == ProjectStatus.ARCHIVED) {
            List<Task> tasks = taskRepository.findByProject(project);
            boolean allDoneOrCancelled = tasks.stream().allMatch(t -> t.getStatus() == TaskStatus.DONE || t.getStatus() == TaskStatus.CANCELLED);
            if (!allDoneOrCancelled) {
                throw new BusinessException("Project cannot be archived unless all tasks are DONE or CANCELLED");
            }
        }
        project.setStatus(newStatus);
        return toDto(projectRepository.save(project));
    }

    public List<ProjectDto> getAll() {
        return projectRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private ProjectDto toDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setOwnerId(project.getOwner().getId());
        dto.setStatus(project.getStatus());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        return dto;
    }
}
