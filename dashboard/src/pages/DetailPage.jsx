import { useParams, Link, useNavigate } from 'react-router-dom';
import { getFormById, updateFormStatus } from '../data/mockData';
import FormDetail from '../components/FormDetail';
import { useState } from 'react';

export default function DetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState(() => getFormById(id));

  if (!form) {
    return (
      <div className="py-12 text-center">
        <h2 className="text-lg font-semibold text-gray-900">Form not found</h2>
        <Link to="/" className="mt-2 inline-block text-sm text-indigo-600 hover:underline">Back to dashboard</Link>
      </div>
    );
  }

  function handleAction(newStatus) {
    const updated = updateFormStatus(id, newStatus);
    setForm({ ...updated });
  }

  return (
    <div className="space-y-6">
      {/* Back link */}
      <Link to="/" className="inline-flex items-center gap-1 text-sm text-indigo-600 hover:text-indigo-900 hover:underline">
        <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
        </svg>
        Back to dashboard
      </Link>

      {/* Header */}
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{form.application_reference}</h1>
          <p className="mt-1 text-sm text-gray-500">
            {form.first_name || '—'} {form.surname || '—'}
          </p>
        </div>

        {/* Action buttons */}
        {form.status === 'Pending Review' && (
          <div className="flex flex-wrap gap-2">
            <button
              onClick={() => handleAction('Approved')}
              className="rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white shadow-sm transition-colors hover:bg-green-700 focus:ring-2 focus:ring-green-500 focus:ring-offset-2 focus:outline-none"
            >
              ✓ Approve
            </button>
            <button
              onClick={() => handleAction('Rejected')}
              className="rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white shadow-sm transition-colors hover:bg-red-700 focus:ring-2 focus:ring-red-500 focus:ring-offset-2 focus:outline-none"
            >
              ✗ Reject
            </button>
            <button
              onClick={() => handleAction('Info Requested')}
              className="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm transition-colors hover:bg-gray-50 focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 focus:outline-none"
            >
              ⓘ Request Info
            </button>
          </div>
        )}

        {form.status !== 'Pending Review' && (
          <button
            onClick={() => handleAction('Pending Review')}
            className="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm transition-colors hover:bg-gray-50 focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 focus:outline-none"
          >
            ↩ Reopen
          </button>
        )}
      </div>

      {/* Form detail card */}
      <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <FormDetail form={form} />
      </div>
    </div>
  );
}
