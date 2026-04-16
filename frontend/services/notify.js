'use strict';

let NotifyClient;
try {
  NotifyClient = require('notifications-node-client').NotifyClient;
} catch {
  console.log('⚠️  notifications-node-client not installed — run: npm install notifications-node-client');
}

const NOTIFY_API_KEY = process.env.GOVUK_NOTIFY_API_KEY;
const NOTIFY_TEMPLATE_ID = process.env.GOVUK_NOTIFY_TEMPLATE_ID;

let notifyClient = null;

if (!NotifyClient) {
  console.log('⚠️  GOV.UK Notify client unavailable — email notifications disabled');
} else if (NOTIFY_API_KEY) {
  notifyClient = new NotifyClient(NOTIFY_API_KEY);
  console.log('✅ GOV.UK Notify client initialised');
} else {
  console.log('⚠️  GOVUK_NOTIFY_API_KEY not set — email notifications disabled');
}

/**
 * Send a confirmation email via GOV.UK Notify.
 *
 * The template should contain these personalisation placeholders:
 *   - reference       — the submission reference ID
 *   - client_name     — extracted client name (e.g. "Mr Rajesh Patel")
 *   - submitted_at    — human-readable submission timestamp
 *
 * @param {string} emailAddress - recipient email address
 * @param {object} personalisation - template placeholder values
 * @returns {Promise<object|null>} Notify response or null if not configured
 */
async function sendConfirmationEmail(emailAddress, personalisation) {
  if (!notifyClient) {
    console.log('⚠️  Skipping email — GOV.UK Notify not configured');
    return null;
  }

  if (!NOTIFY_TEMPLATE_ID) {
    console.log('⚠️  Skipping email — GOVUK_NOTIFY_TEMPLATE_ID not set');
    return null;
  }

  try {
    const response = await notifyClient.sendEmail(NOTIFY_TEMPLATE_ID, emailAddress, {
      personalisation,
      reference: personalisation.reference,
    });
    console.log(`📧 Confirmation email sent to ${emailAddress} (Notify ID: ${response.data.id})`);
    return response.data;
  } catch (err) {
    const message = err.response?.data?.errors?.[0]?.message || err.message;
    console.error(`❌ Failed to send email via GOV.UK Notify: ${message}`);
    return null;
  }
}

module.exports = { sendConfirmationEmail };
