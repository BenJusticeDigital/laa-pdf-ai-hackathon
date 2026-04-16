package uk.gov.justice.laa.springboot.microservice.service;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.springboot.microservice.model.Cw1FormData;
import uk.gov.justice.laa.springboot.microservice.model.ImageResponse;
import uk.gov.justice.laa.springboot.microservice.ocr.OcrProvider;
import uk.gov.justice.laa.springboot.microservice.repository.OcrOutputRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link ImageService}.
 *
 * <p>Verifies that calling processImage results in the extracted OCR data
 * being persisted to the ocr_output table. The OcrProvider is mocked to
 * avoid real external API calls.</p>
 */
@SpringBootTest
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.flyway.enabled=true"
})
class ImageServiceIntegrationTest {

  @Autowired
  private ImageService imageService;

  @Autowired
  private OcrOutputRepository ocrOutputRepository;

  @MockitoBean
  private OcrProvider ocrProvider;

  @Test
  void shouldPersistExtractedDataToDatabase() {
    // Arrange — stub the OCR provider to return known extracted data
    Cw1FormData formData = new Cw1FormData();
    formData.setSurname("Doe");
    formData.setFirstName("John");
    formData.setNationalInsuranceNumber("AB123456C");
    when(ocrProvider.extractFormData(any())).thenReturn(formData);

    MockMultipartFile pdf = new MockMultipartFile(
        "file", "cw1-form.pdf", "application/pdf", "dummy pdf content".getBytes()
    );

    // Act — call processImage as a caseworker would after uploading a PDF
    ImageResponse response = imageService.processImage(pdf, "caseworker@laa.gov.uk");

    // Assert — response contains a valid ID and the extracted data
    assertThat(response.getId()).isNotNull();
    assertThat(response.getExtractedData()).isNotNull();

    // Assert — the OCR output was actually saved to the database
    UUID savedId = response.getId();
    var saved = ocrOutputRepository.findById(savedId);
    assertThat(saved).isPresent();
    assertThat(saved.get().getData())
        .contains("Doe")
        .contains("AB123456C");
  }

  @Test
  void shouldGenerateUniqueIdPerSubmission() {
    // Arrange
    Cw1FormData formData = new Cw1FormData();
    formData.setSurname("Smith");
    when(ocrProvider.extractFormData(any())).thenReturn(formData);

    MockMultipartFile pdf = new MockMultipartFile(
        "file", "cw1-form.pdf", "application/pdf", "dummy".getBytes()
    );

    // Act — submit the same file twice
    ImageResponse first = imageService.processImage(pdf, "caseworker@laa.gov.uk");
    ImageResponse second = imageService.processImage(pdf, "caseworker@laa.gov.uk");

    // Assert — each submission gets a unique ID and a separate DB row
    assertThat(first.getId()).isNotEqualTo(second.getId());
    assertThat(ocrOutputRepository.findById(first.getId())).isPresent();
    assertThat(ocrOutputRepository.findById(second.getId())).isPresent();
    assertThat(ocrOutputRepository.count()).isGreaterThanOrEqualTo(2);
  }
}
