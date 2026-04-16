import { useParams, Link } from 'react-router-dom';
import { getSubmission, mapResponseToForm } from '../api/apiService';
import FormDetail from '../components/FormDetail';
import { useState, useEffect } from 'react';

export default function DetailPage() {
  const { id } = useParams();
  const [form, setForm] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      try {
        const response = await getSubmission(id);
        if (cancelled) return;
        const mapped = mapResponseToForm(response);
        mapped.status = 'Pending Review';
        setForm(mapped);
      } catch (err) {
        if (!cancelled) setError(err.message);
      } finally {
        if (!cancelled) setLoading(false);
      }
    }
    load();
    return () => { cancelled = true; };
  }, [id]);

  if (loading) {
    return (
      <div className="py-12 text-center">
        <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent" />
        <p className="mt-4 text-sm text-gray-500">Loading submission…</p>
      </div>
    );
  }

  if (error || !form) {
    return (
      <div className="py-12 text-center">
        <h2 className="text-lg font-semibold text-gray-900">Form not found</h2>
        <p className="mt-1 text-sm text-red-600">{error}</p>
        <Link to="/" className="mt-2 inline-block text-sm text-indigo-600 hover:underline">Back to dashboard</Link>
      </div>
    );
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
          <h1 className="text-2xl font-bold text-gray-900">{form.application_reference?.slice(0, 8)}…</h1>
          <p className="mt-1 text-sm text-gray-500">
            {form.first_name || '—'} {form.surname || '—'}
          </p>
        </div>
      </div>

      {/* Form detail card */}
      <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <FormDetail form={form} />
      </div>
    </div>
  );
}
