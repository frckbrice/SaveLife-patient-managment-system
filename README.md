# SaveLife Patient Management System

![System Architecture](assets/system%20architecture.png)

## Overview

SaveLife Patient Management System is a comprehensive microservices-based healthcare management platform designed to handle patient data, authentication, billing, and analytics. The system is built using Spring Boot 3.4 and follows modern microservices architecture patterns with RESTful APIs, gRPC for inter-service communication, and event-driven architecture using Apache Kafka.

## Architecture

The system follows a microservices architecture pattern with the following key components:

- **API Gateway**: Single entry point for all client requests with routing and JWT validation
- **Service Mesh**: Independent, loosely coupled services communicating via REST and gRPC
- **Event-Driven Communication**: Asynchronous event processing using Apache Kafka
- **Infrastructure as Code**: AWS CDK-based infrastructure deployment using LocalStack

## Technology Stack

### Core Technologies
- **Java 21**: Modern Java features and performance improvements
- **Spring Boot 3.4**: Enterprise-grade application framework
- **Spring Cloud Gateway**: API gateway and routing
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database abstraction and ORM

### Communication Protocols
- **REST API**: HTTP-based synchronous communication
- **gRPC**: High-performance RPC framework for inter-service communication
- **Protocol Buffers**: Language-neutral data serialization
- **Apache Kafka**: Distributed event streaming platform

### Databases
- **PostgreSQL**: Primary relational database for persistent data storage
- **H2 Database**: In-memory database for testing

### Infrastructure & DevOps
- **Docker**: Containerization for all services
- **AWS CDK**: Infrastructure as Code using Java
- **LocalStack**: Local AWS cloud stack emulation
- **Maven**: Build automation and dependency management

### Documentation & Testing
- **SpringDoc OpenAPI**: API documentation and Swagger UI
- **JUnit**: Unit and integration testing framework

## Microservices

### 1. API Gateway Service
**Port**: 4004

The API Gateway serves as the single entry point for all client requests. It handles:
- Request routing to appropriate microservices
- JWT token validation for protected endpoints
- API documentation aggregation
- Load balancing and service discovery

**Key Features**:
- Dynamic route configuration
- JWT validation filter
- Path rewriting for API documentation endpoints
- Production and development environment configurations

### 2. Authentication Service
**Port**: 4005

Handles user authentication and authorization for the entire system.

**Key Features**:
- User registration and login
- JWT token generation and validation
- Password encryption using Spring Security
- User session management
- PostgreSQL database integration

**Endpoints**:
- POST `/auth/register` - User registration
- POST `/auth/login` - User authentication
- POST `/auth/validate` - Token validation

### 3. Patient Service
**Port**: 4000

Core service managing patient information and medical records.

**Key Features**:
- CRUD operations for patient records
- Email uniqueness validation
- Integration with Billing Service via gRPC
- Event publishing to Kafka for analytics
- PostgreSQL database for data persistence
- OpenAPI documentation

**Endpoints**:
- GET `/api/patients` - Retrieve all patients
- POST `/api/patients` - Create new patient
- PUT `/api/patients/{id}` - Update patient information
- DELETE `/api/patients/{id}` - Delete patient record

### 4. Billing Service
**Ports**: 4001 (HTTP), 9001 (gRPC)

Manages billing accounts and financial transactions for patients.

**Key Features**:
- gRPC-based service interface
- Billing account creation and management
- Protocol Buffer message definitions
- High-performance inter-service communication

**gRPC Methods**:
- `createBillingAccount` - Create billing account for new patients

### 5. Analytics Service
**Port**: 4002

Processes and analyzes patient events for business intelligence and reporting.

**Key Features**:
- Kafka consumer for patient events
- Real-time event processing
- Protocol Buffer event deserialization
- Event logging and analytics

**Event Processing**:
- Consumes from `patient` Kafka topic
- Processes patient creation, update, and deletion events
- Supports extensible analytics pipeline

### 6. Infrastructure Module

AWS CDK-based infrastructure definition for cloud deployment.

**Components**:
- VPC configuration
- ECS Fargate services for all microservices
- RDS PostgreSQL databases for Auth and Patient services
- MSK (Managed Streaming for Apache Kafka) cluster
- Health checks and service dependencies
- Environment variable management

## Project Structure

```
java-spring-microservices/
├── api-gateway/              # API Gateway service
├── auth-service/             # Authentication service
├── patient-service/          # Patient management service
├── billing-service/          # Billing service (gRPC)
├── analytics-service/        # Analytics and event processing
├── infrastructure/           # AWS CDK infrastructure code
├── integration-tests/        # End-to-end integration tests
├── api-requests/            # HTTP request examples
├── grpc-requests/           # gRPC request examples
└── assets/                  # Documentation assets
```

