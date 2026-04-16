# CW1 OCR Extraction Response — API Specification

This document describes the JSON structure returned by the AI/OCR model after processing a scanned CW1 Legal Help form. It is the contract between the OCR extraction service and the Spring Boot backend.

## Files

| File | Purpose |
|------|---------|
| [`ocr-response-schema.json`](./ocr-response-schema.json) | JSON Schema (draft-07) — machine-readable contract |
| [`ocr-response-example.json`](./ocr-response-example.json) | Realistic example response with OCR corrections, low-confidence fields, and warnings |

---

## Overview

```
POST /api/v1/documents/upload  →  [OCR Provider]  →  CW1 Extraction Response (this schema)
```

The backend sends the uploaded PDF to an OCR provider, which returns a structured JSON response. The backend then validates this response, persists it, and serves it to the frontend for caseworker review.

### Design principles

1. **Every field carries a confidence score** (0.0–1.0) — the frontend uses this to highlight uncertain fields
2. **Raw values are preserved** alongside normalised values — so caseworkers can see what the OCR actually read
3. **Bounding boxes** tie each field back to its position on the scanned page — enabling future "click to see source" features
4. **Warnings** surface OCR corrections, quality issues, and missing fields in a structured array
5. **null values** explicitly indicate fields the model could not extract — distinguished from empty/blank fields on the form

---

## Response envelope

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `extraction_id` | UUID | ✅ | Unique identifier for this extraction job |
| `status` | enum | ✅ | `"complete"` — all fields extracted · `"partial"` — some fields missing · `"failed"` — extraction unusable |
| `model` | object | ✅ | Provider, model ID, and version used for extraction |
| `source_document` | object | ✅ | Original document metadata and quality assessment |
| `extracted_at` | ISO 8601 | ✅ | When extraction completed |
| `processing_time_ms` | integer | | Time taken in milliseconds |
| `overall_confidence` | float | | Mean confidence across all extracted fields (0.0–1.0) |
| `fields_extracted` | integer | | Count of fields successfully extracted |
| `fields_total` | integer | | Total fields the model attempted to extract |
| `form_data` | object | ✅ | The extracted CW1 data, grouped by section |
| `warnings` | array | | Non-fatal issues for caseworker review |

---

## The `extracted_field` type

Every form field is wrapped in this structure:

```json
{
  "value": "Patel",
  "raw_value": "Pate1",
  "confidence": 0.84,
  "source_page": 2,
  "bounding_box": {
    "top": 0.12,
    "left": 0.10,
    "width": 0.20,
    "height": 0.03
  }
}
```

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `value` | any | ✅ | The extracted & normalised value. `null` if extraction failed. Type varies by field (string, boolean, number, date as ISO string). |
| `raw_value` | string | | The raw OCR text before normalisation. Only present when normalisation changed the value. Useful for caseworker review. |
| `confidence` | float | ✅ | Model confidence for this field. `0.0` = no confidence / not found, `1.0` = certain. |
| `source_page` | integer | | Page number where this field was found (1-indexed). |
| `bounding_box` | object | | Normalised coordinates (0.0–1.0) of the field on the page. Origin is top-left. |

### Confidence thresholds (recommended)

| Range | Meaning | Frontend treatment |
|-------|---------|--------------------|
| ≥ 0.90 | High confidence | Show value as-is |
| 0.60–0.89 | Medium confidence | Show value with amber warning indicator |
| 0.01–0.59 | Low confidence | Show value with red warning, prompt caseworker to verify |
| 0.0 | Not extracted | Show as missing field — caseworker must provide |

---

## Form data sections

The `form_data` object groups fields by CW1 form section:

### `application`
Top-level form metadata.

| Field | Value type | Description |
|-------|-----------|-------------|
| `application_reference` | string | LAA application reference number |
| `form_type` | string | Form type identifier (e.g. "CW1 — Legal Help") |
| `is_exceptional_case_funding` | boolean | Whether ECF applies |
| `ecf_reference` | string | ECF reference number (null if ECF not applicable) |

