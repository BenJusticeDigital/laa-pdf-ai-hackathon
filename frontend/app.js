'use strict';

const express = require('express');
const nunjucks = require('nunjucks');
const path = require('path');
const uploadRouter = require('./routes/upload');

const app = express();
const PORT = process.env.PORT || 3000;

// Nunjucks templating
nunjucks.configure(
  [
    path.join(__dirname, 'views'),
    path.join(__dirname, 'node_modules/govuk-frontend/dist'),
  ],
  {
    autoescape: true,
    express: app,
  }
);
app.set('view engine', 'njk');

// Serve govuk-frontend static assets
app.use(
  '/assets',
  express.static(
    path.join(__dirname, 'node_modules/govuk-frontend/dist/govuk/assets')
  )
);
app.use(
  '/govuk-frontend.js',
  express.static(
    path.join(__dirname, 'node_modules/govuk-frontend/dist/govuk/govuk-frontend.min.js')
  )
);
app.use(
  '/govuk-frontend.css',
  express.static(
    path.join(__dirname, 'node_modules/govuk-frontend/dist/govuk/govuk-frontend.min.css')
  )
);

app.use(express.urlencoded({ extended: true }));

app.use('/', uploadRouter);

app.listen(PORT, () => {
  console.log(`Frontend running at http://localhost:${PORT}`);
});

module.exports = app;
