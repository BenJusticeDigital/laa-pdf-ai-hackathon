package uk.gov.justice.laa.springboot.microservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Confidence scores for each field extracted from a CW1 Legal Help form.
 *
 * <p>Each score is a value between 0.0 (no confidence) and 1.0 (full confidence),
 * as self-reported by the AI model based on legibility and clarity of the source image.</p>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cw1FormConfidence {

  @JsonProperty("applicationReference")
  private Double applicationReference;

  @JsonProperty("isExceptionalCaseFunding")
  private Double isExceptionalCaseFunding;

  @JsonProperty("ethnicity")
  private Double ethnicity;

  @JsonProperty("disabilityNotConsidered")
  private Double disabilityNotConsidered;

  @JsonProperty("disabilityMentalHealth")
  private Double disabilityMentalHealth;

  @JsonProperty("disabilityBlind")
  private Double disabilityBlind;

  @JsonProperty("disabilityLearning")
  private Double disabilityLearning;

  @JsonProperty("disabilityLongStandingIllness")
  private Double disabilityLongStandingIllness;

  @JsonProperty("disabilityMobility")
  private Double disabilityMobility;

  @JsonProperty("disabilityOther")
  private Double disabilityOther;

  @JsonProperty("disabilityDeaf")
  private Double disabilityDeaf;

  @JsonProperty("disabilityUnknown")
  private Double disabilityUnknown;

  @JsonProperty("disabilityHearingImpaired")
  private Double disabilityHearingImpaired;

  @JsonProperty("disabilityPreferNotSay")
  private Double disabilityPreferNotSay;

  @JsonProperty("disabilityVisuallyImpaired")
  private Double disabilityVisuallyImpaired;

  @JsonProperty("title")
  private Double title;

  @JsonProperty("initials")
  private Double initials;

  @JsonProperty("surname")
  private Double surname;

  @JsonProperty("firstName")
  private Double firstName;

  @JsonProperty("surnameAtBirth")
  private Double surnameAtBirth;

  @JsonProperty("dateOfBirth")
  private Double dateOfBirth;

  @JsonProperty("nationalInsuranceNumber")
  private Double nationalInsuranceNumber;

  @JsonProperty("sex")
  private Double sex;

  @JsonProperty("maritalStatus")
  private Double maritalStatus;

  @JsonProperty("placeOfBirthTown")
  private Double placeOfBirthTown;

  @JsonProperty("job")
  private Double job;

  @JsonProperty("currentAddress")
  private Double currentAddress;

  @JsonProperty("currentAddressLine2")
  private Double currentAddressLine2;

  @JsonProperty("postcode")
  private Double postcode;
}

