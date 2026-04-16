-- CW1 Form: Legal Help, Help at Court and Family Help (Lower)
CREATE TABLE CW1_APPLICATIONS
(
    id                              UUID            DEFAULT gen_random_uuid(),
    application_reference           VARCHAR(50)     UNIQUE,

    -- Exceptional Case Funding
    is_exceptional_case_funding     BOOLEAN         DEFAULT FALSE,

    -- Equal Opportunities Monitoring - Ethnicity
    ethnicity                       VARCHAR(50),

    -- Equal Opportunities Monitoring - Disability
    disability_not_considered       BOOLEAN         DEFAULT FALSE,
    disability_mental_health        BOOLEAN         DEFAULT FALSE,
    disability_blind                BOOLEAN         DEFAULT FALSE,
    disability_learning             BOOLEAN         DEFAULT FALSE,
    disability_long_standing_illness BOOLEAN        DEFAULT FALSE,
    disability_mobility             BOOLEAN         DEFAULT FALSE,
    disability_other                BOOLEAN         DEFAULT FALSE,
    disability_deaf                 BOOLEAN         DEFAULT FALSE,
    disability_unknown              BOOLEAN         DEFAULT FALSE,
    disability_hearing_impaired     BOOLEAN         DEFAULT FALSE,
    disability_prefer_not_say       BOOLEAN         DEFAULT FALSE,
    disability_visually_impaired    BOOLEAN         DEFAULT FALSE,

    -- Client Details
    title                           VARCHAR(20),
    initials                        VARCHAR(10),
    surname                         VARCHAR(100)    NOT NULL,
    first_name                      VARCHAR(100)    NOT NULL,
    surname_at_birth                VARCHAR(100),
    date_of_birth                   DATE            NOT NULL,
    national_insurance_number       VARCHAR(9),
    sex                             VARCHAR(20),
    marital_status                  VARCHAR(30),
    place_of_birth_town             VARCHAR(100),
    job                             VARCHAR(100),
    current_address                 VARCHAR(200),
    current_address_line2           VARCHAR(200),
    postcode                        VARCHAR(10),

    -- Audit fields
    created_at                      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at                      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);

-- Trigger to auto-update updated_at on row changes
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cw1_applications_updated_at
    BEFORE UPDATE ON CW1_APPLICATIONS
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Indexes for quick lookups
CREATE INDEX idx_cw1_surname ON CW1_APPLICATIONS(surname);
CREATE INDEX idx_cw1_dob ON CW1_APPLICATIONS(date_of_birth);
CREATE INDEX idx_cw1_ni_number ON CW1_APPLICATIONS(national_insurance_number);
CREATE INDEX idx_cw1_reference ON CW1_APPLICATIONS(application_reference);