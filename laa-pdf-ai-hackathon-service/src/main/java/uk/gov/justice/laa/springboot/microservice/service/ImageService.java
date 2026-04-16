package uk.gov.justice.laa.springboot.microservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.justice.laa.springboot.microservice.model.Cw1FormData;
import uk.gov.justice.laa.springboot.microservice.model.ImageResponse;
import uk.gov.justice.laa.springboot.microservice.model.ImageSummary;
import uk.gov.justice.laa.springboot.microservice.ocr.OcrProvider;

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
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Accepts an uploaded image and associated metadata, passes it to the
   * configured OCR provider for structured CW1 data extraction, and returns
   * the result with a unique submission ID.
   *
   * @param image the uploaded image file
   * @param email the email address of the submitter
   * @return an {@link ImageResponse} containing the submission ID and extracted CW1 form data
   */
  public ImageResponse processImage(MultipartFile image, String email) {
    UUID id = UUID.randomUUID();

    log.info("Processing image submission [id={}] from [email={}], filename=[{}]",
        id, email, image.getOriginalFilename());

    Cw1FormData formData = ocrProvider.extractFormData(image);

    log.info("Extraction complete for submission [id={}], surname=[{}]",
        id, formData.getSurname());

    Map<String, Object> dataMap = objectMapper.convertValue(
        formData, new TypeReference<Map<String, Object>>() {});


    ImageResponse response = new ImageResponse(id);
    response.setExtractedData(dataMap);
    return response;
  }

  /**
   * Returns a list of all image submissions.
   * TODO: wire to database once persistence is in place.
   *
   * @return list of {@link ImageSummary}
   */
  public List<ImageSummary> listImages() {
    log.info("Listing all image submissions (stub — DB not yet wired)");
    return Collections.emptyList();
  }

  /**
   * Returns a single image submission by ID.
   * TODO: wire to database once persistence is in place.
   *
   * @param id the submission ID
   * @return an {@link Optional} containing the {@link ImageResponse}, or empty if not found
   */
  public Optional<ImageResponse> getImageById(UUID id) {
    log.info("Getting image submission [id={}] (stub — DB not yet wired)", id);
    return Optional.empty();
  }
}
