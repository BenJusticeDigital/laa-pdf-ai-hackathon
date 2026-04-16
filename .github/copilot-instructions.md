# GitHub Copilot Instructions

## What this repo is

This is an LAA (Legal Aid Agency) Spring Boot Java microservice, bootstrapped from the
[laa-spring-boot-microservice-template](https://github.com/ministryofjustice/laa-spring-boot-microservice-template).

The placeholder "Items" CRUD code (`ItemController`, `ItemService`, `ItemEntity`, etc.) is **example scaffolding
only and should be replaced** with the domain logic described below.

---

## What we're building

A **proof-of-concept GOV.UK service** that allows LAA caseworkers to upload a scanned PDF of a completed
**CW1 Legal Help form** and review/correct the data extracted from it.

### CW1 form
The CW1 form ("Legal Help, Help at Court and Family Help (Lower)") is a paper form submitted by solicitors
to the Legal Aid Agency to claim payment for legal aid work.

Reference copy: https://assets.publishing.service.gov.uk/media/679d0312c496e5d3ddafb69e/CW1_form__Version_32_February_2025.pdf

### User journey
1. **Upload** — a caseworker uploads a scanned PDF of a completed CW1 form
2. **Extract** — the backend sends the PDF to an OCR/document AI provider, which returns raw text and field positions
3. **Map & validate** — the service maps the extracted text to a structured JSON schema matching CW1 form fields, then validates the data against business rules
4. **Review & correct** — the caseworker is shown the extracted data in a GOV.UK-styled form, pre-populated with the OCR output, and can correct any mistakes
5. **Done** — the corrected data is confirmed (no downstream submission is in scope for this PoC)

---

## Architecture

This monorepo contains two main components:

### Backend — Spring Boot REST API
Gradle multi-module project:
- **`laa-pdf-ai-hackathon-api`** — OpenAPI specification defining the REST contract
- **`laa-pdf-ai-hackathon-service`** — Spring Boot application implementing the API

Responsibilities:
- `POST /api/v1/documents/upload` — accept a multipart PDF upload
- Call an OCR provider to extract text and field data from the scanned PDF
- Map raw OCR output to a structured `Cw1FormData` JSON schema
- Validate the structured data (required fields, format checks, business rules)
- Store the extracted/corrected data in session (H2 in-memory for PoC)
- `GET /api/v1/documents/{id}` — return structured form data for review
- `PUT /api/v1/documents/{id}` — accept caseworker corrections and re-validate

### Frontend — GOV.UK Frontend (Node.js / Nunjucks)
Located in the `/frontend` subdirectory of this repo.

- Built with [govuk-frontend](https://frontend.design-system.service.gov.uk/) npm package
- Nunjucks templating
- Calls the Spring Boot backend API
- Pages:
  - Upload page — file input, submit button
  - Review & correct page — form pre-populated with extracted data, inline error messages for validation failures

---

## OCR / Document AI

The OCR provider is **not yet decided**. Candidates:
- [Azure AI Document Intelligence](https://learn.microsoft.com/en-us/azure/ai-services/document-intelligence/)
- [AWS Textract](https://aws.amazon.com/textract/)
- [Google Cloud Document AI](https://cloud.google.com/document-ai)
- [Tesseract](https://github.com/tesseract-ocr/tesseract) (open source, self-hosted)

The OCR integration should be behind an interface (`OcrProvider`) so the implementation can be swapped.

---

## Key domain model

```
Cw1FormData
├── clientDetails      (name, date of birth, NI number, address)
├── solicitorDetails   (name, firm, LAA account number)
├── matterType         (legal help / help at court / family help lower)
├── caseDetails        (case reference, court, category of law)
├── costs              (profit costs, disbursements, VAT)
└── signatures         (solicitor signature, date)
```

---

## Tech stack

| Layer       | Technology                                      |
|-------------|------------------------------------------------|
| Backend     | Java 21, Spring Boot 3.x, Gradle               |
| API spec    | OpenAPI 3.0 (code-generated stubs)             |
| Persistence | H2 in-memory (PoC only)                        |
| Mapping     | MapStruct                                       |
| Boilerplate | Lombok                                          |
| Testing     | JUnit 5, Mockito, Spring Boot Test, RestAssured |
| Frontend    | Node.js, Nunjucks, govuk-frontend               |
| Logging     | ECS structured JSON (logback)                  |
| Monitoring  | Spring Boot Actuator, Sentry                   |

---

## Coding conventions

- **OpenAPI-first**: define or update the spec in the `-api` module; generate Java interfaces from it — do not hand-write controller interfaces
- Follow the existing package structure: `controller`, `service`, `repository`, `entity`, `mapper`, `exception`
- Use MapStruct for DTO ↔ Entity conversion; never map manually in controllers or services
- Use Lombok (`@Data`, `@Builder`, `@RequiredArgsConstructor`) to reduce boilerplate
- Keep controllers thin — delegate all logic to `@Service` classes
- Never put secrets or credentials in source code; use environment variables or Kubernetes secrets
- GOV.UK frontend: follow the [GOV.UK Design System](https://design-system.service.gov.uk/) — use standard components, accessible markup, plain English labels

---

## Useful links

- [CW1 form (PDF)](https://assets.publishing.service.gov.uk/media/679d0312c496e5d3ddafb69e/CW1_form__Version_32_February_2025.pdf)
- [GOV.UK Design System](https://design-system.service.gov.uk/)
- [govuk-frontend npm package](https://frontend.design-system.service.gov.uk/installing-with-npm/)
- [LAA Spring Boot common plugin](https://github.com/ministryofjustice/laa-spring-boot-common)
- [MoJ Technical Guidance](https://technical-guidance.service.justice.gov.uk/)
