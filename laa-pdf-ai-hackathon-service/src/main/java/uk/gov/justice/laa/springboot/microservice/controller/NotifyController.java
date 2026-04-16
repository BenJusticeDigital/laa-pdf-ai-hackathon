package uk.gov.justice.laa.springboot.microservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.springboot.microservice.service.NotifyService;
import uk.gov.service.notify.NotificationClientException;

/**
 * Controller for testing GOV.UK Notify email functionality.
 */
@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
@Slf4j
public class NotifyController {

  private final NotifyService notifyService;

  /**
   * Sends a sample test email.
   *
   * @param emailAddress the recipient email address
   * @return response with notification ID
   */
  @PostMapping("/send-sample-email")
  public ResponseEntity<String> sendSampleEmail(
      @RequestParam String emailAddress) {
    try {
      String notificationId = notifyService.sendSampleEmail(emailAddress);
      return ResponseEntity.ok("Email sent successfully. Notification ID: " + notificationId);
    } catch (NotificationClientException e) {
      log.error("Failed to send email", e);
      return ResponseEntity.internalServerError()
          .body("Failed to send email: " + e.getMessage());
    }
  }
}
