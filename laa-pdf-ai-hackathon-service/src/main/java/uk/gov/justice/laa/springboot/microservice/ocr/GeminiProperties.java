package uk.gov.justice.laa.springboot.microservice.ocr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds {@code gemini.*} properties from application configuration.
 *
 * <p>The API key should be supplied via the {@code GEMINI_API_KEY}
 * environment variable — never hardcoded in source.</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

  /**
   * Google Gemini API key. Set via GEMINI_API_KEY environment variable.
   */
  private String apiKey;
}
