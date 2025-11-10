# User Service

User authentication and profile management microservice for the Event Ticketing System.

## Overview

The User Service handles user registration, authentication, JWT token generation, and user profile management. It serves as the authentication gateway for all other microservices.

## Tech Stack

### Core Framework

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Build Tool**: Maven 3.9

### Database

- **MySQL Connector** - Database connectivity
- **Hibernate** - ORM with DDL auto-update

### Security & Documentation

- **JWT (jjwt)**: 0.12.3 - Token generation and validation
- **BCrypt** - Password hashing
- **SpringDoc OpenAPI**: 2.2.0 - API documentation (Swagger UI)

## Build & Run

### Prerequisites

- Java 17 or higher
- Maven 3.9+
- MySQL database (local or remote)

### 1. Build with Maven

```bash
# Clean and build
mvn clean package

# Skip tests (if needed)
mvn clean package -DskipTests
```

The JAR file will be created at: `target/user-service-1.0.0.jar`

### 2. Run Locally

```bash
# Run with Java
java -jar target/user-service-1.0.0.jar

# Or use Maven
mvn spring-boot:run
```

### 3. Build & Run with Docker

```bash
# Build Docker image
docker build -t user-service:1.0.0 .

# Run container
docker run -d \
  -p 8081:8081 \
  -e DB_URL="jdbc:mysql://your-db-host:3306/user_service_db" \
  -e DB_USERNAME="your-username" \
  -e DB_PASSWORD="your-password" \
  -e JWT_SECRET="your-secret-key-at-least-32-characters-long" \
  --name user-service \
  user-service:1.0.0
```

### 4. Run with Docker Compose

```bash
# From project root
docker-compose up user-service
```

## Configuration

### Environment Variables

| Variable           | Description                   | Default               |
| ------------------ | ----------------------------- | --------------------- |
| `DB_URL`         | MySQL database connection URL | See application.yml   |
| `DB_USERNAME`    | Database username             | `avnadmin`          |
| `DB_PASSWORD`    | Database password             | -                     |
| `JWT_SECRET`     | Secret key for JWT generation | Required              |
| `JWT_EXPIRATION` | Token expiration (ms)         | `86400000` (24 hrs) |
| `SERVER_PORT`    | Application port              | `8081`              |

### application.yml

```yaml
spring:
  application:
    name: user-service
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
server:
  port: 8081
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000
```

## Exposed APIs

### Base URL

- **Local**: `http://localhost:8081`
- **Container**: `http://user-service:8081`

---

### 1. **Register User**

```http
POST /api/v1/auth/register
```

**Headers:**

- `Content-Type: application/json`

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "9876543210"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

**cURL Example:**

```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "9876543210"
  }'
```

---

### 2. **Login**

```http
POST /api/v1/auth/login
```

**Headers:**

- `Content-Type: application/json`

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

**cURL Example:**

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

---

### 3. **Get User Profile**

```http
GET /api/v1/users/{id}
```

**Headers:**

- `Authorization: Bearer <JWT_TOKEN>`

**Response (200 OK):**

```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "9876543210",
  "role": "USER",
  "active": true,
  "createdAt": "2025-11-09T10:30:00"
}
```

**cURL Example:**

```bash
curl -X GET http://localhost:8081/api/v1/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 4. **Health Check** (No Auth Required)

```http
GET /actuator/health
```

**Response (200 OK):**

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL"
      }
    }
  }
}
```

**cURL Example:**

```bash
curl http://localhost:8081/actuator/health
```

---

## User Roles

| Role      | Description                    |
| --------- | ------------------------------ |
| `USER`  | Standard user (default)        |
| `ADMIN` | Administrator with full access |

## Security Features

- **Password Hashing**: BCrypt with salt
- **JWT Tokens**: HS512 algorithm with 24-hour expiration
- **Email Validation**: RFC 5322 compliant
- **Password Requirements**: Minimum 6 characters

## API Documentation

### Swagger UI

Access interactive API documentation at:

```
http://localhost:8081/swagger-ui.html
```

### OpenAPI Specification

Get the OpenAPI JSON specification at:

```
http://localhost:8081/v3/api-docs
```

## JWT Token Details

### Token Structure

```
Header: {
  "alg": "HS512",
  "typ": "JWT"
}

Payload: {
  "sub": "user@example.com",
  "userId": 1,
  "email": "user@example.com",
  "role": "USER",
  "iat": 1699520400,
  "exp": 1699606800
}
```

### Token Usage

Include the token in the Authorization header for all protected endpoints:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## Database Schema

### Users Table

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  phone VARCHAR(20),
  role ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
  active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_email (email),
  INDEX idx_role (role)
);
```

## Monitoring

### Health Checks

- **Endpoint**: `/actuator/health`
- **Interval**: 30 seconds (in Docker)
- **Checks**: Database connectivity, disk space, ping

### Logs

Application logs include:

- User registration events
- Login attempts (success/failure)
- JWT token generation
- Profile updates

## Quick Start Example

```bash
# 1. Build the service
mvn clean package

# 2. Run the service
java -jar target/user-service-1.0.0.jar

# 3. Register a new user
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@example.com",
    "password": "Demo123",
    "firstName": "Demo",
    "lastName": "User",
    "phone": "1234567890"
  }' | jq '.'

# 4. Save the token
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"Demo123"}' | jq -r '.token')

# 5. Get user profile
curl -X GET http://localhost:8081/api/v1/users/1 \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

## Related Services

- **Catalog Service** (8082): Uses user authentication
- **Seating Service** (8083): Uses user authentication
- **Order Service** (8084): Uses user authentication
- **Payment Service** (8085): Uses user authentication
