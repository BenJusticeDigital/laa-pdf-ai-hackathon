const TITLES = ['Mr', 'Mrs', 'Miss', 'Ms', 'Dr'];
const FIRST_NAMES = ['James', 'Sarah', 'Mohammed', 'Emma', 'David', 'Fatima', 'John', 'Priya', 'Oliver', 'Amina', 'Thomas', 'Zara', 'William', 'Aisha', 'George', 'Mei'];
const SURNAMES = ['Smith', 'Patel', 'Williams', 'Ahmed', 'Jones', 'Khan', 'Taylor', 'Singh', 'Brown', 'Ali', 'Wilson', 'Hussain', 'Davies', 'Begum', 'Evans', 'Sharma'];
const TOWNS = ['Birmingham', 'Manchester', 'Leeds', 'Bristol', 'Sheffield', 'Liverpool', 'London', 'Nottingham', 'Bradford', 'Cardiff'];
const JOBS = ['Teacher', 'Retail Assistant', 'Carer', 'Unemployed', 'Driver', 'Administrator', 'Nurse', 'Chef', 'Cleaner', 'Warehouse Operative'];
const STREETS = ['14 High Street', '7 Church Road', '23 Oak Avenue', '2 Station Road', '56 Victoria Lane', '11 Park Crescent', '8 Mill Lane', '31 King Street'];
const POSTCODES = ['B1 1AA', 'M1 2BB', 'LS1 3CC', 'BS1 4DD', 'S1 5EE', 'L1 6FF', 'SW1A 1AA', 'NG1 7GG', 'BD1 8HH', 'CF1 9JJ'];
const ETHNICITIES = ['White British', 'White Irish', 'White Other', 'Asian or Asian British - Pakistani', 'Asian or Asian British - Indian', 'Asian or Asian British - Bangladeshi', 'Black or Black British - Caribbean', 'Black or Black British - African', 'Mixed - White and Black Caribbean', 'Mixed - White and Asian', 'Chinese', 'Any other ethnic group', 'Prefer not to say'];
const MARITAL_STATUSES = ['Single', 'Married', 'Divorced', 'Widowed', 'Cohabiting', 'Separated'];
const SEXES = ['Male', 'Female', 'Prefer not to say'];
const STATUSES = ['Pending Review', 'Pending Review', 'Pending Review', 'Approved', 'Rejected', 'Info Requested'];
const CASEWORKERS = ['Sarah Johnson', 'Raj Patel', 'Emily Chen', 'Michael O\'Brien', null, null];

function pick(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}

function randomDate(startYear, endYear) {
  const start = new Date(startYear, 0, 1);
  const end = new Date(endYear, 11, 31);
  const d = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
  return d.toISOString().split('T')[0];
}

function randomNino() {
  const letters = 'ABCEGHJKLMNPRSTWXYZ';
  const l1 = letters[Math.floor(Math.random() * letters.length)];
  const l2 = letters[Math.floor(Math.random() * letters.length)];
  const nums = String(Math.floor(Math.random() * 900000) + 100000);
  const suffix = ['A', 'B', 'C', 'D'][Math.floor(Math.random() * 4)];
  return `${l1}${l2} ${nums.slice(0, 2)} ${nums.slice(2, 4)} ${nums.slice(4, 6)} ${suffix}`;
}

function randomSubmittedDate() {
  const now = Date.now();
  const daysAgo = Math.floor(Math.random() * 30);
  const d = new Date(now - daysAgo * 86400000);
  return d.toISOString();
}

function generateForm(seed) {
  const firstName = pick(FIRST_NAMES);
  const surname = pick(SURNAMES);
  const status = pick(STATUSES);

  const blankableFields = ['title', 'date_of_birth', 'national_insurance_number', 'sex', 'marital_status', 'place_of_birth_town', 'job', 'current_address', 'postcode', 'ethnicity'];

  const form = {
    id: crypto.randomUUID(),
    application_reference: `LAA-${(Date.now() + seed).toString(36).toUpperCase()}`,
    status,
    assigned_to: status === 'Pending Review' ? null : pick(CASEWORKERS),
    submitted_at: randomSubmittedDate(),

    is_exceptional_case_funding: Math.random() > 0.85,

    ethnicity: pick(ETHNICITIES),
    disability_prefer_not_say: Math.random() > 0.7,
    disability_mental_health: Math.random() > 0.9,
    disability_blind: false,
    disability_learning: Math.random() > 0.9,
    disability_long_standing_illness: Math.random() > 0.85,
    disability_mobility: Math.random() > 0.9,
    disability_other: false,
    disability_deaf: false,
    disability_hearing_impaired: false,
    disability_visually_impaired: false,
    disability_unknown: false,
    disability_not_considered: false,

    title: pick(TITLES),
    initials: firstName[0],
    first_name: firstName,
    surname,
    surname_at_birth: Math.random() > 0.7 ? pick(SURNAMES) : null,
    date_of_birth: randomDate(1950, 2000),
    national_insurance_number: randomNino(),
    sex: pick(SEXES),
    marital_status: pick(MARITAL_STATUSES),
    place_of_birth_town: pick(TOWNS),
    job: pick(JOBS),
    current_address: pick(STREETS),
    current_address_line2: null,
    postcode: pick(POSTCODES),
  };

  // Simulate OCR failures on some pending forms
  if (status === 'Pending Review' && Math.random() > 0.5) {
    const shuffled = [...blankableFields].sort(() => Math.random() - 0.5);
    const count = 1 + Math.floor(Math.random() * 3);
    for (let i = 0; i < count; i++) {
      form[shuffled[i]] = null;
    }
    form.extraction_errors = count;
  } else {
    form.extraction_errors = 0;
  }

  return form;
}

// Generate a fixed set of mock forms (seeded so they're stable per session)
export const mockForms = Array.from({ length: 22 }, (_, i) => generateForm(i * 1000));

export function getFormById(id) {
  return mockForms.find((f) => f.id === id) || null;
}

export function updateFormStatus(id, newStatus) {
  const form = mockForms.find((f) => f.id === id);
  if (form) {
    form.status = newStatus;
    if (newStatus !== 'Pending Review') {
      form.assigned_to = form.assigned_to || 'Current User';
    }
  }
  return form;
}
