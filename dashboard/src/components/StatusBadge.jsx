const STATUS_STYLES = {
  'Pending Review': 'bg-yellow-100 text-yellow-800 border-yellow-300',
  'Approved': 'bg-green-100 text-green-800 border-green-300',
  'Rejected': 'bg-red-100 text-red-800 border-red-300',
  'Info Requested': 'bg-blue-100 text-blue-800 border-blue-300',
};

export default function StatusBadge({ status }) {
  const classes = STATUS_STYLES[status] || 'bg-gray-100 text-gray-800 border-gray-300';
  return (
    <span className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium ${classes}`}>
      {status}
    </span>
  );
}
