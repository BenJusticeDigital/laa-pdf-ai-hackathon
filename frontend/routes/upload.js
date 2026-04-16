'use strict';

const express = require('express');
const fileUpload = require('express-fileupload');
const axios = require('axios');
const FormData = require('form-data');
const crypto = require('crypto');

const router = express.Router();
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:8081';
const USE_MOCK = process.env.USE_MOCK === 'true';

if (USE_MOCK) {
  console.log('⚠️  Mock mode enabled — backend API calls will be simulated');
}

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

  // Mock mode — skip the backend call and return a fake ID
  if (USE_MOCK) {
    return res.redirect(`/success?id=${crypto.randomUUID()}&mock=true`);
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

// GET /success — confirmation page
router.get('/success', (req, res) => {
  const { id, mock } = req.query;
  if (!id) return res.redirect('/');
  res.render('success.njk', { id, mockMode: mock === 'true' });
});

module.exports = router;
