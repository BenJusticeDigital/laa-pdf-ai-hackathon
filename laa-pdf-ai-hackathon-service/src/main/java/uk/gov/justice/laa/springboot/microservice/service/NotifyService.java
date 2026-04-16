package uk.gov.justice.laa.springboot.microservice.service;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

/**
 * Service for sending notifications via GOV.UK Notify.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyService {

  private final NotificationClient notificationClient;

  @Value("${notify.template-id}")
  private String defaultTemplateId;

  /**
   * Sends a sample email using GOV.UK Notify.
   *
   * @param emailAddress the recipient email address
   * @param templateId the Notify template ID
   * @param personalisation template personalisation parameters
   * @return the notification ID
   * @throws NotificationClientException if the email fails to send
   */
  public String sendEmail(String emailAddress, String templateId,
      Map<String, String> personalisation) throws NotificationClientException {
    log.info("Sending email to {} using template {}", emailAddress, templateId);

    SendEmailResponse response = notificationClient.sendEmail(
        templateId,
        emailAddress,
        personalisation,
        null // reference - optional
    );

    String notificationId = response.getNotificationId().toString();
    log.info("Email sent successfully. Notification ID: {}", notificationId);

    return notificationId;
  }

  /**
   * Sends a sample test email with default template.
   *
   * @param emailAddress the recipient email address
   * @return the notification ID
   * @throws NotificationClientException if the email fails to send
   */
  public String sendSampleEmail(String emailAddress) throws NotificationClientException {
    Map<String, String> personalisation = new HashMap<>();
    personalisation.put("name", "Test User");
    personalisation.put("application_reference", "CW1-2025-001");

    return sendEmail(emailAddress, defaultTemplateId, personalisation);
  }
}
