package uk.gov.justice.laa.springboot.microservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.service.notify.NotificationClient;

/**
 * Configuration for GOV.UK Notify client.
 */
@Configuration
public class NotifyConfig {

  @Value("${notify.api-key}")
  private String apiKey;

  @Bean
  public NotificationClient notificationClient() {
    return new NotificationClient(apiKey);
  }
}
