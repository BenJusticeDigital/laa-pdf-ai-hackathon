'use strict';

const express = require('express');
const fileUpload = require('express-fileupload');
const axios = require('axios');
const FormData = require('form-data');
const { generateMockCw1 } = require('../mock/cw1Generator');
const { sendConfirmationEmail } = require('../services/notify');

const router = express.Router();
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:8081';
const USE_MOCK = process.env.USE_MOCK === 'true';

if (USE_MOCK) {
  console.log('⚠️  Mock mode enabled — backend API calls will be simulated');
}

// Short-lived in-memory store for extracted CW1 data (keyed by submission ID).
// Sufficient for a PoC demo session.
const reviewStore = new Map();

router.use(fileUpload());

// GET /  — upload form
router.get('/', (req, res) => {
  res.render('upload.njk', {
    errors: null,
    values: {},
    mockMode: USE_MOCK,
  });
});

// POST / — handle form submission
router.post('/', async (req, res) => {
  const { email } = req.body;
  const imageFile = req.files && req.files.image;

  // Validation
  const errors = [];
  if (!imageFile || imageFile.size === 0) {
    errors.push({ field: 'image', text: 'Select an image file to upload' });
  }
  if (!email || !email.trim()) {
    errors.push({ field: 'email', text: 'Enter your email address' });
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    errors.push({ field: 'email', text: 'Enter an email address in the correct format, like name@example.com' });
  }

  if (errors.length > 0) {
    return res.render('upload.njk', {
      errors,
      values: { email },
      mockMode: USE_MOCK,
    });
  }

  // Mock mode — generate a fake CW1 extraction, show processing animation
  if (USE_MOCK) {
    const mockData = generateMockCw1();
    mockData._submitterEmail = email.trim();
    reviewStore.set(mockData.id, mockData);
    return res.render('processing.njk', {
      redirectUrl: `/review/${mockData.id}?mock=true`,
      mockMode: true,
    });
  }

  try {
    const form = new FormData();
    form.append('image', imageFile.data, {
      filename: imageFile.name,
      contentType: imageFile.mimetype,
    });
    form.append('email', email.trim());

    const response = await axios.post(`${BACKEND_URL}/api/v1/image`, form, {
      headers: form.getHeaders(),
    });

    return res.redirect(`/success?id=${response.data.id}`);
  } catch (err) {
    const status = err.response ? err.response.status : null;
    const message =
      status === 400
        ? 'The selected file could not be processed. Check the file and try again.'
        : 'There was a problem uploading your image. Try again later.';

    return res.render('upload.njk', {
      errors: [{ field: null, text: message }],
      values: { email },
      mockMode: USE_MOCK,
    });
  }
});

// Fields that the OCR is expected to extract — used to detect gaps.
const TRACKED_FIELDS = [
  { key: 'title',                    label: 'Title',                    section: 'client-details' },
  { key: 'first_name',               label: 'First name',              section: 'client-details' },
  { key: 'surname',                  label: 'Surname',                 section: 'client-details' },
  { key: 'date_of_birth',            label: 'Date of birth',           section: 'client-details' },
  { key: 'national_insurance_number', label: 'National Insurance number', section: 'client-details' },
  { key: 'sex',                      label: 'Sex',                     section: 'client-details' },
  { key: 'marital_status',           label: 'Marital status',          section: 'client-details' },
  { key: 'place_of_birth_town',      label: 'Place of birth (town)',   section: 'client-details' },
  { key: 'job',                      label: 'Occupation',              section: 'client-details' },
  { key: 'current_address',          label: 'Current address',         section: 'client-details' },
  { key: 'postcode',                 label: 'Postcode',                section: 'client-details' },
  { key: 'ethnicity',                label: 'Ethnicity',               section: 'equal-opportunities' },
];

// Returns an object keyed by missing field names (for easy lookup in Nunjucks templates).
function getMissingKeys(data) {
  const obj = {};
  TRACKED_FIELDS.forEach(f => { if (!data[f.key]) obj[f.key] = true; });
  return obj;
}

// Returns the missingFields array for a given section, suitable for govukErrorSummary.
function getMissingFieldsForSection(data, section) {
  return TRACKED_FIELDS
    .filter(f => f.section === section && !data[f.key])
    .map(f => ({
      text: `${f.label} could not be extracted — enter this information`,
      href: `#${f.key}`,
    }));
}

