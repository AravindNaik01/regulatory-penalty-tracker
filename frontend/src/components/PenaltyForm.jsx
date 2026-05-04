import { useState, useEffect } from 'react';
import api from '../services/api';
import { Sparkles, X } from 'lucide-react';

export default function PenaltyForm({ penaltyId, onClose, onSaved }) {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    severity: 'Low',
    issuedDate: '',
    amount: '',
    companyName: '',
    status: 'Open'
  });
  const [aiInsights, setAiInsights] = useState(null);
  const [loadingAi, setLoadingAi] = useState(false);

  useEffect(() => {
    if (penaltyId) {
      api.get(`/penalties/${penaltyId}`).then(res => setFormData(res.data));
    }
  }, [penaltyId]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (penaltyId) {
      await api.put(`/penalties/${penaltyId}`, formData);
    } else {
      await api.post('/penalties', formData);
    }
    onSaved();
    onClose();
  };

  const getAiRecommendations = async () => {
    if (!formData.title || !formData.description) return alert('Title and Description needed for AI');
    setLoadingAi(true);
    try {
      const res1 = await api.post('/ai/describe', { title: formData.title, description: formData.description });
      const res2 = await api.post('/ai/recommend', { title: formData.title, description: formData.description });
      setAiInsights({ desc: res1.data.description, recs: res2.data.recommendations });
    } catch (err) {
      setAiInsights({ error: 'AI processing failed' });
    }
    setLoadingAi(false);
  };

  return (
    <div className="bg-slate-800 p-6 rounded-2xl border border-slate-700 shadow-2xl">
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-xl font-semibold text-white">{penaltyId ? 'Edit Penalty' : 'New Penalty'}</h3>
        <button onClick={onClose} className="text-slate-400 hover:text-white transition-colors p-1"><X size={20}/></button>
      </div>
      
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <label className="block text-sm font-medium text-slate-300 mb-1">Title</label>
              <input name="title" value={formData.title} onChange={handleChange} required className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all" />
            </div>
            
            <div className="col-span-2">
              <label className="block text-sm font-medium text-slate-300 mb-1">Description</label>
              <textarea name="description" value={formData.description} onChange={handleChange} rows="3" className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all" />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Company Name</label>
              <input name="companyName" value={formData.companyName} onChange={handleChange} required className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all" />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Issued Date</label>
              <input type="date" name="issuedDate" value={formData.issuedDate} onChange={handleChange} required className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all" />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Severity</label>
              <select name="severity" value={formData.severity} onChange={handleChange} className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all">
                <option>Low</option>
                <option>Medium</option>
                <option>High</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Status</label>
              <select name="status" value={formData.status} onChange={handleChange} className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all">
                <option>Open</option>
                <option>Paid</option>
                <option>Appealed</option>
              </select>
            </div>
            
            <div className="col-span-2">
              <label className="block text-sm font-medium text-slate-300 mb-1">Amount ($)</label>
              <input type="number" name="amount" value={formData.amount} onChange={handleChange} className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all" />
            </div>
          </div>

          <div className="flex gap-3 pt-4 border-t border-slate-700">
            <button type="submit" className="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-2.5 rounded-lg font-medium transition-colors">
              Save Penalty
            </button>
            <button type="button" onClick={getAiRecommendations} disabled={loadingAi} className="flex-1 bg-purple-600/20 hover:bg-purple-600/30 text-purple-400 border border-purple-500/30 py-2.5 rounded-lg font-medium transition-colors flex items-center justify-center gap-2">
              <Sparkles size={18} /> {loadingAi ? 'Analyzing...' : 'AI Insights'}
            </button>
          </div>
        </form>

        <div className="bg-slate-900/50 rounded-xl p-6 border border-slate-700/50">
          <div className="flex items-center gap-2 mb-4 text-purple-400 font-medium">
            <Sparkles size={20} /> AI Analysis Panel
          </div>
          {aiInsights ? (
            <div className="space-y-4 text-sm">
              {aiInsights.error ? (
                <p className="text-red-400">{aiInsights.error}</p>
              ) : (
                <>
                  <div>
                    <h4 className="text-slate-300 font-semibold mb-1">Implications:</h4>
                    <p className="text-slate-400 leading-relaxed whitespace-pre-wrap">{aiInsights.desc}</p>
                  </div>
                  <div>
                    <h4 className="text-slate-300 font-semibold mb-1">Recommendations:</h4>
                    <p className="text-slate-400 leading-relaxed whitespace-pre-wrap">{aiInsights.recs}</p>
                  </div>
                </>
              )}
            </div>
          ) : (
            <div className="h-full flex flex-col items-center justify-center text-slate-500 space-y-3 pb-8">
              <Sparkles size={32} className="opacity-20" />
              <p className="text-center text-sm">Fill in the title and description,<br/>then click AI Insights to analyze the penalty.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
