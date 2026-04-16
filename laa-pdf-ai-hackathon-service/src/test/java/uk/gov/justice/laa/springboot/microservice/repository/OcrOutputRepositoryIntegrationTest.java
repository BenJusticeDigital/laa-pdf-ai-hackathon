package uk.gov.justice.laa.springboot.microservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.springboot.microservice.PostgresIntegrationTestBase;
import uk.gov.justice.laa.springboot.microservice.entity.OcrOutputEntity;
import uk.gov.justice.laa.springboot.microservice.ocr.OcrProvider;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link OcrOutputRepository}.
 *
 * <p>Tests the persistence of OCR extracted data to the ocr_output table.</p>
 */
@SpringBootTest
@Transactional
class OcrOutputRepositoryIntegrationTest extends PostgresIntegrationTestBase {

  @Autowired
  private OcrOutputRepository ocrOutputRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldSaveAndRetrieveOcrOutputWithJsonData() throws Exception {
    // Arrange
    UUID id = UUID.randomUUID();
    Map<String, Object> extractedData = Map.of(
        "clientName", "John Doe",
        "dateOfBirth", "1990-05-15",
        "caseReference", "CR-2024-001",
        "costs", 1500.00
    );
    String jsonData = objectMapper.writeValueAsString(extractedData);

    OcrOutputEntity entity = OcrOutputEntity.builder()
        .id(id)
        .data(jsonData)
        .build();

    // Act
    ocrOutputRepository.save(entity);
    var retrievedEntity = ocrOutputRepository.findById(id);

    // Assert
    assertThat(retrievedEntity).isPresent();
    assertThat(retrievedEntity.get().getId()).isEqualTo(id);
    assertThat(retrievedEntity.get().getData()).isEqualTo(jsonData);

    // Verify we can deserialize the JSON back
    @SuppressWarnings("unchecked")
    Map<String, Object> deserializedData = objectMapper.readValue(
        retrievedEntity.get().getData(),
        Map.class
    );
    assertThat(deserializedData)
        .containsEntry("clientName", "John Doe")
        .containsEntry("caseReference", "CR-2024-001");
  }

  @Test
  void shouldHandleComplexNestedJsonData() throws Exception {
    // Arrange
    UUID id = UUID.randomUUID();
    Map<String, Object> nestedData = Map.of(
        "client", Map.of(
            "name", "Jane Smith",
            "contact", Map.of(
                "email", "jane@example.com",
                "phone", "020-1234-5678"
            )
        ),
        "status", "EXTRACTED"
    );
    String jsonData = objectMapper.writeValueAsString(nestedData);

    OcrOutputEntity entity = OcrOutputEntity.builder()
        .id(id)
        .data(jsonData)
        .build();

    // Act
    ocrOutputRepository.save(entity);
    var retrievedEntity = ocrOutputRepository.findById(id);

    // Assert
    assertThat(retrievedEntity).isPresent();
    @SuppressWarnings("unchecked")
    Map<String, Object> deserializedData = objectMapper.readValue(
        retrievedEntity.get().getData(),
        Map.class
    );

    assertThat(deserializedData).containsKey("client");
    @SuppressWarnings("unchecked")
    Map<String, Object> clientData = (Map<String, Object>) deserializedData.get("client");
    assertThat(clientData).containsEntry("name", "Jane Smith");

    @SuppressWarnings("unchecked")
    Map<String, Object> contactData = (Map<String, Object>) clientData.get("contact");
    assertThat(contactData).containsEntry("email", "jane@example.com");
  }

  @Test
  void shouldDeleteOcrOutput() {
    // Arrange
    UUID id = UUID.randomUUID();
    OcrOutputEntity entity = OcrOutputEntity.builder()
        .id(id)
        .data("{\"test\": \"data\"}")
        .build();

    ocrOutputRepository.save(entity);
    assertThat(ocrOutputRepository.findById(id)).isPresent();

    // Act
    ocrOutputRepository.deleteById(id);

    // Assert
    assertThat(ocrOutputRepository.findById(id)).isEmpty();
  }
}
