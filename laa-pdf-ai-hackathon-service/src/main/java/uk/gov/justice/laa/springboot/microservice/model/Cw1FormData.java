package uk.gov.justice.laa.springboot.microservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Structured data extracted from a CW1 Legal Help form.
 *
 * <p>Field names map directly to the CW1_APPLICATIONS database schema.
 * All fields are nullable — OCR extraction may not find every field.</p>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cw1FormData {

  // Application reference
  @JsonProperty("applicationReference")
  private String applicationReference;

  // Exceptional Case Funding
  @JsonProperty("isExceptionalCaseFunding")
  private Boolean isExceptionalCaseFunding;

  // Equal Opportunities Monitoring — Ethnicity
  @JsonProperty("ethnicity")
  private String ethnicity;

  // Equal Opportunities Monitoring — Disability
  @JsonProperty("disabilityNotConsidered")
  private Boolean disabilityNotConsidered;

  @JsonProperty("disabilityMentalHealth")
  private Boolean disabilityMentalHealth;

  @JsonProperty("disabilityBlind")
  private Boolean disabilityBlind;

  @JsonProperty("disabilityLearning")
  private Boolean disabilityLearning;

  @JsonProperty("disabilityLongStandingIllness")
  private Boolean disabilityLongStandingIllness;

  @JsonProperty("disabilityMobility")
  private Boolean disabilityMobility;

  @JsonProperty("disabilityOther")
  private Boolean disabilityOther;

  @JsonProperty("disabilityDeaf")
  private Boolean disabilityDeaf;

  @JsonProperty("disabilityUnknown")
  private Boolean disabilityUnknown;

  @JsonProperty("disabilityHearingImpaired")
  private Boolean disabilityHearingImpaired;

  @JsonProperty("disabilityPreferNotSay")
  private Boolean disabilityPreferNotSay;

  @JsonProperty("disabilityVisuallyImpaired")
  private Boolean disabilityVisuallyImpaired;

  // Client Details
  @JsonProperty("title")
  private String title;

  @JsonProperty("initials")
  private String initials;

  @JsonProperty("surname")
  private String surname;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("surnameAtBirth")
  private String surnameAtBirth;

  @JsonProperty("dateOfBirth")
  private String dateOfBirth;

  @JsonProperty("nationalInsuranceNumber")
  private String nationalInsuranceNumber;

  @JsonProperty("sex")
  private String sex;

  @JsonProperty("maritalStatus")
  private String maritalStatus;

  @JsonProperty("placeOfBirthTown")
  private String placeOfBirthTown;

  @JsonProperty("job")
  private String job;

  @JsonProperty("currentAddress")
  private String currentAddress;

  @JsonProperty("currentAddressLine2")
  private String currentAddressLine2;

  @JsonProperty("postcode")
  private String postcode;
}

