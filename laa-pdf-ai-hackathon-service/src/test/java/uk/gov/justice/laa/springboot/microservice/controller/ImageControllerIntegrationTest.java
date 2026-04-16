package uk.gov.justice.laa.springboot.microservice.controller;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.justice.laa.springboot.microservice.PostgresIntegrationTestBase;
import uk.gov.justice.laa.springboot.microservice.model.Cw1FormData;
import uk.gov.justice.laa.springboot.microservice.ocr.OcrProvider;
import uk.gov.justice.laa.springboot.microservice.ocr.OcrResult;
import uk.gov.justice.laa.springboot.microservice.repository.OcrOutputRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link ImageController}.
 *
 * <p>Tests the full HTTP request/response cycle through the controller,
 * service, and repository layers using MockMvc and a real H2 database.
 * The OcrProvider is mocked to avoid external API calls.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class ImageControllerIntegrationTest extends PostgresIntegrationTestBase {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private OcrOutputRepository ocrOutputRepository;

  @MockitoBean
  private OcrProvider ocrProvider;

  @Test
  void shouldReturn201AndLocationHeaderOnSuccessfulUpload() throws Exception {
    Cw1FormData formData = new Cw1FormData();
    formData.setSurname("Doe");
    formData.setFirstName("John");
    when(ocrProvider.extractFormData(any())).thenReturn(new OcrResult(true, formData, null));

    MockMultipartFile pdf = new MockMultipartFile(
        "image", "cw1-form.pdf", "application/pdf", "dummy pdf content".getBytes()
    );

    mockMvc.perform(multipart("/api/v1/image")
            .file(pdf)
            .param("email", "caseworker@laa.gov.uk"))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.extractedData.surname").value("Doe"))
        .andExpect(jsonPath("$.extractedData.firstName").value("John"));
  }

  @Test
  void shouldPersistOcrOutputToDatabaseOnUpload() throws Exception {
    Cw1FormData formData = new Cw1FormData();
    formData.setSurname("Smith");
    formData.setNationalInsuranceNumber("AB123456C");
    when(ocrProvider.extractFormData(any())).thenReturn(new OcrResult(true, formData, null));

    MockMultipartFile pdf = new MockMultipartFile(
        "image", "cw1-form.pdf", "application/pdf", "dummy pdf content".getBytes()
    );

    MvcResult result = mockMvc.perform(multipart("/api/v1/image")
            .file(pdf)
            .param("email", "caseworker@laa.gov.uk"))
        .andExpect(status().isCreated())
        .andReturn();

    // Extract the ID from the Location header and verify the DB row exists
    String location = result.getResponse().getHeader("Location");
    assertThat(location).isNotNull();
    UUID id = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));

    var saved = ocrOutputRepository.findById(id);
    assertThat(saved).isPresent();
    assertThat(saved.get().getData())
        .contains("Smith")
        .contains("AB123456C");
  }

  @Test
  void shouldReturn400WhenEmailIsMissing() throws Exception {
    MockMultipartFile pdf = new MockMultipartFile(
        "image", "cw1-form.pdf", "application/pdf", "dummy pdf content".getBytes()
    );

    mockMvc.perform(multipart("/api/v1/image")
            .file(pdf))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn500WhenEmailIsInvalid() throws Exception {
    MockMultipartFile pdf = new MockMultipartFile(
        "image", "cw1-form.pdf", "application/pdf", "dummy pdf content".getBytes()
    );

    mockMvc.perform(multipart("/api/v1/image")
            .file(pdf)
            .param("email", "not-a-valid-email"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void shouldReturn400WhenFileIsMissing() throws Exception {
    mockMvc.perform(multipart("/api/v1/image")
            .param("email", "caseworker@laa.gov.uk"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldIncludeLocationHeaderPointingToNewResource() throws Exception {
    Cw1FormData formData = new Cw1FormData();
    formData.setSurname("Jones");
    when(ocrProvider.extractFormData(any())).thenReturn(new OcrResult(true, formData, null));

    MockMultipartFile pdf = new MockMultipartFile(
        "image", "cw1-form.pdf", "application/pdf", "dummy".getBytes()
    );

    MvcResult result = mockMvc.perform(multipart("/api/v1/image")
            .file(pdf)
            .param("email", "caseworker@laa.gov.uk"))
        .andExpect(status().isCreated())
        .andReturn();

    String location = result.getResponse().getHeader("Location");
    String id = result.getResponse().getContentAsString()
        .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

    assertThat(location).endsWith(id);
  }
}
