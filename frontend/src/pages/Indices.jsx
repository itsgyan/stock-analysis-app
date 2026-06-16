import { useState, useEffect } from 'react';
import { TrendingUp, TrendingDown } from 'lucide-react';
import api from '../api/axios';

const Indices = () => {
  const [indices, setIndices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchIndices = async () => {
      try {
        const res = await api.get('/api/indices');
        setIndices(res.data || []);
      } catch (err) {
        console.error('Failed to fetch indices:', err);
        setError('Unable to fetch live index data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchIndices();
  }, []);

  if (loading) {
    return (
      <div className="w-full space-y-4">
        <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Indian Indices</h1>
            <p className="text-sm text-slate-500">Live performance of major Indian stock market indices</p>
          </div>
        </div>

        {/* Index Cards Skeleton */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="mc-card p-5 animate-pulse space-y-3">
              <div className="bg-slate-200 h-3 rounded w-20"></div>
              <div className="bg-slate-200 h-8 rounded w-28"></div>
              <div className="bg-slate-200 h-4 rounded w-24"></div>
            </div>
          ))}
        </div>

        <div className="mc-card overflow-hidden animate-pulse">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <div key={i} className="flex justify-between px-4 py-4 border-b border-slate-100">
              <div className="bg-slate-200 h-4 rounded w-32"></div>
              <div className="bg-slate-200 h-4 rounded w-20"></div>
              <div className="bg-slate-200 h-4 rounded w-16"></div>
              <div className="bg-slate-200 h-4 rounded w-16"></div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (error && indices.length === 0) {
    return (
      <div className="w-full">
        <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Indian Indices</h1>
          </div>
        </div>
        <div className="mc-card p-8 text-center text-red-500 font-bold">{error}</div>
      </div>
    );
  }

  // Pick top 4 indices for the summary cards
  const topIndices = indices.slice(0, 4);

  return (
    <div className="w-full">
      <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Indian Indices</h1>
          <p className="text-sm text-slate-500">Live performance of major Indian stock market indices</p>
        </div>
        <span className="text-xs text-slate-400 font-medium">Data from Yahoo Finance</span>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {topIndices.map((idx, i) => (
          <div key={i} className={`mc-card p-5 border-t-4 ${idx.pos ? 'border-[#138150]' : 'border-[#d52b2b]'}`}>
            <p className="text-xs font-bold text-slate-500 mb-1">{idx.name}</p>
            <p className="text-2xl font-black text-slate-800">{Number(idx.price).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</p>
            <div className={`flex items-center gap-2 mt-1 text-sm font-bold ${idx.pos ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
              {idx.pos ? <TrendingUp size={16} /> : <TrendingDown size={16} />}
              <span>{idx.pos ? '+' : ''}{Number(idx.change).toFixed(2)}</span>
              <span>({idx.pos ? '+' : ''}{Number(idx.pctChange).toFixed(2)}%)</span>
            </div>
          </div>
        ))}
      </div>

      {/* Full Table */}
      <div className="mc-card overflow-hidden">
        <div className="bg-slate-100 px-4 py-3 font-bold text-sm text-slate-700 uppercase border-b-2 border-slate-200">
          All Indian Market Indices
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="bg-slate-50 text-slate-500 border-b border-slate-200">
              <tr>
                <th className="px-4 py-3 font-bold uppercase tracking-wider">Index Name</th>
                <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Last Price</th>
                <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Change</th>
                <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">% Change</th>
                <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Day High</th>
                <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Day Low</th>
                <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Prev Close</th>
              </tr>
            </thead>
            <tbody>
              {indices.map((idx, i) => (
                <tr key={i} className="border-b border-slate-200 hover:bg-slate-50 cursor-pointer transition-colors">
                  <td className="px-4 py-3">
                    <span className="font-bold text-blue-700">{idx.name}</span>
                  </td>
                  <td className="px-4 py-3 font-bold text-slate-800 text-right">
                    {Number(idx.price).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </td>
                  <td className={`px-4 py-3 font-bold text-right ${idx.pos ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
                    <span className="flex items-center justify-end gap-1">
                      {idx.pos ? <TrendingUp size={14} /> : <TrendingDown size={14} />}
                      {idx.pos ? '+' : ''}{Number(idx.change).toFixed(2)}
                    </span>
                  </td>
                  <td className={`px-4 py-3 font-bold text-right ${idx.pos ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
                    {idx.pos ? '+' : ''}{Number(idx.pctChange).toFixed(2)}%
                  </td>
                  <td className="px-4 py-3 text-right text-slate-600">
                    {Number(idx.dayHigh).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </td>
                  <td className="px-4 py-3 text-right text-slate-600">
                    {Number(idx.dayLow).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </td>
                  <td className="px-4 py-3 text-right text-slate-600">
                    {Number(idx.previousClose).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Indices;
