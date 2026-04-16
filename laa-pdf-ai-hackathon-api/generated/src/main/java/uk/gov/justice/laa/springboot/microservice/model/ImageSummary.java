package uk.gov.justice.laa.springboot.microservice.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ImageSummary
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-16T14:22:28.390778+01:00[Europe/London]", comments = "Generator version: 7.18.0")
public class ImageSummary implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;

  private String email;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime submittedAt;

  public ImageSummary() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ImageSummary(UUID id, String email, OffsetDateTime submittedAt) {
    this.id = id;
    this.email = email;
    this.submittedAt = submittedAt;
  }

  public ImageSummary id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the submission
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", description = "The unique identifier of the submission", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ImageSummary email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Email address of the submitter
   * @return email
   */
  @NotNull 
  @Schema(name = "email", description = "Email address of the submitter", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public ImageSummary submittedAt(OffsetDateTime submittedAt) {
    this.submittedAt = submittedAt;
    return this;
  }

  /**
   * Timestamp when the submission was received
   * @return submittedAt
   */
  @NotNull @Valid 
  @Schema(name = "submittedAt", description = "Timestamp when the submission was received", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("submittedAt")
  public OffsetDateTime getSubmittedAt() {
    return submittedAt;
  }

  public void setSubmittedAt(OffsetDateTime submittedAt) {
    this.submittedAt = submittedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ImageSummary imageSummary = (ImageSummary) o;
    return Objects.equals(this.id, imageSummary.id) &&
        Objects.equals(this.email, imageSummary.email) &&
        Objects.equals(this.submittedAt, imageSummary.submittedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, submittedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImageSummary {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    submittedAt: ").append(toIndentedString(submittedAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
  public static class Builder {

    private ImageSummary instance;

    public Builder() {
      this(new ImageSummary());
    }

    protected Builder(ImageSummary instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ImageSummary value) { 
      this.instance.setId(value.id);
      this.instance.setEmail(value.email);
      this.instance.setSubmittedAt(value.submittedAt);
      return this;
    }

    public ImageSummary.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public ImageSummary.Builder email(String email) {
      this.instance.email(email);
      return this;
    }
    
    public ImageSummary.Builder submittedAt(OffsetDateTime submittedAt) {
      this.instance.submittedAt(submittedAt);
      return this;
    }
    
    /**
    * returns a built ImageSummary instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ImageSummary build() {
      try {
        return this.instance;
      } finally {
        // ensure that this.instance is not reused
        this.instance = null;
      }
    }

    @Override
    public String toString() {
      return getClass() + "=(" + instance + ")";
    }
  }

  /**
  * Create a builder with no initialized field (except for the default values).
  */
  public static ImageSummary.Builder builder() {
    return new ImageSummary.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ImageSummary.Builder toBuilder() {
    ImageSummary.Builder builder = new ImageSummary.Builder();
    return builder.copyOf(this);
  }

}

