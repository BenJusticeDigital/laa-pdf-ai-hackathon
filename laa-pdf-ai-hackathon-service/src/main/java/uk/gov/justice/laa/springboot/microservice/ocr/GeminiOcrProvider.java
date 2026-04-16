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

/**
 * OCR provider backed by Google Gemini 1.5 Flash.
 *
 * <p>Sends the image as a base64-encoded inline part alongside a structured
 * extraction prompt. Gemini returns a JSON object of form fields which is
 * parsed and returned to the caller.</p>
 *
 * <p>Requires {@code gemini.api.key} to be set in application configuration
 * (sourced from the {@code GEMINI_API_KEY} environment variable).</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiOcrProvider implements OcrProvider {

  private static final String GEMINI_API_URL =
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

  private static final String PROMPT = """
      You are a form data extraction assistant.
      The image shows a completed paper form.
      Extract every visible field and its value from the form.
      Return ONLY a valid JSON object where each key is the field label and each value is the
      field content as written on the form.
      If a field is blank, include it with a null value.
      Do not include any explanation, markdown, or code fences — just the raw JSON object.
      """;

  private final RestClient restClient;
  private final ObjectMapper objectMapper;
  private final GeminiProperties geminiProperties;

  @Override
  public Map<String, Object> extractFormData(MultipartFile image) {
    log.info("Sending image to Gemini for extraction, filename={}, size={} bytes",
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

    return parseExtractedData(rawResponse);
  }

  private String encodeImageToBase64(MultipartFile image) {
    try {
      return Base64.getEncoder().encodeToString(image.getBytes());
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read image bytes for OCR processing", e);
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
        )
    );
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> parseExtractedData(String rawResponse) {
    try {
      JsonNode root = objectMapper.readTree(rawResponse);
      String text = root
          .path("candidates").get(0)
          .path("content")
          .path("parts").get(0)
          .path("text")
          .asText();

      log.info("Gemini extracted text: {}", text);

      return objectMapper.readValue(text, Map.class);
    } catch (Exception e) {
      log.error("Failed to parse Gemini response as structured JSON, returning raw response", e);
      return Map.of("rawResponse", rawResponse);
    }
  }
}

