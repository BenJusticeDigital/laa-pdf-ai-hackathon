import StatusBadge from './StatusBadge';

function Section({ title, children }) {
  return (
    <div className="mb-6">
      <h3 className="mb-3 border-b border-gray-200 pb-2 text-sm font-semibold uppercase tracking-wider text-gray-500">{title}</h3>
      <dl className="grid grid-cols-1 gap-x-6 gap-y-3 sm:grid-cols-2">{children}</dl>
    </div>
  );
}

function Field({ label, value, missing }) {
  return (
    <div>
      <dt className="text-sm font-medium text-gray-500">{label}</dt>
      {missing ? (
        <dd className="mt-0.5 text-sm font-medium text-red-600">⚠ Not extracted</dd>
      ) : (
        <dd className="mt-0.5 text-sm text-gray-900">{value || '—'}</dd>
      )}
    </div>
  );
}

function DisabilityList({ form }) {
  const flags = [
    ['disability_mental_health', 'Mental health condition'],
    ['disability_blind', 'Blind'],
    ['disability_learning', 'Learning disability'],
    ['disability_long_standing_illness', 'Long-standing illness'],
    ['disability_mobility', 'Mobility impairment'],
    ['disability_deaf', 'Deaf'],
    ['disability_hearing_impaired', 'Hearing impaired'],
    ['disability_visually_impaired', 'Visually impaired'],
    ['disability_other', 'Other'],
    ['disability_unknown', 'Unknown'],
    ['disability_not_considered', 'Not considered'],
    ['disability_prefer_not_say', 'Prefer not to say'],
  ];

  const active = flags.filter(([key]) => form[key]).map(([, label]) => label);
  return <span>{active.length > 0 ? active.join(', ') : 'None declared'}</span>;
}

export default function FormDetail({ form }) {
  return (
    <div className="space-y-2">
      {/* Status bar */}
      <div className="flex flex-wrap items-center gap-3">
        <StatusBadge status={form.status} />
        {form.assigned_to && (
          <span className="text-sm text-gray-500">Assigned to: <span className="font-medium text-gray-700">{form.assigned_to}</span></span>
        )}
        {form.extraction_errors > 0 && (
          <span className="text-sm font-medium text-red-600">
            {form.extraction_errors} field{form.extraction_errors > 1 ? 's' : ''} could not be extracted
          </span>
        )}
      </div>

      <Section title="Application">
        <Field label="Reference" value={form.application_reference} />
        <Field label="Exceptional case funding" value={form.is_exceptional_case_funding ? 'Yes' : 'No'} />
        <Field label="Submitted" value={new Date(form.submitted_at).toLocaleString('en-GB', { dateStyle: 'medium', timeStyle: 'short' })} />
      </Section>

      <Section title="Client details">
        <Field label="Title" value={form.title} missing={form.title === null} />
        <Field label="First name" value={form.first_name} missing={form.first_name === null} />
        <Field label="Surname" value={form.surname} missing={form.surname === null} />
        <Field label="Surname at birth" value={form.surname_at_birth} />
        <Field label="Date of birth" value={form.date_of_birth} missing={form.date_of_birth === null} />
        <Field label="National Insurance number" value={form.national_insurance_number} missing={form.national_insurance_number === null} />
        <Field label="Sex" value={form.sex} missing={form.sex === null} />
        <Field label="Marital status" value={form.marital_status} missing={form.marital_status === null} />
        <Field label="Place of birth" value={form.place_of_birth_town} missing={form.place_of_birth_town === null} />
        <Field label="Occupation" value={form.job} missing={form.job === null} />
        <Field label="Address" value={[form.current_address, form.current_address_line2].filter(Boolean).join(', ')} missing={form.current_address === null} />
        <Field label="Postcode" value={form.postcode} missing={form.postcode === null} />
      </Section>

      <Section title="Equal opportunities">
        <Field label="Ethnicity" value={form.ethnicity} missing={form.ethnicity === null} />
        <div>
          <dt className="text-sm font-medium text-gray-500">Disability</dt>
          <dd className="mt-0.5 text-sm text-gray-900"><DisabilityList form={form} /></dd>
        </div>
      </Section>
    </div>
  );
}
