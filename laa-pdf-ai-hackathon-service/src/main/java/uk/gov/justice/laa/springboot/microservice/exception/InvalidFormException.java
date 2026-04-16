package uk.gov.justice.laa.springboot.microservice.exception;

/**
 * Thrown when the uploaded image is not recognised as a valid CW1 Legal Help form.
 */
public class InvalidFormException extends RuntimeException {

  public InvalidFormException(String message) {
    super(message);
  }
}

