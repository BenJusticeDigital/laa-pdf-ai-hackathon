package uk.gov.justice.laa.springboot.microservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.justice.laa.springboot.microservice.model.ImageResponse;

import java.util.UUID;

/**
 * Service for handling image submissions.
 *
 * <p>Currently stubs image processing — intended to be extended to pass the image
 * to an AI/vision model in a future stage.</p>
 */
@Slf4j
@Service
public class ImageService {

    /**
     * Accepts an uploaded image and associated metadata, assigns it a unique ID,
     * and prepares it for downstream AI processing.
     *
     * @param image the uploaded image file
     * @param email the email address of the submitter
     * @return an {@link ImageResponse} containing the unique ID of this submission
     */
    public ImageResponse processImage(MultipartFile image, String email) {
        UUID id = UUID.randomUUID();

        log.info("Received image submission [id={}] from [email={}], filename=[{}], size=[{}] bytes",
                id, email, image.getOriginalFilename(), image.getSize());

        // TODO: pass the image to an AI/vision service for analysis
        //       e.g. aiVisionService.analyse(id, image, email);

        return new ImageResponse(id);
    }
}

