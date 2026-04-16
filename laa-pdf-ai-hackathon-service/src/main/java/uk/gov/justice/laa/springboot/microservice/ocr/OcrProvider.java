package uk.gov.justice.laa.springboot.microservice.ocr;

import org.springframework.web.multipart.MultipartFile;
import uk.gov.justice.laa.springboot.microservice.model.Cw1FormData;

/**
 * Abstraction over an OCR / document AI provider.
 *
 * <p>Implementations can be swapped (Gemini, Azure Document Intelligence,
 * AWS Textract, Tesseract, etc.) without changing the service layer.</p>
 */
public interface OcrProvider {

  /**
   * Extracts structured CW1 form data from an image.
   *
   * @param image the uploaded image file
   * @return a {@link Cw1FormData} populated with values extracted from the image
   */
  Cw1FormData extractFormData(MultipartFile image);
}
