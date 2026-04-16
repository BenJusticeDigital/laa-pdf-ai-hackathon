package uk.gov.justice.laa.springboot.microservice.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.justice.laa.springboot.microservice.model.Cw1FormData;

/**
 * OCR provider backed by Google Gemini 2.5 Flash.
 *
 * <p>Uses Gemini's native structured output (responseSchema) to constrain the
 * model's response to the CW1 form schema, returning a fully typed
 * {@link Cw1FormData} object.</p>
 *
 * <p>Requires the {@code GEMINI_API_KEY} environment variable to be set.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiOcrProvider implements OcrProvider {

  private static final String GEMINI_API_URL =
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

  private static final String PROMPT =
      "You are a form data extraction assistant. "
      + "The image shows a completed CW1 Legal Help paper form. "
      + "Extract every visible field value from the form and populate the response schema. "
      + "Use null for any field that is blank or cannot be read clearly. "
      + "For boolean fields (checkboxes), use true if the box is ticked, false if unticked, "
      + "null if not visible. "
      + "Return dates in the format DD/MM/YYYY as a string.";

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final RestClient restClient;
  private final GeminiProperties geminiProperties;

  @Override
  public Cw1FormData extractFormData(MultipartFile image) {
    log.info("Sending image to Gemini for structured CW1 extraction, filename={}, size={} bytes",
        image.getOriginalFilename(), image.getSize());

    String base64Image = encodeImageToBase64(image);
    String mimeType = resolveMimeType(image);
    Map<String, Object> requestBody = buildRequestBody(base64Image, mimeType);

    String rawResponse = restClient.post()
        .uri(GEMINI_API_URL + "?key=" + geminiProperties.getApiKey())
        .header("Content-Type", "application/json")
        .body(requestBody)
        .retrieve()
        .body(String.class);

    log.debug("Raw Gemini response: {}", rawResponse);

    return parseCw1FormData(rawResponse);
  }

  private String encodeImageToBase64(MultipartFile image) {
    try {
      return Base64.getEncoder().encodeToString(image.getBytes());
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to read image bytes for OCR processing", ex);
    }
  }

  private String resolveMimeType(MultipartFile image) {
    String contentType = image.getContentType();
    return (contentType != null && !contentType.isBlank()) ? contentType : "image/jpeg";
  }

  private Map<String, Object> buildRequestBody(String base64Image, String mimeType) {
    return Map.of(
        "contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", PROMPT),
                Map.of("inline_data", Map.of(
                    "mime_type", mimeType,
                    "data", base64Image
                ))
            ))
        ),
        "generationConfig", Map.of(
            "responseMimeType", "application/json",
            "responseSchema", buildCw1ResponseSchema()
        )
    );
  }

  private Map<String, Object> buildCw1ResponseSchema() {
    return Map.of(
        "type", "OBJECT",
        "properties", Map.ofEntries(
            Map.entry("applicationReference", Map.of("type", "STRING")),
            Map.entry("isExceptionalCaseFunding", Map.of("type", "BOOLEAN")),
            Map.entry("ethnicity", Map.of("type", "STRING")),
            Map.entry("disabilityNotConsidered", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityMentalHealth", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityBlind", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityLearning", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityLongStandingIllness", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityMobility", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityOther", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityDeaf", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityUnknown", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityHearingImpaired", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityPreferNotSay", Map.of("type", "BOOLEAN")),
            Map.entry("disabilityVisuallyImpaired", Map.of("type", "BOOLEAN")),
            Map.entry("title", Map.of("type", "STRING")),
            Map.entry("initials", Map.of("type", "STRING")),
            Map.entry("surname", Map.of("type", "STRING")),
            Map.entry("firstName", Map.of("type", "STRING")),
            Map.entry("surnameAtBirth", Map.of("type", "STRING")),
            Map.entry("dateOfBirth", Map.of("type", "STRING")),
            Map.entry("nationalInsuranceNumber", Map.of("type", "STRING")),
            Map.entry("sex", Map.of("type", "STRING")),
            Map.entry("maritalStatus", Map.of("type", "STRING")),
            Map.entry("placeOfBirthTown", Map.of("type", "STRING")),
            Map.entry("job", Map.of("type", "STRING")),
            Map.entry("currentAddress", Map.of("type", "STRING")),
            Map.entry("currentAddressLine2", Map.of("type", "STRING")),
            Map.entry("postcode", Map.of("type", "STRING"))
        )
    );
  }

  private Cw1FormData parseCw1FormData(String rawResponse) {
    try {
      JsonNode root = objectMapper.readTree(rawResponse);
      String json = root
          .path("candidates").get(0)
          .path("content")
          .path("parts").get(0)
          .path("text")
          .asText();

      log.info("Gemini structured response received, deserialising into Cw1FormData");

      return objectMapper.readValue(json, Cw1FormData.class);
    } catch (Exception ex) {
      log.error("Failed to deserialise Gemini response into Cw1FormData", ex);
      return new Cw1FormData();
    }
  }
}

