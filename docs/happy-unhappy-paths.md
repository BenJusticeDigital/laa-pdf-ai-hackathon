# Happy and unhappy paths

## Happy path

The provider uploads a clear photo of a well-filled form. Gemini extracts all fields with high confidence. The check your answers screen shows everything populated correctly. The provider confirms without needing to make any edits. The simulated submission succeeds. In Phase 3, Notify fires successfully and the caseworker receives the email with the deep link (and eventually the PDF).

## Unhappy paths

### At upload

The image is blurry, too dark, or at an angle. Gemini still returns JSON but with missing or garbled values. The provider has to correct many fields manually — the tool still works, but the AI adds less value.

Worse: the image is so poor that Gemini returns garbage or errors out entirely. This needs to be handled gracefully — probably an error page with a "try again with a clearer image" message.

### At extraction

Gemini times out or returns malformed JSON that the backend can't parse. The backend needs to catch this and return a meaningful error rather than a 500.

### At the review screen (Phase 2)

A field is flagged as low confidence but the provider doesn't notice or ignores the warning and confirms anyway. The data submitted is wrong. This is partly a UI design problem — the flagging needs to be prominent enough to catch attention without being so alarming it slows down every submission.

### At submission

The simulated LAA API call fails. The provider has already confirmed the data — you shouldn't lose it. Worth thinking about whether you hold the structured data in session or backend state so they can retry.

### At notification (Phase 3)

The Notify API is down or rate-limited. The submission has already gone through, so the caseworker just doesn't get the email. This failure should be logged clearly rather than silently swallowed. Similarly, if PDF generation fails, the fallback should be to send the email without the attachment rather than failing the whole notification.

### Data quality edge cases

The handwriting is ambiguous — e.g. a "1" that looks like a "7" in a date of birth, or an NI number where a letter is unclear. Gemini may return a plausible but wrong value with high confidence. This is the hardest unhappy path because the tool won't flag it — the provider is the last line of defence, which is why the check your answers pattern matters so much.
