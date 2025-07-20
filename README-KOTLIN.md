# JWT Auth API - Kotlin + Spring Boot + MongoDB

## What does this application do?

- Provides a REST API for user authentication and authorization using JWT tokens.
- Manages users with roles (ADMIN, MANAGER, USER) stored in MongoDB.
- Secures endpoints with Spring Security and role-based access control.
- Offers endpoints for login, user management, and health check.
- Automatically creates default users at startup for testing/demo.

## Technologies used

- **Kotlin** (main language)
- **Spring Boot** 3.2.x (REST API, Security)
- **Spring Security** (JWT authentication)
- **Spring Data MongoDB** (database access)
- **MongoDB** 7.x (Docker container)
- **Gradle** (Kotlin DSL build)
- **Docker Compose** (for local MongoDB)

---

For quick start and usage, see the main README or README-DOCKER.md.