## Prerequisites

- Java 21 or higher
- Maven 3.8 or higher
- Docker and Docker Compose
- PostgreSQL 12 or higher (for local development)
- Apache Kafka (or use Docker Compose setup)
- LocalStack (for infrastructure testing)

## Getting Started

### 1. Clone the Repository

```bash
git clone git@github.com:frckbrice/SaveLife-patient-managment-system.git
cd SaveLife-patient-managment-system
```

### 2. Start Infrastructure Services

Start PostgreSQL and Kafka using Docker Compose:

```bash
docker-compose up -d
```

### 3. Build the Project

Build all microservices:

```bash
mvn clean install
```

### 4. Run Services

Start each service in the following order:

**Terminal 1 - Authentication Service**:
```bash
cd auth-service
mvn spring-boot:run
```

**Terminal 2 - Patient Service**:
```bash
cd patient-service
mvn spring-boot:run
```

**Terminal 3 - Billing Service**:
```bash
cd billing-service
mvn spring-boot:run
```

**Terminal 4 - Analytics Service**:
```bash
cd analytics-service
mvn spring-boot:run
```

**Terminal 5 - API Gateway**:
```bash
cd api-gateway
mvn spring-boot:run
```

### 5. Access Services

- **API Gateway**: http://localhost:4004
- **Patient Service API Docs**: http://localhost:4004/api-docs/patients
- **Auth Service API Docs**: http://localhost:4004/api-docs/auth
- **Patient Service Direct**: http://localhost:4000
- **Auth Service Direct**: http://localhost:4005

## API Documentation

### Authentication Flow

1. Register a new user:
```http
POST http://localhost:4004/auth/register
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

2. Login to get JWT token:
```http
POST http://localhost:4004/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

3. Use the token for authenticated requests:
```http
GET http://localhost:4004/api/patients
Authorization: Bearer <your-jwt-token>
```

### Patient Management

Create a new patient:
```http
POST http://localhost:4004/api/patients
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "dateOfBirth": "1990-01-15",
  "phoneNumber": "+1234567890"
}
```

## Docker Deployment

Each service includes a Dockerfile for containerization:

```bash
# Build Docker image
docker build -t patient-service ./patient-service

# Run container
docker run -p 4000:4000 patient-service
```

## Infrastructure Deployment

Deploy infrastructure using AWS CDK and LocalStack:

```bash
cd infrastructure
./localstack-deploy.sh
```

## Testing

Run integration tests:

```bash
cd integration-tests
mvn test
```

Run tests for a specific service:

```bash
cd <service-name>
mvn test
```

## Branch Structure

The project is organized into feature branches for each microservice:

- `analytics-service` - Analytics service implementation
- `api-gateway` - API Gateway service
- `auth-service` - Authentication service
- `billing-service` - Billing service with gRPC
- `patient-service` - Patient management service
- `infrastructure` - Infrastructure as Code
- `integration-tests` - Integration test suite
- `main` - Complete project with all services

## Development Guidelines

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add Javadoc comments for public APIs
- Maintain consistent formatting

### Commit Messages
- Use clear, descriptive commit messages
- Reference issue numbers when applicable
- Follow conventional commit format

### Testing
- Write unit tests for business logic
- Include integration tests for API endpoints
- Maintain minimum 70% code coverage

## Security Considerations

- JWT tokens for stateless authentication
- Password encryption using Spring Security BCrypt
- Input validation on all endpoints
- SQL injection prevention via JPA
- CORS configuration for API Gateway
- Environment variable management for secrets

## Performance Optimizations

- Connection pooling for database connections
- gRPC for high-performance inter-service communication
- Asynchronous event processing with Kafka
- Docker containerization for scalability
- Stateless service design for horizontal scaling

## Future Enhancements

- Service mesh implementation (Istio/Linkerd)
- Distributed tracing (Jaeger/Zipkin)
- Centralized logging (ELK Stack)
- Monitoring and alerting (Prometheus/Grafana)
- CI/CD pipeline automation
- Kubernetes deployment manifests
- Multi-region deployment support

## Contributing

1. Create a feature branch from `main`
2. Make your changes with appropriate tests
3. Ensure all tests pass
4. Submit a pull request with a clear description

## License

This project is proprietary and confidential.

## Contact

For questions or support, please contact the development team.

