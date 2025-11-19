# Store Management System

A Spring Boot REST API for store management with JWT authentication, built with Kotlin, MongoDB and Minio. Features include product catalog management, supplier management, inventory tracking, user authentication, and order processing.

## Features

- **JWT Authentication**: Secure token-based authentication with role-based access control
- **User Management**: Support for OWNER, EMPLOYEE, and CLIENT roles
- **Category Management**: Hierarchical product categorization system
- **Supplier Management**: Supplier details and contact management
- **Product Management**: Complete product catalog with SKU, pricing, and stock tracking
- **Order Processing**: Customer order management with status tracking
- **Loyalty Program**: Points accumulation and redemption system
- **Audit Trail**: Complete activity logging for compliance and tracking
- **File Storage**: MinIO integration for product images
- **Inventory Control**: Real-time stock monitoring with low-stock alerts
- **Billing**: Automated billing and invoice generation
- **Email Notifications**: Email alerts for order confirmations
- **Statistics & Reporting**: Sales and inventory reports for business insights

## Tech Stack

- **Language**: Kotlin 1.9.20
- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with JWT
- **Database**: MongoDB 7.0
- **Storage**: MinIO
- **Build Tool**: Gradle (Kotlin DSL)
- **Authentication**: JJWT 0.11.5

## Quick Start

### Option 1: Using Docker Hub Image (the used images are private)

```bash
# Clone the repository
git clone <repository-url>
cd sedo-store-app
# hosts file entry to access minio locally
add 127.0.0.1 minio in /etc/hosts file
# Run with pre-built Docker image from DockerHub (image are private)
docker-compose -f docker-compose.demo.yml up -d
```

**Services will be available at:**
- Backend API: http://localhost:8080
- Frontend UI: http://localhost:4000 with private image from docker hub
- Frontend UI: http://localhost:4200 local running of angular app
- MinIO Console: http://localhost:9001

### Option 2: Build from Source

```bash
# Clone the repository
git clone <repository-url>
cd Jwt_auth_app
# hosts file entry to access minio locally
add 127.0.0.1 minio in /etc/hosts file
# Build and run all services
docker-compose up -d
```

### Option 3: Local Development

```bash
# Start only MongoDB and MinIO
docker-compose -f docker-compose.yml up mongodb minio -d

# Run the application locally
./gradlew bootRun
```

Application will start on http://localhost:8080

## API Endpoints

### Authentication

| Method | Endpoint | Description                              |
|--------|----------|------------------------------------------|
| POST | `/api/auth/login` | User authentication                      |
| POST | `/api/auth/logout` | User logout                              |
| POST | `/api/auth/refresh_token` | Refresh JWT token                        |
| POST | `/api/auth/set-password` | Set/change password                      |
| GET | `/api/auth/validate-token` | Validate JWT token when updatin password |

### User Management

| Method | Endpoint | Description                  | Required Role |
|--------|----------|------------------------------|---------------|
| GET | `/api/users` | List all users with pageable | OWNER |
| GET | `/api/users/{id}` | Get user by ID               | OWNER/EMPLOYEE |
| POST | `/api/users` | Create user                  | OWNER |
| PUT | `/api/users/{id}` | Update user                  | OWNER |
| DELETE | `/api/users/{id}` | Delete user                  | OWNER |

### Category Management

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/products/categories` | List all categories | All |
| GET | `/api/products/categories/{id}` | Get category by ID | All |
| POST | `/api/products/categories` | Create category | OWNER/EMPLOYEE |
| PUT | `/api/products/categories/{id}` | Update category | OWNER/EMPLOYEE |
| DELETE | `/api/products/categories/{id}` | Delete category | OWNER |

### Product Management

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/products` | List all products | All |
| GET | `/api/products/{id}` | Get product by ID | All |
| POST | `/api/products` | Create product | OWNER/EMPLOYEE |
| PUT | `/api/products/{id}` | Update product | OWNER/EMPLOYEE |
| DELETE | `/api/products/{id}` | Delete product | OWNER |

## Default Users

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| owner | password | OWNER | Complete system control |
| employee | password | EMPLOYEE | Daily operations management |
| client | password | CLIENT | Customer operations |

## Configuration

### Environment Variables

```bash
# MongoDB
SPRING_DATA_MONGODB_HOST=localhost
SPRING_DATA_MONGODB_PORT=27017
SPRING_DATA_MONGODB_DATABASE=jwtauthdb
SPRING_DATA_MONGODB_USERNAME=admin
SPRING_DATA_MONGODB_PASSWORD=password123

# MinIO Storage
APP_MINIO_URL=http://localhost:9000
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin123

# JWT Configuration
JWT_SECRET=myDefaultSecretKeyForJwtTokenGeneration1234567890
JWT_ACCESS_TOKEN_EXPIRATION=900000 # 15 minute
JWT_REFRESH_TOKEN_EXPIRATION=86400000 # 24 hours
```

## API Examples

### Login Request
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "owner",
    "password": "password"
  }'
```

### Login Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user123",
    "username": "owner",
    "roles": ["OWNER"]
  }
}
```

### Create Category
```bash
curl -X POST http://localhost:8080/api/products/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Electronics",
    "description": "Electronic products",
    "isActive": true
  }'
```

### Building for Production

```bash
# Build JAR
./gradlew bootJar

# Run JAR
java -jar build/libs/Jwt_auth_app-0.0.1-SNAPSHOT.jar
```

## Development

### Running Tests
```bash
./gradlew test
```

### Building Docker Image
```bash
docker build -t store-management-api .
```

### Project Structure
```
src/
├── main/
│   ├── kotlin/com/sedo/jwtauth/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST endpoints
│   │   ├── model/           # DTOs and entities
│   │   ├── service/         # Business logic
│   │   ├── repository/      # Data access
│   │   ├── util/            # Utilities
│   │   └── filter/          # Security filters
│   └── resources/
│       ├── application.yml  # Main configuration
│       └── application-dev.yml # Development config
└── test/                    # Test files
```
