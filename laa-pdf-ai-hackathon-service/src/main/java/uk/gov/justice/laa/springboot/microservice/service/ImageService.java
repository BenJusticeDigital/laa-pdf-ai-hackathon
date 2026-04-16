package uk.gov.justice.laa.springboot.microservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.justice.laa.springboot.microservice.entity.OcrOutputEntity;
import uk.gov.justice.laa.springboot.microservice.model.ImageResponse;
import uk.gov.justice.laa.springboot.microservice.ocr.OcrProvider;
import uk.gov.justice.laa.springboot.microservice.repository.OcrOutputRepository;

/**
 * Service for handling image submissions.
 *
 * <p>Delegates OCR/AI extraction to the configured {@link OcrProvider},
 * keeping the service layer decoupled from any specific AI vendor.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

  private final OcrProvider ocrProvider;
  private final OcrOutputRepository ocrOutputRepository;
  private final ObjectMapper objectMapper;

  /**
   * Accepts an uploaded image and associated metadata, passes it to the
   * configured OCR provider for structured data extraction, and returns
   * the result with a unique submission ID.
   *
   * @param image the uploaded image file
   * @param email the email address of the submitter
   * @return an {@link ImageResponse} containing the submission ID and extracted form data
   */
  public ImageResponse processImage(MultipartFile image, String email) {
    UUID id = UUID.randomUUID();

    log.info("Processing image submission [id={}] from [email={}], filename=[{}]",
        id, email, image.getOriginalFilename());

    Map<String, Object> extractedData = ocrProvider.extractFormData(image);

    log.info("Extraction complete for submission [id={}], fields extracted: {}",
        id, extractedData.keySet());

    // Serialize extracted data to JSON and persist to database
    try {
      String jsonData = objectMapper.writeValueAsString(extractedData);
      OcrOutputEntity entity = OcrOutputEntity.builder()
          .id(id)
          .data(jsonData)
          .build();
      ocrOutputRepository.save(entity);
      log.info("OCR output saved to database [id={}]", id);
    } catch (Exception e) {
      log.error("Failed to persist OCR output [id={}]", id, e);
      throw new RuntimeException("Failed to save OCR output", e);
    }

    ImageResponse response = new ImageResponse(id);
    response.setExtractedData(extractedData);
    return response;
  }
}
