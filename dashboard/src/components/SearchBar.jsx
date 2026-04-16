const STATUSES = ['All', 'Pending Review', 'Approved', 'Rejected', 'Info Requested'];

export default function SearchBar({ query, onQueryChange, statusFilter, onStatusChange }) {
  return (
    <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
      <div className="relative flex-1">
        <svg className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input
          type="text"
          placeholder="Search by name, reference, or NI number…"
          value={query}
          onChange={(e) => onQueryChange(e.target.value)}
          className="w-full rounded-lg border border-gray-300 py-2 pl-10 pr-4 text-sm shadow-sm focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 focus:outline-none"
        />
      </div>
      <select
        value={statusFilter}
        onChange={(e) => onStatusChange(e.target.value)}
        className="rounded-lg border border-gray-300 py-2 pl-3 pr-8 text-sm shadow-sm focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 focus:outline-none"
      >
        {STATUSES.map((s) => (
          <option key={s} value={s}>{s === 'All' ? 'All statuses' : s}</option>
        ))}
      </select>
    </div>
  );
}
