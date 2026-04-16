# laa-pdf-ai-hackathon

A Spring Boot REST API for submitting images for AI processing.

---

## Requirements

- Java 21+
- Gradle (wrapper included)
- Docker (optional)

---

## Build & Run

### Build
```bash
./gradlew clean build
```

### Run locally
```bash
./gradlew bootRun
```

### Run with local profile (human-readable console logs)
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Run via Docker
```bash
docker compose up
```

---

## API Endpoints

Base URL: `http://localhost:8081`

### Swagger UI
`http://localhost:8081/swagger-ui/index.html`

### Health Check
```bash
curl http://localhost:8181/actuator/health
```

---

## Image Upload

### `POST /api/v1/image`

Accepts a `multipart/form-data` request containing an image file and submitter email address.  
Returns `201 Created` with the unique ID of the submission.

#### Request fields

| Field   | Type   | Required | Description                    |
|---------|--------|----------|--------------------------------|
| `image` | file   | Yes      | The image file to be processed |
| `email` | string | Yes      | Email address of the submitter |

#### Example curl

```bash
curl -X POST http://localhost:8081/api/v1/image \
  -F "image=@/path/to/your/image.jpg" \
  -F "email=user@example.com"
```

#### Example response

```json
{
  "id": "a3f1c2d4-89ab-4def-b012-3456789abcde"
}
```

HTTP status: `201 Created`  
`Location` header: `/api/v1/image/a3f1c2d4-89ab-4def-b012-3456789abcde`

---

## Items API

A basic CRUD API is also available at `/api/v1/items`. See the Swagger UI for full details.
