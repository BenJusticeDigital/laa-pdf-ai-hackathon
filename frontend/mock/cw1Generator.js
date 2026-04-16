'use strict';

const crypto = require('crypto');

// Realistic sample data pools for mock CW1 generation
const TITLES      = ['Mr', 'Mrs', 'Miss', 'Ms', 'Dr'];
const FIRST_NAMES = ['James', 'Sarah', 'Mohammed', 'Emma', 'David', 'Fatima', 'John', 'Priya'];
const SURNAMES    = ['Smith', 'Patel', 'Williams', 'Ahmed', 'Jones', 'Khan', 'Taylor', 'Singh'];
const TOWNS       = ['Birmingham', 'Manchester', 'Leeds', 'Bristol', 'Sheffield', 'Liverpool'];
const JOBS        = ['Teacher', 'Retail Assistant', 'Carer', 'Unemployed', 'Driver', 'Administrator'];
const STREETS     = ['14 High Street', '7 Church Road', '23 Oak Avenue', '2 Station Road'];
const POSTCODES   = ['B1 1AA', 'M1 2BB', 'LS1 3CC', 'BS1 4DD', 'S1 5EE'];
const ETHNICITIES = ['White British', 'Asian or Asian British - Pakistani', 'Black or Black British - Caribbean', 'Mixed - White and Black African'];
const MARITAL_STATUSES = ['Single', 'Married', 'Divorced', 'Widowed', 'Cohabiting'];
const SEXES       = ['Male', 'Female', 'Prefer not to say'];

function pick(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}

function randomDate(startYear, endYear) {
  const start = new Date(startYear, 0, 1);
  const end   = new Date(endYear, 11, 31);
  const d = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
  return d.toISOString().split('T')[0]; // YYYY-MM-DD
}

function randomNino() {
  const letters = 'ABCEGHJKLMNPRSTWXYZ';
  const l1 = letters[Math.floor(Math.random() * letters.length)];
  const l2 = letters[Math.floor(Math.random() * letters.length)];
  const nums = String(Math.floor(Math.random() * 900000) + 100000);
  const suffix = ['A', 'B', 'C', 'D'][Math.floor(Math.random() * 4)];
  return `${l1}${l2}${nums}${suffix}`;
}

function generateMockCw1() {
  const firstName = pick(FIRST_NAMES);
  const surname   = pick(SURNAMES);
  const idx       = Math.floor(Math.random() * POSTCODES.length);

  const id = crypto.randomUUID();

  const data = {
    id,
    application_reference:     id,

    // Exceptional case funding
    is_exceptional_case_funding: Math.random() > 0.8,

    // Equal opportunities
    ethnicity:                 pick(ETHNICITIES),
    disability_prefer_not_say: Math.random() > 0.7,

    // Client details
    title:                     pick(TITLES),
    initials:                  firstName[0],
    first_name:                firstName,
    surname,
    surname_at_birth:          Math.random() > 0.7 ? pick(SURNAMES) : null,
    date_of_birth:             randomDate(1950, 2000),
    national_insurance_number: randomNino(),
    sex:                       pick(SEXES),
    marital_status:            pick(MARITAL_STATUSES),
    place_of_birth_town:       pick(TOWNS),
    job:                       pick(JOBS),
    current_address:           pick(STREETS),
    postcode:                  POSTCODES[idx],
  };

  // Simulate OCR extraction failures — randomly blank out 2-4 fields
  const blankableFields = [
    'title', 'date_of_birth', 'national_insurance_number',
    'sex', 'marital_status', 'place_of_birth_town',
    'job', 'current_address', 'postcode', 'ethnicity',
  ];
  const shuffled = blankableFields.sort(() => Math.random() - 0.5);
  const blanksCount = 2 + Math.floor(Math.random() * 3); // 2 to 4
  for (let i = 0; i < blanksCount; i++) {
    data[shuffled[i]] = null;
  }

  return data;
}

module.exports = { generateMockCw1 };
