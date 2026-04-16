import { useState, useMemo } from 'react';
import { Link } from 'react-router-dom';
import { mockForms } from '../data/mockData';
import StatusBadge from '../components/StatusBadge';
import SearchBar from '../components/SearchBar';

const STATUS_ORDER = { 'Pending Review': 0, 'Info Requested': 1, 'Approved': 2, 'Rejected': 3 };

export default function DashboardPage() {
  const [query, setQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('All');

  const filtered = useMemo(() => {
    const q = query.toLowerCase().trim();
    return mockForms
      .filter((f) => {
        if (statusFilter !== 'All' && f.status !== statusFilter) return false;
        if (!q) return true;
        return (
          f.application_reference.toLowerCase().includes(q) ||
          f.first_name?.toLowerCase().includes(q) ||
          f.surname?.toLowerCase().includes(q) ||
          f.national_insurance_number?.toLowerCase().includes(q) ||
          `${f.first_name} ${f.surname}`.toLowerCase().includes(q)
        );
      })
      .sort((a, b) => STATUS_ORDER[a.status] - STATUS_ORDER[b.status] || new Date(b.submitted_at) - new Date(a.submitted_at));
  }, [query, statusFilter]);

  // Summary counts
  const counts = useMemo(() => {
    const c = { total: mockForms.length, pending: 0, approved: 0, rejected: 0, info: 0, errors: 0 };
    mockForms.forEach((f) => {
      if (f.status === 'Pending Review') c.pending++;
      else if (f.status === 'Approved') c.approved++;
      else if (f.status === 'Rejected') c.rejected++;
      else if (f.status === 'Info Requested') c.info++;
      if (f.extraction_errors > 0) c.errors++;
    });
    return c;
  }, []);

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
        Showing {filtered.length} of {mockForms.length} forms
      </p>

      {/* Table */}
      <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Reference</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Name</th>
                <th className="hidden px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500 md:table-cell">DOB</th>
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
                      {form.application_reference}
                    </Link>
                  </td>
                  <td className="whitespace-nowrap px-4 py-3 text-sm text-gray-900">
                    {form.first_name || '—'} {form.surname || '—'}
                  </td>
                  <td className="hidden whitespace-nowrap px-4 py-3 text-sm text-gray-500 md:table-cell">
                    {form.date_of_birth || '—'}
                  </td>
                  <td className="hidden whitespace-nowrap px-4 py-3 text-sm text-gray-500 lg:table-cell">
                    {form.national_insurance_number || '—'}
                  </td>
                  <td className="whitespace-nowrap px-4 py-3">
                    <StatusBadge status={form.status} />
                  </td>
                  <td className="hidden whitespace-nowrap px-4 py-3 text-sm text-gray-500 sm:table-cell">
                    {new Date(form.submitted_at).toLocaleDateString('en-GB', { day: 'numeric', month: 'short' })}
                  </td>
                  <td className="whitespace-nowrap px-4 py-3 text-sm">
                    {form.extraction_errors > 0 ? (
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
