
package com.kizora.taskmanager.bootstrap;

import com.kizora.taskmanager.domain.*;
import com.kizora.taskmanager.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class SampleDataLoader {

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository,
                                      ProjectRepository projectRepository,
                                      TaskRepository taskRepository,
                                      ProjectMemberRepository projectMemberRepository) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .fullName("Admin User")
                    .role(UserRole.ADMIN)
                    .build();
            admin = userRepository.save(admin);

            User manager = User.builder()
                    .username("manager")
                    .email("manager@example.com")
                    .fullName("Manager User")
                    .role(UserRole.MANAGER)
                    .build();
            manager = userRepository.save(manager);

            User dev1 = User.builder()
                    .username("dev1")
                    .email("dev1@example.com")
                    .fullName("Nilesh")
                    .role(UserRole.DEVELOPER)
                    .build();
            dev1 = userRepository.save(dev1);

            User dev2 = User.builder()
                    .username("dev2")
                    .email("dev2@example.com")
                    .fullName("Gaurav")
                    .role(UserRole.DEVELOPER)
                    .build();
            dev2 = userRepository.save(dev2);

            Project project = Project.builder()
                    .name("Sample Project")
                    .description("Demo project for assignment")
                    .owner(manager)
                    .status(ProjectStatus.ACTIVE)
                    .startDate(LocalDate.now().minusDays(7))
                    .endDate(LocalDate.now().plusDays(30))
                    .build();
            project = projectRepository.save(project);

            // project members
            projectMemberRepository.save(ProjectMember.builder().project(project).user(manager).build());
            projectMemberRepository.save(ProjectMember.builder().project(project).user(dev1).build());
            projectMemberRepository.save(ProjectMember.builder().project(project).user(dev2).build());

            Task task1 = Task.builder()
                    .title("Setup project skeleton")
                    .description("Initialize Spring Boot project and base entities")
                    .project(project)
                    .assignee(dev1)
                    .status(TaskStatus.IN_PROGRESS)
                    .priority(TaskPriority.HIGH)
                    .dueDate(LocalDate.now().plusDays(3))
                    .estimatedHours(new BigDecimal("6.0"))
                    .build();
            taskRepository.save(task1);

            Task task2 = Task.builder()
                    .title("Implement business rules")
                    .description("Add status transitions, assignment rules, and project rules")
                    .project(project)
                    .assignee(dev2)
                    .status(TaskStatus.TODO)
                    .priority(TaskPriority.CRITICAL)
                    .dueDate(LocalDate.now().plusDays(5))
                    .estimatedHours(new BigDecimal("10.0"))
                    .build();
            taskRepository.save(task2);
        };
    }
}
