package uk.gov.justice.laa.springboot.microservice.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.justice.laa.springboot.microservice.api.ImageApi;
import uk.gov.justice.laa.springboot.microservice.model.ImageResponse;
import uk.gov.justice.laa.springboot.microservice.model.ImageSummary;
import uk.gov.justice.laa.springboot.microservice.service.ImageService;

/**
 * Controller for handling image upload and retrieval requests.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController implements ImageApi {

  private final ImageService imageService;

  @Override
  public ResponseEntity<List<ImageSummary>> listImages() {
    log.info("Listing all image submissions");
    return ResponseEntity.ok(imageService.listImages());
  }

  @Override
  public ResponseEntity<ImageResponse> getImageById(UUID id) {
    log.info("Getting image submission [id={}]", id);
    return imageService.getImageById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<ImageResponse> uploadImage(MultipartFile image, String email) {
    log.info("Received image upload request from email={}", email);

    ImageResponse response = imageService.processImage(image, email);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.getId())
        .toUri();

    return ResponseEntity.created(location).body(response);
  }
}