// GET /review/:id — check your answers page
router.get('/review/:id', (req, res) => {
  const data = reviewStore.get(req.params.id);
  if (!data) return res.redirect('/');

  const mockMode = req.query.mock === 'true';

  // Detect fields the OCR couldn't extract (null or empty string)
  const missingFields = TRACKED_FIELDS
    .filter(f => !data[f.key])
    .map(f => ({
      label: f.label,
      section: f.section,
      text: `${f.label} could not be extracted from the form`,
      href: `/edit/${data.id}/${f.section}`,
    }));

  res.render('review.njk', { data, mockMode, missingFields });
});

// --- Edit routes ---

router.get('/edit/:id/application', (req, res) => {
  const data = reviewStore.get(req.params.id);
  if (!data) return res.redirect('/');
  const missing = getMissingKeys(data);
  const errorList = getMissingFieldsForSection(data, 'application');
  res.render('edit-application.njk', { data, missing, errorList });
});

router.post('/edit/:id/application', express.urlencoded({ extended: true }), (req, res) => {
  const data = reviewStore.get(req.params.id);
  if (!data) return res.redirect('/');

  data.application_reference     = req.body.application_reference || data.application_reference;
  data.is_exceptional_case_funding = req.body.is_exceptional_case_funding === 'true';

  res.redirect(`/review/${req.params.id}?mock=true`);
});

router.get('/edit/:id/client-details', (req, res) => {
  const data = reviewStore.get(req.params.id);
  if (!data) return res.redirect('/');
  const missing = getMissingKeys(data);
  const errorList = getMissingFieldsForSection(data, 'client-details');
  res.render('edit-client-details.njk', { data, missing, errorList });
});

router.post('/edit/:id/client-details', express.urlencoded({ extended: true }), (req, res) => {
  const data = reviewStore.get(req.params.id);
  if (!data) return res.redirect('/');

  Object.assign(data, {
    title:                    req.body.title,
    first_name:               req.body.first_name,
    surname:                  req.body.surname,
    surname_at_birth:         req.body.surname_at_birth || null,
    date_of_birth:            req.body.date_of_birth,
    national_insurance_number: req.body.national_insurance_number,
    sex:                      req.body.sex,
    marital_status:           req.body.marital_status,
    place_of_birth_town:      req.body.place_of_birth_town,
    job:                      req.body.job,
    current_address:          req.body.current_address,
    postcode:                 req.body.postcode,
  });

  res.redirect(`/review/${req.params.id}?mock=true`);
});

router.get('/edit/:id/equal-opportunities', (req, res) => {
  const data = reviewStore.get(req.params.id);
  if (!data) return res.redirect('/');
  const missing = getMissingKeys(data);
  const errorList = getMissingFieldsForSection(data, 'equal-opportunities');
  res.render('edit-equal-opportunities.njk', { data, missing, errorList });
});

router.post('/edit/:id/equal-opportunities', express.urlencoded({ extended: true }), (req, res) => {
  const data = reviewStore.get(req.params.id);
  if (!data) return res.redirect('/');

  // Reset all disability flags then set only checked ones
  const disabilityFields = [
    'disability_not_considered', 'disability_mental_health', 'disability_blind',
    'disability_learning', 'disability_long_standing_illness', 'disability_mobility',
    'disability_other', 'disability_deaf', 'disability_unknown',
    'disability_hearing_impaired', 'disability_prefer_not_say', 'disability_visually_impaired',
  ];
  disabilityFields.forEach(f => { data[f] = false; });

  const checked = Array.isArray(req.body.disability) ? req.body.disability : (req.body.disability ? [req.body.disability] : []);
  checked.forEach(f => { if (disabilityFields.includes(f)) data[f] = true; });

  data.ethnicity = req.body.ethnicity;

  res.redirect(`/review/${req.params.id}?mock=true`);
});

// GET /success — confirmation page (real mode)
router.get('/success', (req, res) => {
  const { id } = req.query;
  if (!id) return res.redirect('/');
  res.render('success.njk', { id, mockMode: false });
});

// POST /confirm — final submission from review page
router.post('/confirm', async (req, res) => {
  const { id } = req.body;
  if (!id) return res.redirect('/');

  const data = reviewStore.get(id);
  const email = data?._submitterEmail;

  if (email && data) {
    const clientName = [data.title, data.first_name, data.surname].filter(Boolean).join(' ') || 'Unknown';
    await sendConfirmationEmail(email, {
      reference: data.application_reference || id,
      client_name: clientName,
      submitted_at: new Date().toLocaleString('en-GB', { dateStyle: 'long', timeStyle: 'short' }),
    });
  }

  reviewStore.delete(id);
  res.render('success.njk', { id, mockMode: USE_MOCK, emailSent: !!email });
});

module.exports = router;
