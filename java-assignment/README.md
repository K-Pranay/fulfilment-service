# Java Code Assignment

This is a code assignment exploring various aspects of software development, including API implementation, documentation, persistence layer handling, and testing.

## About the assignment

You will find the tasks of this assignment on [CODE_ASSIGNMENT](CODE_ASSIGNMENT.md) file.
You will find answers to the technical questions on [QUESTIONS.md](QUESTIONS.md).

### Requirements

To compile and run this demo you will need:
- JDK 17+
- Maven 3.8+ (or use the included `./mvnw` wrapper)
- In addition, you will need Docker to run PostgreSQL (optional for local in-memory H2 dev mode).

### Building and Testing

Execute the Maven build and test suite inside `java-assignment/`:

```sh
./mvnw clean test
```

### Running the Application Locally

```sh
./mvnw spring-boot:run
```

Once running:
- **REST API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Interactive API Lifecycle Dashboard**: `http://localhost:8080/api-demo.html`

### Running with Docker Compose (PostgreSQL)

```sh
docker-compose up --build
```