### `provider_details`
Section 1 — Solicitor/provider information.

| Field | Value type | Description |
|-------|-----------|-------------|
| `provider_name` | string | Name of the solicitor firm |
| `laa_account_number` | string | LAA account number |
| `supplier_number` | string | Supplier number |
| `office_name` | string | Office name |
| `office_address` | string | Office street address |
| `office_postcode` | string | Office postcode |
| `office_telephone` | string | Office phone number |
| `solicitor_name` | string | Named solicitor handling the case |
| `solicitor_reference` | string | Solicitor's own case reference |

### `client_details`
Client personal information.

| Field | Value type | Description |
|-------|-----------|-------------|
| `title` | string | Mr, Mrs, Miss, Ms, Dr, etc. |
| `first_name` | string | Client's first name |
| `initials` | string | Client's initials |
| `surname` | string | Client's surname |
| `surname_at_birth` | string | Maiden name / surname at birth (null if same or not provided) |
| `date_of_birth` | string (YYYY-MM-DD) | Date of birth in ISO format |
| `national_insurance_number` | string | NI number (format: AA999999A) |
| `sex` | string | Male, Female, or Prefer not to say |
| `marital_status` | string | Single, Married, Divorced, Widowed, Cohabiting, etc. |
| `place_of_birth_town` | string | Town/city of birth |
| `job` | string | Current occupation |
| `current_address` | string | Address line 1 |
| `current_address_line2` | string | Address line 2 (area, city) |
| `postcode` | string | Current postcode |

### `case_details`
Details of the legal matter.

| Field | Value type | Description |
|-------|-----------|-------------|
| `category_of_law` | string | E.g. "Family", "Housing", "Debt", "Immigration" |
| `matter_type` | string | "Legal Help", "Help at Court", "Family Help (Lower)" |
| `case_reference` | string | Case reference number |
| `court_name` | string | Name of the court |
| `proceedings_type` | string | Type of proceedings |
| `date_case_started` | string (YYYY-MM-DD) | When the case started |
| `date_case_ended` | string (YYYY-MM-DD) | When the case ended (null if ongoing) |

### `equal_opportunities`
Equal opportunities monitoring section.

| Field | Value type | Description |
|-------|-----------|-------------|
| `ethnicity` | string | Selected ethnicity category |
| `disability` | object | Object containing 12 boolean `extracted_field` entries for each disability checkbox |

**Disability sub-fields:** `not_considered_disabled`, `mental_health`, `blind`, `learning_disability`, `long_standing_illness`, `mobility`, `deaf`, `hearing_impaired`, `visually_impaired`, `other`, `unknown`, `prefer_not_to_say`

### `proceedings`
Proceedings and scope details.

| Field | Value type | Description |
|-------|-----------|-------------|
| `proceedings_description` | string | Free-text description of proceedings |
| `scope_limitation` | string | Scope limitation text |
| `delegated_functions_used` | boolean | Whether delegated functions were used |
| `delegated_functions_date` | string (YYYY-MM-DD) | Date delegated functions were used |

### `costs`
Financial costs claimed.

| Field | Value type | Description |
|-------|-----------|-------------|
| `profit_costs` | number | Profit costs in GBP |
| `disbursements` | number | Disbursements in GBP |
| `counsel_costs` | number | Counsel costs in GBP |
| `vat_on_profit_costs` | number | VAT on profit costs |
| `vat_on_disbursements` | number | VAT on disbursements |
| `vat_on_counsel` | number | VAT on counsel costs |
| `travel_and_waiting` | number | Travel and waiting costs in GBP |
| `total_claim` | number | Total claim amount in GBP |

### `declaration`
Signature and declaration section.

| Field | Value type | Description |
|-------|-----------|-------------|
| `solicitor_signature_present` | boolean | Whether a solicitor signature was detected |
| `client_signature_present` | boolean | Whether a client signature was detected |
| `date_signed` | string (YYYY-MM-DD) | Date the form was signed |

---

## Warnings

The `warnings` array contains structured alerts about the extraction:

