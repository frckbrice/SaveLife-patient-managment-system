# Testing Guide

This document provides comprehensive information about testing in the SaveLife Patient Management System.

## Testing Strategy

The project follows a multi-layered testing approach:

1. **Unit Tests**: Test individual components in isolation
2. **Integration Tests**: Test service interactions and API endpoints
3. **End-to-End Tests**: Test complete user workflows

## Test Structure

```
src/
├── main/
│   └── java/
└── test/
    └── java/
        └── com/pm/
            └── [service]/
                ├── controller/     # Controller tests
                ├── service/        # Service layer tests
                ├── repository/     # Repository tests (if needed)
                └── [Application]Tests.java  # Application context tests
```

## Running Tests

### Run All Tests

```bash
# From project root
mvn test

# For a specific service
cd patient-service
mvn test
```

### Run Tests with Coverage

```bash
mvn test jacoco:report
```

Coverage reports are generated in `target/site/jacoco/index.html`

### Run Integration Tests

```bash
cd integration-tests
mvn test
```

## Test Coverage Requirements

- Minimum coverage: **70%** for all packages
- Critical paths: **90%+** coverage
- JaCoCo enforces coverage thresholds during build

## Unit Testing

### Service Layer Tests

Service layer tests use Mockito to mock dependencies:

```java
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;
    
    @InjectMocks
    private PatientService patientService;
    
    @Test
    void testCreatePatient() {
        // Test implementation
    }
}
```

### Controller Tests

Controller tests use Spring's `@WebMvcTest` for lightweight MVC testing:

```java
@WebMvcTest(PatientController.class)
class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PatientService patientService;
    
    @Test
    void testGetPatients() throws Exception {
        mockMvc.perform(get("/patients"))
            .andExpect(status().isOk());
    }
}
```

## Integration Testing

### API Integration Tests

Integration tests use RestAssured for HTTP testing:

```java
public class PatientIntegrationTest {
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }
    
    @Test
    public void shouldCreatePatient() {
        given()
            .contentType("application/json")
            .body(patientPayload)
            .when()
            .post("/api/patients")
            .then()
            .statusCode(200);
    }
}
```

## Test Utilities

### Test Data Builders

Create test data builders for consistent test data:

```java
public class PatientTestDataBuilder {
    public static PatientRequestDTO validPatientRequest() {
        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        // ... set other fields
        return dto;
    }
}
```

## Mocking External Dependencies

### Database

Use H2 in-memory database for unit tests:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create-drop
```

### Kafka

Use `@EmbeddedKafka` for Kafka testing:

```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"patient"})
class KafkaIntegrationTest {
    // Test implementation
}
```

### gRPC

Mock gRPC clients in unit tests:

```java
@Mock
private BillingServiceGrpcClient billingServiceGrpcClient;
```

## Best Practices

1. **Test Naming**: Use descriptive test method names
   ```java
   @Test
   @DisplayName("Should create patient when valid data is provided")
   void testCreatePatientWithValidData() { }
   ```

2. **Arrange-Act-Assert**: Follow AAA pattern
   ```java
   @Test
   void testMethod() {
       // Arrange
       PatientRequestDTO request = createTestRequest();
       
       // Act
       PatientResponseDTO response = service.createPatient(request);
       
       // Assert
       assertNotNull(response);
   }
   ```

3. **Test Isolation**: Each test should be independent
   - Use `@BeforeEach` for setup
   - Clean up after tests
   - Don't rely on test execution order

4. **Mock External Services**: Always mock external dependencies
   - Database calls
   - HTTP clients
   - Message queues
   - External APIs

5. **Test Edge Cases**: Include tests for:
   - Invalid input
   - Null values
   - Empty collections
   - Boundary conditions
   - Error scenarios

## Continuous Integration

Tests run automatically on:
- Every push to main/develop branches
- Every pull request
- Coverage reports are generated and uploaded

## Test Coverage Reports

View coverage reports:
1. Run `mvn test jacoco:report`
2. Open `target/site/jacoco/index.html` in browser
3. Review coverage by package, class, and method

## Troubleshooting

### Tests Failing Locally

1. Check database connectivity
2. Verify Kafka is running (for integration tests)
3. Check environment variables
4. Review test logs for errors

### Coverage Not Meeting Threshold

1. Review coverage report
2. Identify uncovered code paths
3. Add tests for missing coverage
4. Consider if code is testable (refactor if needed)

## Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [RestAssured Documentation](https://rest-assured.io/)

