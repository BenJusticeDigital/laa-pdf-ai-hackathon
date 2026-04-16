package uk.gov.justice.laa.springboot.microservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.justice.laa.springboot.microservice.entity.OcrOutputEntity;
import uk.gov.justice.laa.springboot.microservice.model.Cw1FormData;
import uk.gov.justice.laa.springboot.microservice.model.ImageResponse;
import uk.gov.justice.laa.springboot.microservice.model.ImageSummary;
import uk.gov.justice.laa.springboot.microservice.ocr.OcrProvider;
import uk.gov.justice.laa.springboot.microservice.ocr.OcrResult;
import uk.gov.justice.laa.springboot.microservice.repository.OcrOutputRepository;

/**
 * Service for handling image submissions.
 *
 * <p>Delegates OCR/AI extraction to the configured {@link OcrProvider},
 * persists the results to H2 via {@link OcrOutputRepository}, and provides
 * retrieval methods for the dashboard API.</p>
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
   * configured OCR provider for structured CW1 data extraction, persists
   * the result, and returns it with a unique submission ID.
   */
  public ImageResponse processImage(MultipartFile image, String email) {
    UUID id = UUID.randomUUID();

    log.info("Processing image submission [id={}] from [email={}], filename=[{}]",
        id, email, image.getOriginalFilename());

    OcrResult ocrResult = ocrProvider.extractFormData(image);
    Cw1FormData formData = ocrResult.getExtractedData();

    log.info("Extraction complete for submission [id={}], surname=[{}]",
        id, formData.getSurname());

    Map<String, Object> dataMap = objectMapper.convertValue(
        formData, new TypeReference<Map<String, Object>>() {});
    Map<String, Object> confidenceMap = objectMapper.convertValue(
        ocrResult.getConfidence(), new TypeReference<Map<String, Object>>() {});

    // Persist to H2
    try {
      String jsonData = objectMapper.writeValueAsString(dataMap);
      OcrOutputEntity entity = OcrOutputEntity.builder()
          .id(id)
          .data(jsonData)
          .email(email)
          .submittedAt(OffsetDateTime.now())
          .build();
      ocrOutputRepository.save(entity);
      log.info("Saved OCR output to database [id={}]", id);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialise extracted data for persistence [id={}]", id, e);
    }

    ImageResponse response = new ImageResponse(id);
    response.setExtractedData(dataMap);
    response.setConfidence(confidenceMap);
    return response;
  }

  /**
   * Returns a list of all image submissions.
   */
  public List<ImageSummary> listImages() {
    log.info("Listing all image submissions");
    return ocrOutputRepository.findAll().stream()
        .map(entity -> new ImageSummary(entity.getId(), entity.getEmail(), entity.getSubmittedAt()))
        .toList();
  }

  /**
   * Returns a single image submission by ID, including its extracted data.
   */
  public Optional<ImageResponse> getImageById(UUID id) {
    log.info("Getting image submission [id={}]", id);
    return ocrOutputRepository.findById(id)
        .map(entity -> {
          ImageResponse response = new ImageResponse(entity.getId());
          try {
            Map<String, Object> dataMap = objectMapper.readValue(
                entity.getData(), new TypeReference<Map<String, Object>>() {});
            response.setExtractedData(dataMap);
          } catch (JsonProcessingException e) {
            log.error("Failed to deserialise stored OCR data [id={}]", id, e);
          }
          return response;
        });
  }
}
