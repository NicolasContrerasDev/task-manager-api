# Task Manager API

Task management REST API built with Spring Boot and PostgreSQL.

## Technologies

- Java 21
- Spring Boot
- PostgreSQL
- Gradle
- Swagger

## Features

- Register users
- Login with JWT
- Create workspaces or class areas
- Join workspaces by ID
- Manage workspace roles: `OWNER`, `ADMIN`, `USER`
- Create tasks inside workspaces as `OWNER` or `ADMIN`
- Export assigned workspace tasks to Excel
- Create tasks
- Update tasks
- Delete tasks
- Get task by ID
- List all tasks

## Auth

Public endpoints:

- `POST /api/auth/register`
- `POST /api/auth/login`

Both endpoints return a Bearer token. Send it in protected requests:

```http
Authorization: Bearer <token>
```

Passwords must have more than 8 characters and are stored with BCrypt.

## PostgreSQL and UUIDs

The connection is configured through environment variables, with local defaults:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tareas_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=admin123
```

Every public identifier is a UUID: users, workspaces, memberships and tasks. For example:

```text
7e787c51-b573-48a4-a79d-c7856cb43e32
```

If the database was created with the prior version and its `tasks.id` column is numeric, execute [the UUID migration](docs/postgresql-uuid-migration.sql) once before starting this version. Hibernate's `ddl-auto=update` creates the new tables and relations, but it does not safely convert an existing numeric primary key to UUID.

For production, configure `JWT_SECRET` with a private value of at least 32 characters.

## Workspaces

Protected endpoints:

- `POST /api/workspaces`
- `GET /api/workspaces`
- `POST /api/workspaces/{workspaceId}/join`
- `GET /api/workspaces/{workspaceId}/members`
- `PATCH /api/workspaces/{workspaceId}/members/{userId}/role`
- `POST /api/workspaces/{workspaceId}/tasks`
- `GET /api/workspaces/{workspaceId}/tasks`
- `GET /api/workspaces/{workspaceId}/tasks/export`

The creator of a workspace becomes `OWNER`. `OWNER` and `ADMIN` can create, update and delete workspace tasks. Any member can view the workspace itinerary and export an Excel file with the tasks assigned to their own user.

When creating or updating a workspace task, `assignedUserId` must be the UUID of a member of that workspace.

## Author

Emilio Nicolás Contreras Salazar 
https://www.linkedin.com/in/emilio-nicol%C3%A1s-contreras-salazar-0aab0a390/
