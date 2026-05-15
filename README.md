# Smart Task Manager with Analytics

## Project Overview
Smart Task Manager with Analytics is a backend REST API built using Java, Spring Boot, and MySQL for managing users, projects, tasks, assignments, status transitions, and reporting. Each task belongs to a project and can be assigned to a user, while the system also tracks task status history, enforces business rules, prevents silent concurrent overwrites using optimistic locking, and exposes analytics endpoints for reporting and productivity insights.

## Tech Stack
This project is implemented using the assignment-required stack:

- Java 17
- Spring Boot 3.x
- Spring Data JPA + Hibernate
- MySQL 8.x
- Maven
- Lombok
- Spring Validation
- springdoc-openapi / Swagger UI
- Git + GitHub

## Prerequisites
Before running the project locally, install the following:

- JDK 17 (or JDK 11 if the project is configured for Java 11)
- Apache Maven 3.8+
- MySQL Server 8.x
- Git
- Postman or access to Swagger UI for API testing
- An IDE such as STS / IntelliJ IDEA / VS Code / Eclipse (optional but recommended)

## Database Setup
The assignment requires SQL/schema documentation and explicitly states that Hibernate auto-DDL alone is not sufficient.

### 1. Create the database
```sql
CREATE DATABASE smart_task_manager;
```

### 2. Use the database
```sql
USE smart_task_manager;
```

### 3. Core tables
The system is designed around the following core tables:
- `users`
- `projects`
- `tasks`
- `task_status_history`
- `project_members`

### 4. Optional starter SQL
```sql
CREATE DATABASE IF NOT EXISTS smart_task_manager;
```

## How to Run
### 1. Clone the repository
```bash
git clone https://github.com/CopilotGit-dev/smart-task-manager.git
cd smart-task-manager
```

### 2. Create the database in MySQL
```sql
CREATE DATABASE smart_task_manager;
```

### 3. Update environment properties
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_task_manager?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
server.port=8080
```

### 4. Build and run the application
```bash
mvn clean install
mvn spring-boot:run
```

### 5. Access the application
- Base URL: `http://localhost:8080`
- Swagger UI (if enabled): `http://localhost:8080/swagger-ui/index.html`

## Environment Configuration
Do not commit real credentials. Use placeholder values in source config and configure real values locally or through environment-specific configuration.

| Property | Description |
|---|---|
| `spring.datasource.url` | MySQL JDBC URL |
| `spring.datasource.username` | MySQL username |
| `spring.datasource.password` | MySQL password |
| `spring.jpa.hibernate.ddl-auto` | Schema management mode (`update`, `validate`, etc.) |
| `spring.jpa.show-sql` | Enables SQL logging |
| `spring.jpa.properties.hibernate.format_sql` | Formats SQL output |
| `server.port` | Application port |

## API Reference

### User Management
| Method | Path | Request Summary | Response Summary |
|---|---|---|---|
| POST | `/api/users` | Create a new user with username, email, full name, role | Returns created user |
| GET | `/api/users/{id}` | Fetch user by ID | Returns user details |
| GET | `/api/users` | List all active users | Returns user list |
| PUT | `/api/users/{id}` | Update user fields | Returns updated user |
| DELETE | `/api/users/{id}` | Soft delete user by setting `isActive=false` | Returns success / updated state |

### Project Management
| Method | Path | Request Summary | Response Summary |
|---|---|---|---|
| POST | `/api/projects` | Create project with owner, dates, and status | Returns created project |
| GET | `/api/projects/{id}` | Fetch project by ID | Returns project details |
| GET | `/api/projects` | List projects with optional filters | Returns project list |
| PUT | `/api/projects/{id}/status` | Update project status with validation | Returns updated project |
| POST | `/api/projects/{id}/members` | Add user to project membership | Returns membership/result |
| DELETE | `/api/projects/{id}/members/{userId}` | Remove user from project membership | Returns success/result |

### Task Management
| Method | Path | Request Summary | Response Summary |
|---|---|---|---|
| POST | `/api/tasks` | Create task under a project | Returns created task |
| GET | `/api/tasks/{id}` | Get task details including status history | Returns task details |
| GET | `/api/projects/{id}/tasks` | List tasks for a project with filters | Returns task list |
| PATCH | `/api/tasks/{id}/status` | Change task status using allowed transitions | Returns updated task |
| PATCH | `/api/tasks/{id}/assign` | Assign or reassign a task | Returns updated task |
| PUT | `/api/tasks/{id}` | Update non-status task fields | Returns updated task |

