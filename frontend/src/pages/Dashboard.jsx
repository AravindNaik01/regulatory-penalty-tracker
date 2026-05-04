import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import PenaltyForm from '../components/PenaltyForm';
import { Plus, LogOut, Activity, BarChart, FileText, AlertTriangle } from 'lucide-react';

export default function Dashboard() {
  const [penalties, setPenalties] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [aiReport, setAiReport] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchPenalties();
  }, []);

  const fetchPenalties = async () => {
    try {
      const res = await api.get('/penalties');
      setPenalties(res.data);
    } catch (err) {
      if (err.response?.status === 401 || err.response?.status === 403) {
        navigate('/login');
      }
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure?')) {
      await api.delete(`/penalties/${id}`);
      fetchPenalties();
    }
  };

  const generateReport = async () => {
    setAiReport('Generating...');
    try {
      const list = penalties.map(p => `${p.title} (${p.severity}) - ${p.companyName}`).join(', ');
      const res = await api.post('/ai/generate-report', { penalties: list });
      setAiReport(res.data.report);
    } catch (err) {
      setAiReport('Failed to generate report.');
    }
  };

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100">
      <nav className="bg-slate-800 border-b border-slate-700 p-4 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto flex justify-between items-center">
          <h1 className="text-2xl font-bold flex items-center gap-2 text-white">
            <Activity className="text-blue-500" />
            Penalty Tracker
          </h1>
          <button onClick={handleLogout} className="flex items-center gap-2 text-slate-300 hover:text-white transition-colors">
            <LogOut size={18} /> Logout
          </button>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
        {/* KPI Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-slate-800 p-6 rounded-2xl border border-slate-700 shadow-lg">
            <div className="flex items-center gap-4">
              <div className="p-3 bg-blue-500/10 rounded-xl text-blue-500">
                <FileText size={24} />
              </div>
              <div>
                <p className="text-slate-400 text-sm">Total Penalties</p>
                <p className="text-3xl font-bold text-white">{penalties.length}</p>
              </div>
            </div>
          </div>
          <div className="bg-slate-800 p-6 rounded-2xl border border-slate-700 shadow-lg">
            <div className="flex items-center gap-4">
              <div className="p-3 bg-red-500/10 rounded-xl text-red-500">
                <AlertTriangle size={24} />
              </div>
              <div>
                <p className="text-slate-400 text-sm">High Severity</p>
                <p className="text-3xl font-bold text-white">
                  {penalties.filter(p => p.severity === 'High').length}
                </p>
              </div>
            </div>
          </div>
          <div className="bg-slate-800 p-6 rounded-2xl border border-slate-700 shadow-lg cursor-pointer hover:bg-slate-700/50 transition-colors" onClick={generateReport}>
            <div className="flex items-center gap-4">
              <div className="p-3 bg-purple-500/10 rounded-xl text-purple-500">
                <BarChart size={24} />
              </div>
              <div>
                <p className="text-slate-400 text-sm">AI Exec Report</p>
                <p className="text-sm font-medium text-purple-400 mt-1">Click to generate</p>
              </div>
            </div>
          </div>
        </div>

        {aiReport && (
          <div className="mb-8 p-6 bg-purple-900/20 border border-purple-500/30 rounded-2xl relative">
            <button onClick={() => setAiReport(null)} className="absolute top-4 right-4 text-slate-400 hover:text-white">✕</button>
            <h3 className="text-lg font-semibold text-purple-400 mb-2 flex items-center gap-2">
              <BarChart size={18} /> AI Executive Summary
            </h3>
            <p className="text-slate-300 leading-relaxed whitespace-pre-wrap">{aiReport}</p>
          </div>
        )}

        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-semibold text-white">Recent Penalties</h2>
          <button
            onClick={() => { setShowForm(true); setEditingId(null); }}
            className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-all shadow-lg shadow-blue-500/20"
          >
            <Plus size={18} /> Add Penalty
          </button>
        </div>

        {showForm && (
          <div className="mb-8">
            <PenaltyForm
              penaltyId={editingId}
              onClose={() => setShowForm(false)}
              onSaved={fetchPenalties}
            />
          </div>
        )}

        <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden shadow-xl">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-900/50 border-b border-slate-700">
                  <th className="p-4 font-semibold text-slate-300">Title</th>
                  <th className="p-4 font-semibold text-slate-300">Company</th>
                  <th className="p-4 font-semibold text-slate-300">Severity</th>
                  <th className="p-4 font-semibold text-slate-300">Status</th>
                  <th className="p-4 font-semibold text-slate-300 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700/50">
                {penalties.map(p => (
                  <tr key={p.id} className="hover:bg-slate-700/20 transition-colors">
                    <td className="p-4 text-white font-medium">{p.title}</td>
                    <td className="p-4 text-slate-300">{p.companyName}</td>
                    <td className="p-4">
                      <span className={`px-3 py-1 rounded-full text-xs font-medium border ${
                        p.severity === 'High' ? 'bg-red-500/10 text-red-400 border-red-500/20' :
                        p.severity === 'Medium' ? 'bg-yellow-500/10 text-yellow-400 border-yellow-500/20' :
                        'bg-green-500/10 text-green-400 border-green-500/20'
                      }`}>
                        {p.severity}
                      </span>
                    </td>
                    <td className="p-4 text-slate-300">{p.status}</td>
                    <td className="p-4 text-right space-x-3">
                      <button
                        onClick={() => { setEditingId(p.id); setShowForm(true); }}
                        className="text-blue-400 hover:text-blue-300 transition-colors text-sm font-medium"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(p.id)}
                        className="text-red-400 hover:text-red-300 transition-colors text-sm font-medium"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
                {penalties.length === 0 && (
                  <tr>
                    <td colSpan="5" className="p-8 text-center text-slate-400">
                      No penalties found. Add one to get started.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
