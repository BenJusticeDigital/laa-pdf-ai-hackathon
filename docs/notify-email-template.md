# GOV.UK Notify Email Template

Use this when creating the template at https://www.notifications.service.gov.uk

## Template name

`CW1 Form Submission Confirmation`

## Subject line

```
CW1 form submitted — ((reference))
```

## Message body

Copy and paste the text below into the GOV.UK Notify template editor:

---

```
Dear colleague,

A CW1 Legal Help form has been submitted for processing.

# Submission details

^ Reference: ((reference))

Client name: ((client_name))
Submitted at: ((submitted_at))

---

# What happens next

The extracted data has been reviewed and confirmed by a caseworker. The form is now ready for processing.

If you need to query this submission, quote the reference above.

---

This is an automated email from the LAA CW1 Form Submission service. Do not reply to this email.
```

---

## Personalisation placeholders

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `((reference))` | Application reference or submission ID | `LAA-2025-CW1-00472` |
| `((client_name))` | Client's full name from the form | `Mr Rajesh Patel` |
| `((submitted_at))` | When the caseworker confirmed the form | `16 April 2026 at 11:30` |

## Formatting notes

- `# Heading` renders as a heading in the email
- `^ text` renders as an inset/highlight block (useful for the reference)
- `---` renders as a horizontal rule
- See [GOV.UK Notify formatting guide](https://www.notifications.service.gov.uk/using-notify/formatting) for full syntax

## Setup steps

1. Sign in to [GOV.UK Notify](https://www.notifications.service.gov.uk/sign-in)
2. Go to **Templates** → **Add template** → **Email**
3. Set the template name and subject line as above
4. Paste the message body
5. Copy the **template ID** and set it as `GOVUK_NOTIFY_TEMPLATE_ID` in your `.env`
6. Go to **API integration** → copy your API key → set as `GOVUK_NOTIFY_API_KEY`
