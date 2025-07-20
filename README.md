# Spring Boot JWT Auth Application (Kotlin)

# JWT Authentication Application

A Spring Boot application providing JWT-based authentication and authorization with role-based access control using Kotlin and MongoDB.

## Features

- **JWT Authentication**: Secure token-based authentication system
- **Role-Based Authorization**: Support for USER, ADMIN, and MANAGER roles
- **MongoDB Integration**: User data persistence with MongoDB
- **Password Encryption**: BCrypt password hashing
- **RESTful API**: Clean REST endpoints for user management
- **Input Validation**: Comprehensive request validation
- **Logging**: Structured logging with SLF4J and Logback
- **Docker Support**: Containerized deployment ready

## Tech Stack

- **Language**: Kotlin
- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with JWT
- **Database**: MongoDB
- **Build Tool**: Gradle (Kotlin DSL)
- **JWT Library**: JJWT 0.11.5
- **Validation**: Jakarta Validation

## Getting Started

### Prerequisites

- Java 17 or higher
- MongoDB (local or Docker)
- Gradle (or use the included Gradle wrapper)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Jwt_auth_app
   ```

2. **Start MongoDB** (if running locally)
   ```bash
   mongod
   ```

3. **Configure application properties**
   
   Update `src/main/resources/application.yml` with your MongoDB connection details:
   ```yaml
   spring:
     data:
       mongodb:
         uri: mongodb://localhost:27017/jwt_auth_db
   ```

4. **Build and run the application**
   ```bash
   ./gradlew bootRun
   ```

   The application will start on `http://localhost:8080`

### Docker Deployment

1. **Using Docker Compose**
   ```bash
   docker-compose up -d
   ```

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/register` | User registration |

### User Management

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/users` | Get all users | ADMIN |
| GET | `/api/users/{id}` | Get user by ID | ADMIN/MANAGER |
| PUT | `/api/users/{id}` | Update user | ADMIN |
| DELETE | `/api/users/{id}` | Delete user | ADMIN |

## Request/Response Examples

### Login Request
```json
{
  "username": "admin",
  "password": "password"
}
```

### Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "username": "admin",
    "roles": ["ADMIN"]
  }
}
```

### Create User Request
```json
{
  "username": "newuser",
  "password": "securepassword",
  "roles": ["USER"]
}
```

## Default Users

The application creates default users on startup:

| Username | Password | Roles |
|----------|----------|-------|
| admin | password | ADMIN |
| manager | password | MANAGER |
| user | password | USER |

## Configuration

### JWT Configuration

Configure JWT settings in `application.yml`:

```yaml
jwt:
  secret: myDefaultSecretKeyForJwtTokenGeneration1234567890
  expiration: 86400000  # 24 hours in milliseconds
```

### Logging Configuration

Logging is configured in `src/main/resources/logback-spring.xml`:
- Development profile: Console and file logging
- Production profile: File logging only with log rotation

## Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/example/jwtauth/
│   │       ├── config/          # Configuration classes
│   │       ├── controller/      # REST controllers
│   │       ├── dto/            # Data Transfer Objects
│   │       ├── exception/      # Custom exceptions
│   │       ├── filter/         # Security filters
│   │       ├── model/          # Entity models
│   │       ├── repository/     # Data repositories
│   │       ├── service/        # Business logic
│   │       └── util/           # Utility classes
│   └── resources/
│       ├── application.yml     # Application configuration
│       └── logback-spring.xml  # Logging configuration
└── test/                       # Test files
```

## Security

- **Password Hashing**: All passwords are encrypted using BCrypt
- **JWT Security**: Tokens are signed with HS256 algorithm
- **Role-Based Access**: Endpoints are protected based on user roles
- **Input Validation**: All user inputs are validated
- **Security Headers**: CORS and other security headers configured

## Testing

Run tests using:
```bash
./gradlew test
```

## Building for Production

1. **Build the JAR file**
   ```bash
   ./gradlew bootJar
   ```

2. **Run the JAR**
   ```bash
   java -jar build/libs/Jwt_auth_app-0.0.1-SNAPSHOT.jar
   ```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or support, please contact [sedo-sebe@example.com]
