package uk.gov.justice.laa.springboot.microservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Application-wide HTTP client and serialisation configuration.
 */
@Configuration
public class RestClientConfig {

  @Bean
  public RestClient restClient() {
    return RestClient.create();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}

