package uk.gov.justice.laa.springboot.microservice.ocr;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.justice.laa.springboot.microservice.model.Cw1FormConfidence;
import uk.gov.justice.laa.springboot.microservice.model.Cw1FormData;

/**
 * Wraps the structured extraction result from an OCR provider.
 *
 * <p>Contains both the extracted {@link Cw1FormData} and a parallel
 * {@link Cw1FormConfidence} object with per-field confidence scores,
 * plus a flag indicating whether the image was recognised as a CW1 form.</p>
 */
@Data
@AllArgsConstructor
public class OcrResult {

  private boolean validCw1Form;
  private Cw1FormData extractedData;
  private Cw1FormConfidence confidence;
}

