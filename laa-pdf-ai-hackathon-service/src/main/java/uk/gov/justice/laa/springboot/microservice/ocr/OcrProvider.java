package uk.gov.justice.laa.springboot.microservice.ocr;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction over an OCR / document AI provider.
 *
 * <p>Implementations can be swapped (Gemini, Azure Document Intelligence,
 * AWS Textract, Tesseract, etc.) without changing the service layer.</p>
 */
public interface OcrProvider {

  /**
   * Extracts structured CW1 form data from an image, along with per-field confidence scores.
   *
   * @param image the uploaded image file
   * @return an {@link OcrResult} containing extracted data and confidence scores
   */
  OcrResult extractFormData(MultipartFile image);
}
