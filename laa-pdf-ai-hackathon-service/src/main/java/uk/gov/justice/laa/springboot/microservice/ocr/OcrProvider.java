package uk.gov.justice.laa.springboot.microservice.ocr;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction over an OCR / document AI provider.
 *
 * <p>Implementations can be swapped (Gemini, Azure Document Intelligence,
 * AWS Textract, Tesseract, etc.) without changing the service layer.</p>
 */
public interface OcrProvider {

  /**
   * Extracts structured field data from an image of a form.
   *
   * @param image the uploaded image file
   * @return a map of extracted field names to their values
   */
  Map<String, Object> extractFormData(MultipartFile image);
}
