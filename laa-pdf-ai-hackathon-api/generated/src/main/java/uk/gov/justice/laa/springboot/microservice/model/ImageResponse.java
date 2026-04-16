package uk.gov.justice.laa.springboot.microservice.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
 * ImageResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-16T14:22:28.390778+01:00[Europe/London]", comments = "Generator version: 7.18.0")
public class ImageResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;

  @Valid
  private Map<String, Object> extractedData = new HashMap<>();

  @Valid
  private Map<String, Object> confidence = new HashMap<>();

  public ImageResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ImageResponse(UUID id) {
    this.id = id;
  }

  public ImageResponse id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the created image submission
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", description = "The unique identifier of the created image submission", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ImageResponse extractedData(Map<String, Object> extractedData) {
    this.extractedData = extractedData;
    return this;
  }

  public ImageResponse putExtractedDataItem(String key, Object extractedDataItem) {
    if (this.extractedData == null) {
      this.extractedData = new HashMap<>();
    }
    this.extractedData.put(key, extractedDataItem);
    return this;
  }

  /**
   * Structured JSON data extracted from the image by the AI provider
   * @return extractedData
   */
  
  @Schema(name = "extractedData", description = "Structured JSON data extracted from the image by the AI provider", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("extractedData")
  public Map<String, Object> getExtractedData() {
    return extractedData;
  }

  public void setExtractedData(Map<String, Object> extractedData) {
    this.extractedData = extractedData;
  }

  public ImageResponse confidence(Map<String, Object> confidence) {
    this.confidence = confidence;
    return this;
  }

  public ImageResponse putConfidenceItem(String key, Object confidenceItem) {
    if (this.confidence == null) {
      this.confidence = new HashMap<>();
    }
    this.confidence.put(key, confidenceItem);
    return this;
  }

  /**
   * Per-field confidence scores (0.0–1.0) as reported by the AI provider
   * @return confidence
   */
  
  @Schema(name = "confidence", description = "Per-field confidence scores (0.0–1.0) as reported by the AI provider", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("confidence")
  public Map<String, Object> getConfidence() {
    return confidence;
  }

  public void setConfidence(Map<String, Object> confidence) {
    this.confidence = confidence;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ImageResponse imageResponse = (ImageResponse) o;
    return Objects.equals(this.id, imageResponse.id) &&
        Objects.equals(this.extractedData, imageResponse.extractedData) &&
        Objects.equals(this.confidence, imageResponse.confidence);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, extractedData, confidence);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImageResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    extractedData: ").append(toIndentedString(extractedData)).append("\n");
    sb.append("    confidence: ").append(toIndentedString(confidence)).append("\n");
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

    private ImageResponse instance;

    public Builder() {
      this(new ImageResponse());
    }

    protected Builder(ImageResponse instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ImageResponse value) { 
      this.instance.setId(value.id);
      this.instance.setExtractedData(value.extractedData);
      this.instance.setConfidence(value.confidence);
      return this;
    }

    public ImageResponse.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public ImageResponse.Builder extractedData(Map<String, Object> extractedData) {
      this.instance.extractedData(extractedData);
      return this;
    }
    
    public ImageResponse.Builder confidence(Map<String, Object> confidence) {
      this.instance.confidence(confidence);
      return this;
    }
    
    /**
    * returns a built ImageResponse instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ImageResponse build() {
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
  public static ImageResponse.Builder builder() {
    return new ImageResponse.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ImageResponse.Builder toBuilder() {
    ImageResponse.Builder builder = new ImageResponse.Builder();
    return builder.copyOf(this);
  }

}

