package com.kizora.taskmanager.repository;

import com.kizora.taskmanager.domain.Project;
import com.kizora.taskmanager.domain.ProjectStatus;
import com.kizora.taskmanager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner(User owner);
    List<Project> findByStatus(ProjectStatus status);
}
