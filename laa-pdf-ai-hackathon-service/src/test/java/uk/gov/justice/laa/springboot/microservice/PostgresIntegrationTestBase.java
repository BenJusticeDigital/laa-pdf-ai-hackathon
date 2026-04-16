package uk.gov.justice.laa.springboot.microservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests requiring a PostgreSQL database.
 *
 * <p>Starts a shared PostgreSQLContainer once per test suite using Testcontainers,
 * and registers the datasource properties dynamically so Spring Boot connects to it.</p>
 */
@Testcontainers
public abstract class PostgresIntegrationTestBase {

  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("laa_pdf_ai_test")
      .withUsername("postgres")
      .withPassword("postgres");

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("spring.flyway.enabled", () -> "true");
  }
}

