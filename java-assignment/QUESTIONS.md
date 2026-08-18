# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes, I would standardize the database access layer across the application on the Repository Pattern (Hexagonal / Ports and Adapters architecture).

Currently, the codebase uses two mixed patterns:
- Active Record Pattern (PanacheEntity / Panache model methods directly on entities like `Store`)
- Repository Pattern (`ProductRepository`, `WarehouseRepository` implementing `WarehouseStore`)

Why Refactor to Repository Pattern:
1. Separation of Concerns: Active Record tightly couples the domain model with infrastructure and database concerns (JPA/Hibernate), making pure unit testing difficult without starting an ORM container or database.
2. Hexagonal Architecture Alignment: The Warehouse package already uses clean Hexagonal Architecture (Domain models, Ports, Use Cases, Adapters). Standardizing `Store` and `Product` on the Repository Pattern allows domain logic and use cases to depend on interfaces rather than direct JPA static methods, enabling clean unit testing with simple mocks.
3. Testability and Maintainability: Mocking `Store.findById()` or `store.persist()` requires bytecode manipulation or Quarkus test runners, whereas interface-backed repositories can be easily mocked using standard JUnit/Mockito.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
Pros & Cons of Contract-First (OpenAPI Spec Generated) vs Code-First (Annotated JAX-RS):

1. Contract-First (OpenAPI Spec driven, e.g., Warehouse):
   - Pros: Single source of truth for frontend, backend, and external API consumers; enables parallel frontend/backend development with API mocking; ensures consistent schema versioning and API design governance.
   - Cons: Requires setup of code generation plugins; build step complexity; potential rigidity when generated interfaces require custom DTO mapping.

2. Code-First (JAX-RS annotations directly on Java classes, e.g., Store & Product):
   - Pros: Fast developer iteration for internal APIs; full control over Java types, annotations, and framework-specific features without code generation friction.
   - Cons: Risk of API drift where implementation deviates from public documentation; harder to share contracts across teams before implementation.

My Choice:
For core domain services and APIs exposed to multi-team, legacy, or external consumers (like Warehouse and Fulfillment), I advocate for Contract-First (OpenAPI). For purely internal CRUD or rapidly evolving prototype services, Code-First is acceptable initially, provided SmallRye OpenAPI annotations auto-generate accurate specs for Swagger UI.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
Testing Strategy & Prioritization:

1. Testing Pyramid Focus:
   - Unit Tests (70%): Focus heavily on domain use cases and validation business logic (`CreateWarehouseUseCaseTest`, `ReplaceWarehouseUseCaseTest`, `FulfillmentAssociationServiceTest`). These execute in milliseconds without launching Quarkus or a database.
   - Integration / Slice Tests (20%): Test REST endpoints and repository data access against an in-memory/DevServices DB to verify HTTP status codes, JSON serialization, and transaction commit behavior (`StoreResource` post-commit sync).
   - End-to-End Tests (10%): Test complete user flows via `QuarkusIntegrationTest` / REST-Assured against built artifacts.

2. Maintenance of Effective Coverage Over Time:
   - CI/CD Enforcement: Enforce minimum test coverage thresholds (e.g., 85% on domain use cases) in GitHub Actions pipeline using JaCoCo.
   - Mutation Testing (e.g., Pitest): Periodically run mutation testing to verify test quality, ensuring tests actually assert business rules rather than just achieving high line coverage.
```