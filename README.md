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

Both endpoints return only a confirmation message in the JSON body. The Bearer token is returned in the `Authorization` response header, so use `-i` with cURL to see it:

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
```

Example response:

```http
Authorization: Bearer <token>

{"message":"Inicio de sesion exitoso"}
```

Send the token in protected requests:

```http
Authorization: Bearer <token>
```

For the included web interface, serve the `Interfaz` folder at `http://localhost:5500` (for example, with a local static server). This is the default allowed browser origin. For another deployed client, configure `APP_CORS_ALLOWED_ORIGINS` with a comma-separated allowlist; do not use `*` when returning an authorization header.

Passwords must have more than 8 characters and are stored with BCrypt.

## PostgreSQL and UUIDs

The connection is configured through environment variables, with local defaults:



Every public identifier is a UUID: users, workspaces, memberships and tasks. For example:


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