### Analytics Endpoints
| Method | Path | Request Summary | Response Summary |
|---|---|---|---|
| GET | `/api/analytics/projects/{id}/summary` | Task counts grouped by status and priority for a project | Returns summary view |
| GET | `/api/analytics/users/{id}/workload` | Counts tasks by status, total estimated hours, and overdue task count for a user | Returns workload summary |
| GET | `/api/analytics/projects/{id}/overdue` | Returns tasks where `dueDate < current date` and status is not `DONE` or `CANCELLED`, ordered by priority descending and due date ascending | Returns overdue task list |
| GET | `/api/analytics/projects/{id}/velocity` | Returns count of tasks moved to `DONE` in each of the last 4 weeks based on `TaskStatusHistory.changedAt` | Returns weekly velocity data |
| GET | `/api/analytics/leaderboard` | Returns top 5 users by number of tasks completed in the last 30 days using DB-side aggregation | Returns ranked leaderboard |

## Business Rules Summary
Business rules are enforced at the service/application layer, not only at the database layer.

### Status transitions
Allowed task transitions:
- TODO → IN_PROGRESS, CANCELLED
- IN_PROGRESS → IN_REVIEW, TODO, CANCELLED
- IN_REVIEW → DONE, IN_PROGRESS
- DONE → terminal state
- CANCELLED → terminal state

### Assignment rules
- A task can only be assigned to a user with role `DEVELOPER`.
- A task can only be assigned to a user who belongs to the same project through `project_members`.
- Only `MANAGER` or `ADMIN` can assign or reassign tasks.
- A `DONE` or `CANCELLED` task cannot be reassigned.

### Project rules
- A project cannot be archived unless all its tasks are either `DONE` or `CANCELLED`.
- New tasks cannot be created for projects in `ON_HOLD`, `COMPLETED`, or `ARCHIVED`.
- `endDate` must not be earlier than `startDate`.

### Concurrency
Optimistic locking is implemented using JPA `@Version` on the `Task` entity so concurrent updates do not silently overwrite each other.

## Error Handling
The API uses centralized exception handling and structured JSON error responses.

Expected response handling:
- HTTP 400 for validation errors
- HTTP 422 for business rule violations
- HTTP 404 for missing resources
- HTTP 409 for optimistic locking conflicts

Expected error response fields:
- `timestamp`
- `status`
- `errorCode`
- `message`
- `path`

## DSA Approach
The project includes a `TaskScheduler` utility/API to demonstrate the required DSA component.

### Why min-heap
A min-heap is used to efficiently retrieve the next most urgent task without repeatedly sorting the entire task list. In this implementation, ordering is determined by earliest `dueDate`, then higher `priorityScore`, then lower `estimatedHours`, which makes heap-based scheduling a good fit for repeated extraction of the next task.

### Time complexity
- Insert one task into heap: `O(log n)`
- Remove next task from heap: `O(log n)`
- Peek next task: `O(1)`
- Build heap from `n` tasks: `O(n)` in standard heap construction or `O(n log n)` with repeated inserts

### Edge cases handled
- Null due dates are handled using null-safe comparison and placed after valid dates
- Same due date tasks are ordered using `priorityScore`
- Same due date and same priority tasks are further ordered using `estimatedHours`
- Empty input returns an empty response list
- Null optional numeric/date values are handled defensively

## Assumptions & Decisions
- User deletion is implemented as soft delete using `isActive=false`.
- Project membership is required before a task can be assigned to a developer.
- Task reassignment is blocked for terminal task states (`DONE`, `CANCELLED`).
- Analytics queries are executed at repository/database level where practical to avoid loading unnecessary records into memory.
- `TaskScheduler` is exposed as a standalone utility API to satisfy the DSA requirement separately from core task CRUD operations.

## Known Limitations
- Authentication and authorization are simplified and are not production-grade.
- Some analytics outputs are intentionally compact and can be extended with richer reporting DTOs.
- `TaskScheduler` currently demonstrates priority-based scheduling logic but is not integrated with a background worker or job execution engine.
- Additional indexing and tuning may be needed for very large production datasets.
- Some optional features such as advanced filtering, export support, and stronger audit/reporting views can be improved further with more time.

## API Documentation (Swagger)
If configured in the project, Swagger UI is available at:

- `http://localhost:8080/swagger-ui/index.html`
