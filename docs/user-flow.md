# CW1 Form Submission — User Flow Walkthrough

This page walks through the end-to-end journey for digitising a CW1 Legal Help form, from the provider uploading an image through to the LAA caseworker reviewing the extracted data.

For a technical overview of the service architecture, see the [service flow diagram](cw1_digital_service_flow_v2.svg).

---

## Step 1 — Upload form image

![Upload form image screen](screenshots/01%20Screenshot%202026-04-16%20at%2015.31.24.png)

The provider visits the GOV.UK-branded "CW1 Form Submission" service and is presented with an upload page.

They:
1. Select a photo or scan of CW1 page 1 (accepted formats: JPG, PNG, PDF, TIFF)
2. Enter their email address (used to identify the submission and send a confirmation)
3. Click **Upload and extract data**

The page includes a transparency notice explaining that the AI reads text and checkbox data from the image, the extracted data is shown for review and correction before anything is submitted, no AI decisions are made, and documents are processed in the UK and not used to train models.

---

## Step 2 — Processing

![Processing your form loading screen](screenshots/02%20Screenshot%202026-04-16%20at%2015.31.49.png)

After upload, the provider sees a loading screen: **"Processing your form — Our AI is reading your document"**.

A progress bar shows percentage completion alongside a status message describing the current extraction stage (e.g. "Cross-referencing form fields…"). In the background, the Spring Boot backend is sending the image to Google Gemini with a structured JSON schema prompt to extract all CW1 page 1 fields.

---

## Step 3a — Happy path: Check your answers

Once Gemini returns a high-confidence extraction with no missing fields, the provider is taken directly to a **"Check your answers"** review screen (GOV.UK review pattern). All extracted fields are pre-populated and grouped by section. The provider can use **Change** links to correct individual values before confirming the submission.

---

## Step 3b — Unhappy path: Extraction errors

![Check your answers page showing extraction failures](screenshots/03%20Screenshot%202026-04-16%20at%2015.32.22.png)

If Gemini cannot read one or more fields (e.g. due to a blurry image, poor handwriting, or a folded corner obscuring the form), a red error banner appears at the top of the review screen:

> **Some information could not be extracted**  
> The OCR could not read the following fields. Please provide the missing information.

In this example, six fields failed extraction:
- Surname
- Date of birth
- National Insurance number
- Place of birth (town)
- Occupation
- Current address

Fields that were successfully extracted are shown below with **Change** links. The provider must manually complete the missing fields before the form can be submitted. This mirrors the GOV.UK error summary pattern and keeps the provider in control of the data at all times.

---

## Step 4 — Submission confirmation

![Image submitted confirmation page](screenshots/04%20Screenshot%202026-04-16%20at%2015.32.45.png)

Once the provider has reviewed and confirmed all fields, they submit the form. A GOV.UK green confirmation banner is displayed:

> **Image submitted**  
> Submission reference: `682ad5ce-ac1c-4afb-b8ee-2232297c6090`

The page confirms the scanned form image has been received and is being processed, and that a confirmation email has been sent. A link is provided to submit another image.

---

## Step 5 — Provider confirmation email

![Provider confirmation email](screenshots/05%20Screenshot%202026-04-16%20at%2015.32.59.png)

A GOV.UK Notify email is sent to the provider's email address. It includes:

- **Submission reference** — unique UUID for this submission
- **Client name** — as extracted from the form (e.g. Mr Ben Test)
- **Submission timestamp** — date and time of submission

The email explains what happens next: the extracted data will be reviewed and confirmed by a caseworker, after which the form is ready for processing. The provider is instructed to quote the reference number for any queries.

---

## Step 6 — Caseworker dashboard

![LAA CW1 Caseworker Dashboard showing submitted forms](screenshots/06%20Screenshot%202026-04-16%20at%2015.33.17.png)

LAA caseworkers access an internal dashboard to review incoming submissions. The dashboard shows:

- **Summary counts** across all submissions — Total, Pending, Approved, Rejected, and Extraction errors
- A **search bar** to find submissions by name, reference, or NI number
- A **status filter** dropdown
- A **table** listing each submission with: reference, client name, email, NI number, status badge, submission date, and error count

Submissions with extraction errors are visible at a glance, allowing caseworkers to prioritise forms that need manual attention.

---

## Step 7 — Caseworker detail view

![LAA CW1 Caseworker Dashboard showing individual submission details](screenshots/07%20Screenshot%202026-04-16%20at%2015.33.26.png)

Clicking a submission opens the individual detail view. The page shows:

- **Status badge** — "Pending Review" (amber), with an alert indicating how many fields could not be extracted (e.g. "5 fields could not be extracted")
- **Application section** — submission reference, exceptional case funding flag, and submission timestamp
- **Client details section** — all CW1 page 1 client fields displayed in a two-column layout. Fields that could not be extracted are highlighted with a red warning icon (e.g. Place of birth, Address, NI number, Occupation, Postcode)
- **Equal Opportunities section** — ethnicity and disability values

The caseworker can review the extracted data, identify fields requiring correction, and action the submission accordingly.
