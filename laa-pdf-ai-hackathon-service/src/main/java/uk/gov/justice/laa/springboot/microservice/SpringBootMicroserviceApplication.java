package uk.gov.justice.laa.springboot.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Entry point for the Spring Boot microservice application.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class SpringBootMicroserviceApplication {

  /**
   * The application main method.
   *
   * @param args the application arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(SpringBootMicroserviceApplication.class, args);
  }
}
