package uk.gov.justice.laa.springboot.microservice.exception;

/** Items scaffold — pending deletion. */
@Deprecated
public class ItemNotFoundException extends RuntimeException {

  public ItemNotFoundException(String message) {
    super(message);
  }
}
