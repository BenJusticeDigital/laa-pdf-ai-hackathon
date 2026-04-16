import { useState, useMemo, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { listSubmissions, getSubmission, mapResponseToForm, mapSummaryToRow } from '../api/apiService';
import StatusBadge from '../components/StatusBadge';
import SearchBar from '../components/SearchBar';

export default function DashboardPage() {
  const [forms, setForms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [query, setQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('All');

  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        // Fetch summary list
        const summaries = await listSubmissions();
        if (cancelled) return;

        // Show rows immediately with summary data
        const rows = summaries.map(mapSummaryToRow);
        setForms(rows);
        setLoading(false);

        // Fetch full details in parallel to get extracted fields + error counts
        const details = await Promise.allSettled(
          summaries.map((s) => getSubmission(s.id))
        );
        if (cancelled) return;

        const enriched = summaries.map((s, i) => {
          if (details[i].status === 'fulfilled') {
            const form = mapResponseToForm(details[i].value);
            form.email = s.email;
            form.submitted_at = s.submittedAt;
            form.status = 'Pending Review';
            return form;
          }
          return mapSummaryToRow(s);
        });

        setForms(enriched);
      } catch (err) {
        if (!cancelled) {
          setError(err.message);
          setLoading(false);
        }
      }
    }

    load();
    return () => { cancelled = true; };
  }, []);

  const filtered = useMemo(() => {
    const q = query.toLowerCase().trim();
    return forms
      .filter((f) => {
        if (statusFilter !== 'All' && f.status !== statusFilter) return false;
        if (!q) return true;
        return (
          f.application_reference?.toLowerCase().includes(q) ||
          f.first_name?.toLowerCase().includes(q) ||
          f.surname?.toLowerCase().includes(q) ||
          f.email?.toLowerCase().includes(q) ||
          f.national_insurance_number?.toLowerCase().includes(q) ||
          `${f.first_name || ''} ${f.surname || ''}`.toLowerCase().includes(q)
        );
      })
      .sort((a, b) => new Date(b.submitted_at) - new Date(a.submitted_at));
  }, [query, statusFilter, forms]);

  const counts = useMemo(() => {
    const c = { total: forms.length, pending: 0, approved: 0, rejected: 0, info: 0, errors: 0 };
    forms.forEach((f) => {
      if (f.status === 'Pending Review') c.pending++;
      else if (f.status === 'Approved') c.approved++;
      else if (f.status === 'Rejected') c.rejected++;
      else if (f.status === 'Info Requested') c.info++;
      if (f.extraction_errors > 0) c.errors++;
    });
    return c;
  }, [forms]);

  if (loading) {
    return (
      <div className="py-12 text-center">
        <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent" />
        <p className="mt-4 text-sm text-gray-500">Loading submissions…</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 p-6 text-center">
        <p className="text-sm font-medium text-red-800">Failed to load submissions</p>
        <p className="mt-1 text-sm text-red-600">{error}</p>
        <button onClick={() => window.location.reload()} className="mt-3 text-sm text-indigo-600 hover:underline">
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Submitted Forms</h1>
        <p className="mt-1 text-sm text-gray-500">Review and action CW1 Legal Help form submissions.</p>
      </div>

      {/* Summary cards */}
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-5">
        <SummaryCard label="Total" value={counts.total} color="gray" />
        <SummaryCard label="Pending" value={counts.pending} color="yellow" />
        <SummaryCard label="Approved" value={counts.approved} color="green" />
        <SummaryCard label="Rejected" value={counts.rejected} color="red" />
        <SummaryCard label="Extraction errors" value={counts.errors} color="orange" />
      </div>

      {/* Search & filter */}
      <SearchBar query={query} onQueryChange={setQuery} statusFilter={statusFilter} onStatusChange={setStatusFilter} />

      {/* Results count */}
      <p className="text-sm text-gray-500">
        Showing {filtered.length} of {forms.length} forms
      </p>

      {/* Table */}
      <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Reference</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Name</th>
                <th className="hidden px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500 md:table-cell">Email</th>
                <th className="hidden px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500 lg:table-cell">NI Number</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Status</th>
                <th className="hidden px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500 sm:table-cell">Submitted</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Errors</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {filtered.map((form) => (
                <tr key={form.id} className="transition-colors hover:bg-gray-50">
                  <td className="whitespace-nowrap px-4 py-3">
                    <Link to={`/form/${form.id}`} className="text-sm font-medium text-indigo-600 hover:text-indigo-900 hover:underline">
                      {form.application_reference?.slice(0, 8)}…
                    </Link>
                  </td>
                  <td className="whitespace-nowrap px-4 py-3 text-sm text-gray-900">
                    {form.first_name || form.email?.split('@')[0] || '—'} {form.surname || ''}
                  </td>
                  <td className="hidden whitespace-nowrap px-4 py-3 text-sm text-gray-500 md:table-cell">
                    {form.email || '—'}
                  </td>
                  <td className="hidden whitespace-nowrap px-4 py-3 text-sm text-gray-500 lg:table-cell">
                    {form.national_insurance_number || '—'}
                  </td>
                  <td className="whitespace-nowrap px-4 py-3">
                    <StatusBadge status={form.status} />
                  </td>
                  <td className="hidden whitespace-nowrap px-4 py-3 text-sm text-gray-500 sm:table-cell">
                    {form.submitted_at ? new Date(form.submitted_at).toLocaleDateString('en-GB', { day: 'numeric', month: 'short' }) : '—'}
                  </td>
                  <td className="whitespace-nowrap px-4 py-3 text-sm">
                    {form.extraction_errors === null ? (
                      <span className="text-gray-400">…</span>
                    ) : form.extraction_errors > 0 ? (
                      <span className="font-medium text-red-600">{form.extraction_errors}</span>
                    ) : (
                      <span className="text-green-600">✓</span>
                    )}
                  </td>
                </tr>
              ))}
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-sm text-gray-400">
                    No forms match your search.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

function SummaryCard({ label, value, color }) {
  const colors = {
    gray: 'bg-gray-50 border-gray-200 text-gray-900',
    yellow: 'bg-yellow-50 border-yellow-200 text-yellow-900',
    green: 'bg-green-50 border-green-200 text-green-900',
    red: 'bg-red-50 border-red-200 text-red-900',
    orange: 'bg-orange-50 border-orange-200 text-orange-900',
  };
  return (
    <div className={`rounded-lg border p-4 ${colors[color]}`}>
      <p className="text-sm font-medium opacity-70">{label}</p>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}
