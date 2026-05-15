package com.kizora.taskmanager.repository;

import com.kizora.taskmanager.domain.Project;
import com.kizora.taskmanager.domain.ProjectMember;
import com.kizora.taskmanager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    Optional<ProjectMember> findByProjectAndUser(Project project, User user);
}
