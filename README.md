# Task Manager API

A RESTful API for task management built with Spring Boot and PostgreSQL.

## Overview

Task Manager API is a backend application that allows users to create, retrieve, update, and delete tasks. The project was developed as part of my software engineering learning journey to practice REST API development, database integration, and backend architecture using Spring Boot.

## Features

* Create new tasks
* Retrieve all tasks
* Retrieve a task by ID
* Update existing tasks
* Delete tasks
* Interactive API documentation with Swagger/OpenAPI
* PostgreSQL database integration

## Technologies Used

* Java 21
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Maven
* Swagger / OpenAPI

## Project Structure

The project follows a layered architecture:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

### Components

* **Controller**: Handles HTTP requests and responses.
* **Service**: Contains business logic.
* **Repository**: Manages database operations.
* **Entity**: Represents database tables.

## API Endpoints

| Method | Endpoint    | Description             |
| ------ | ----------- | ----------------------- |
| GET    | /tasks      | Get all tasks           |
| GET    | /tasks/{id} | Get task by ID          |
| POST   | /tasks      | Create a new task       |
| PUT    | /tasks/{id} | Update an existing task |
| DELETE | /tasks/{id} | Delete a task           |

## Running the Project

### Prerequisites

* Java 21+
* Maven
* PostgreSQL

### Clone the Repository

```bash
git clone https://github.com/NicolasContrerasDev/task-manager-api.git
cd task-manager-api
```

### Configure Database

Update your application.properties file:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Run the Application

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

Swagger documentation:

```text
http://localhost:8080/swagger-ui.html
```

## Future Improvements

* JWT Authentication
* User Management
* Role-Based Authorization
* Pagination and Filtering
* Docker Support
* Unit and Integration Testing

## Author

Emilio Nicolás Contreras

Engineering Student focused on Backend Development with Java and Spring Boot.

GitHub:
https://github.com/NicolasContrerasDev

LinkedIn:
https://www.linkedin.com/in/emilio-nicol%C3%A1s-contreras-salazar-0aab0a390/


## API Documentation
<img width="1338" height="598" alt="image" src="https://github.com/user-attachments/assets/2e7eb04a-e467-42b0-8d9c-2cb0f7a4fb72" />
