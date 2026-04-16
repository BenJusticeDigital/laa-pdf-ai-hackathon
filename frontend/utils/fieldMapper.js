'use strict';

/**
 * Maps the backend's camelCase Cw1FormData keys to the snake_case keys
 * used by the frontend review/edit templates.
 */
const FIELD_MAP = {
  applicationReference:         'application_reference',
  isExceptionalCaseFunding:     'is_exceptional_case_funding',
  ethnicity:                    'ethnicity',
  disabilityNotConsidered:      'disability_not_considered',
  disabilityMentalHealth:       'disability_mental_health',
  disabilityBlind:              'disability_blind',
  disabilityLearning:           'disability_learning',
  disabilityLongStandingIllness:'disability_long_standing_illness',
  disabilityMobility:           'disability_mobility',
  disabilityOther:              'disability_other',
  disabilityDeaf:               'disability_deaf',
  disabilityUnknown:            'disability_unknown',
  disabilityHearingImpaired:    'disability_hearing_impaired',
  disabilityPreferNotSay:       'disability_prefer_not_say',
  disabilityVisuallyImpaired:   'disability_visually_impaired',
  title:                        'title',
  initials:                     'initials',
  surname:                      'surname',
  firstName:                    'first_name',
  surnameAtBirth:               'surname_at_birth',
  dateOfBirth:                  'date_of_birth',
  nationalInsuranceNumber:      'national_insurance_number',
  sex:                          'sex',
  maritalStatus:                'marital_status',
  placeOfBirthTown:             'place_of_birth_town',
  job:                          'job',
  currentAddress:               'current_address',
  currentAddressLine2:          'current_address_line2',
  postcode:                     'postcode',
};

/**
 * Convert the backend's camelCase extractedData into the snake_case
 * flat object expected by the frontend templates.
 *
 * @param {object} extractedData - The extractedData map from the API response
 * @returns {object} Flat object with snake_case keys
 */
function mapExtractedData(extractedData) {
  const result = {};
  for (const [camel, snake] of Object.entries(FIELD_MAP)) {
    if (camel in extractedData) {
      result[snake] = extractedData[camel];
    }
  }
  return result;
}

module.exports = { mapExtractedData, FIELD_MAP };
