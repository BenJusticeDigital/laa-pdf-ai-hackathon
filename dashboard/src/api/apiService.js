const API_BASE = '/api/v1';

/**
 * Fetch all image submissions (summary: id, email, submittedAt).
 */
export async function listSubmissions() {
  const res = await fetch(`${API_BASE}/image`);
  if (!res.ok) throw new Error(`Failed to fetch submissions: ${res.status}`);
  return res.json();
}

/**
 * Fetch a single submission's full details including extracted form data.
 */
export async function getSubmission(id) {
  const res = await fetch(`${API_BASE}/image/${id}`);
  if (!res.ok) throw new Error(`Failed to fetch submission ${id}: ${res.status}`);
  return res.json();
}

// Maps camelCase backend keys to snake_case dashboard keys
const FIELD_MAP = {
  applicationReference: 'application_reference',
  isExceptionalCaseFunding: 'is_exceptional_case_funding',
  ethnicity: 'ethnicity',
  disabilityNotConsidered: 'disability_not_considered',
  disabilityMentalHealth: 'disability_mental_health',
  disabilityBlind: 'disability_blind',
  disabilityLearning: 'disability_learning',
  disabilityLongStandingIllness: 'disability_long_standing_illness',
  disabilityMobility: 'disability_mobility',
  disabilityOther: 'disability_other',
  disabilityDeaf: 'disability_deaf',
  disabilityUnknown: 'disability_unknown',
  disabilityHearingImpaired: 'disability_hearing_impaired',
  disabilityPreferNotSay: 'disability_prefer_not_say',
  disabilityVisuallyImpaired: 'disability_visually_impaired',
  title: 'title',
  initials: 'initials',
  surname: 'surname',
  firstName: 'first_name',
  surnameAtBirth: 'surname_at_birth',
  dateOfBirth: 'date_of_birth',
  nationalInsuranceNumber: 'national_insurance_number',
  sex: 'sex',
  maritalStatus: 'marital_status',
  placeOfBirthTown: 'place_of_birth_town',
  job: 'job',
  currentAddress: 'current_address',
  currentAddressLine2: 'current_address_line2',
  postcode: 'postcode',
};

const TRACKED_FIELDS = [
  'title', 'first_name', 'surname', 'date_of_birth',
  'national_insurance_number', 'sex', 'marital_status',
  'place_of_birth_town', 'job', 'current_address', 'postcode', 'ethnicity',
];

/**
 * Convert backend ImageResponse into the flat snake_case format used by
 * the dashboard components, including extraction_errors count.
 */
export function mapResponseToForm(response) {
  const form = { id: response.id, application_reference: response.id };
  const extracted = response.extractedData || {};

  for (const [camel, snake] of Object.entries(FIELD_MAP)) {
    const value = extracted[camel];
    form[snake] = (value !== undefined && value !== null && value !== '' && value !== 'null') ? value : null;
  }

  // Use backend id as reference
  form.application_reference = response.id;

  // Count missing tracked fields
  form.extraction_errors = TRACKED_FIELDS.filter((k) => !form[k]).length;

  return form;
}

/**
 * Convert backend ImageSummary into a lightweight row for the table.
 */
export function mapSummaryToRow(summary) {
  return {
    id: summary.id,
    application_reference: summary.id,
    email: summary.email,
    submitted_at: summary.submittedAt,
    status: 'Pending Review',
    extraction_errors: null, // unknown until detail is fetched
  };
}
