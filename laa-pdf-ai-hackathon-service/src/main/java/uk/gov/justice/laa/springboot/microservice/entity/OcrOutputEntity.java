package uk.gov.justice.laa.springboot.microservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing extracted OCR data from a submitted form.
 *
 * <p>Stores the raw JSON data extracted from a scanned PDF document.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ocr_output")
public class OcrOutputEntity {

  @Id
  private UUID id;

  /**
   * Raw JSON data extracted from the OCR provider.
   * Stored as a VARCHAR containing serialized JSON.
   */
  private String data;
}