```json
{
  "code": "LOW_CONFIDENCE",
  "message": "Place of birth could not be reliably extracted due to faint text",
  "field": "client_details.place_of_birth_town",
  "severity": "error"
}
```

| Code | Meaning |
|------|---------|
| `LOW_CONFIDENCE` | A field was extracted but confidence is below the usable threshold |
| `MISSING_FIELD` | A field appears blank or was not found on the form |
| `OCR_CORRECTION` | The model auto-corrected a common OCR mistake (e.g. O→0, 1→l) |
| `QUALITY_ISSUE` | A document quality problem was detected (blur, fold, stain) |
| `SIGNATURE_LOW_QUALITY` | A signature was detected but is smudged or partial |
| `FORMAT_MISMATCH` | An extracted value doesn't match expected format (e.g. NI number pattern) |
| `CROSS_FIELD_INCONSISTENCY` | Two fields appear to contradict each other |

### Severity levels

| Level | Meaning |
|-------|---------|
| `info` | Informational — no action needed (e.g. auto-correction applied) |
| `warning` | Caseworker should review but extraction may be correct |
| `error` | Field is unusable — caseworker must provide or correct the value |

---

## How the frontend should use this

1. **Flatten `form_data`** — extract `.value` from each `extracted_field` for display
2. **Flag low-confidence fields** — any field with `confidence < 0.60` should show an amber/red indicator
3. **Show raw values** — when `raw_value` differs from `value`, offer a tooltip: _"OCR read: {raw_value}"_
4. **Surface warnings** — display the `warnings` array as a GOV.UK error/warning summary at the top of the review page
5. **Track null fields** — fields where `value` is `null` must be filled in by the caseworker before submission

---

## Mapping to the existing database schema

The `form_data` fields map directly to the `CW1_APPLICATIONS` table:

| JSON path | DB column |
|-----------|-----------|
| `application.application_reference.value` | `application_reference` |
| `application.is_exceptional_case_funding.value` | `is_exceptional_case_funding` |
| `client_details.title.value` | `title` |
| `client_details.first_name.value` | `first_name` |
| `client_details.initials.value` | `initials` |
| `client_details.surname.value` | `surname` |
| `client_details.surname_at_birth.value` | `surname_at_birth` |
| `client_details.date_of_birth.value` | `date_of_birth` |
| `client_details.national_insurance_number.value` | `national_insurance_number` |
| `client_details.sex.value` | `sex` |
| `client_details.marital_status.value` | `marital_status` |
| `client_details.place_of_birth_town.value` | `place_of_birth_town` |
| `client_details.job.value` | `job` |
| `client_details.current_address.value` | `current_address` |
| `client_details.current_address_line2.value` | `current_address_line2` |
| `client_details.postcode.value` | `postcode` |
| `equal_opportunities.ethnicity.value` | `ethnicity` |
| `equal_opportunities.disability.not_considered_disabled.value` | `disability_not_considered` |
| `equal_opportunities.disability.mental_health.value` | `disability_mental_health` |
| `equal_opportunities.disability.blind.value` | `disability_blind` |
| `equal_opportunities.disability.learning_disability.value` | `disability_learning` |
| `equal_opportunities.disability.long_standing_illness.value` | `disability_long_standing_illness` |
| `equal_opportunities.disability.mobility.value` | `disability_mobility` |
| `equal_opportunities.disability.deaf.value` | `disability_deaf` |
| `equal_opportunities.disability.hearing_impaired.value` | `disability_hearing_impaired` |
| `equal_opportunities.disability.visually_impaired.value` | `disability_visually_impaired` |
| `equal_opportunities.disability.other.value` | `disability_other` |
| `equal_opportunities.disability.unknown.value` | `disability_unknown` |
| `equal_opportunities.disability.prefer_not_to_say.value` | `disability_prefer_not_say` |

> **Note:** Fields in `provider_details`, `case_details`, `proceedings`, `costs`, and `declaration` are not yet in the database schema. The schema should be extended to accommodate them as the PoC matures.
