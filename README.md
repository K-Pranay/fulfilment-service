# Warehouse Colocation & Fulfillment Service (Spring Boot 3)

This repository contains the complete implementation for the Warehouse Colocation & Fulfillment Microservice built with **Java 17, Spring Boot 3, Spring Web MVC, Spring Data JPA, Spring Transaction Management, PostgreSQL, Docker, AWS Free Tier deployment, and GitHub Actions CI/CD**.

## Deliverables & Architecture

- `fulfilment-service/`: Complete, self-contained, AWS-ready Spring Boot 3 codebase.
- `QUESTIONS.md`: Answers to technical questions on Database Access Patterns, OpenAPI vs Code-First API design, and Testing Strategies.
- `CASE_STUDY.md`: Detailed strategic answers for all 5 business case study scenarios (Cost Allocation, Cost Optimization, Financial System Integration, Budgeting/Forecasting, Warehouse Replacement).
- `.github/workflows/ci-cd.yml`: Automated GitHub Actions pipeline.
- `Dockerfile` & `docker-compose.yml`: Container configuration for production & local development.
- `aws/`: AWS App Runner and ECS Fargate deployment specs for AWS Free Tier.

## Building and Running

### Prerequisites
- JDK 17+
- Maven 3.8+ or `./mvnw`
- Docker (optional)

### Build and Test
```bash
./mvnw clean test
```

### Run Application Locally
```bash
./mvnw spring-boot:run
```
Navigate to Swagger UI: http://localhost:8080/swagger-ui.html

### Run via Docker Compose
```bash
docker-compose up --build
```

## Submission & Portfolio Link

Refer to [PORTFOLIO.md](PORTFOLIO.md) for full architecture diagrams, API documentation, and presentation formatting.