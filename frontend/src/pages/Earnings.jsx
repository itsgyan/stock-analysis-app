import { useState, useEffect } from 'react';
import api from '../api/axios';

const Earnings = () => {
  const [earnings, setEarnings] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDailyEarnings = async () => {
      setIsLoading(true);
      setError(null);
      try {
        const response = await api.get('/api/earnings');
        setEarnings(response.data);
      } catch (err) {
        console.error("Failed to fetch daily earnings", err);
        setError("Unable to fetch earnings data. Please try again later.");
      } finally {
        setIsLoading(false);
      }
    };
    
    fetchDailyEarnings();
  }, []);

  return (
    <div className="w-full">
      <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Corporate Earnings</h1>
          <p className="text-sm text-slate-500">Quarterly results, financial statements, and board meetings. Click a row to view the official report.</p>
        </div>
      </div>

      {error && (
        <div className="mb-4 p-4 bg-red-50 text-red-600 rounded border border-red-200">
          {error}
        </div>
      )}

      <div className="mc-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="bg-slate-100 text-slate-600 border-b-2 border-slate-200">
              <tr>
                <th className="px-4 py-3 font-bold uppercase tracking-wider">Company</th>
                <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Quarter</th>
                <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Revenue</th>
                <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Net Profit (PAT)</th>
                <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">YoY Growth</th>
              </tr>
            </thead>
            <tbody>
              {isLoading ? (
                <tr>
                  <td colSpan="5" className="px-4 py-8 text-center text-slate-500 font-medium">
                    <div className="flex justify-center items-center space-x-2">
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-700"></div>
                      <span>Fetching latest daily earnings data...</span>
                    </div>
                  </td>
                </tr>
              ) : earnings.length > 0 ? (
                earnings.map((eq, i) => {
                  const isPositive = eq.yoy.startsWith('+');
                  return (
                    <tr 
                      key={i} 
                      className="border-b border-slate-200 hover:bg-slate-50 cursor-pointer transition-colors hover:shadow-sm"
                      onClick={() => window.open(eq.url, '_blank', 'noopener,noreferrer')}
                      title="Click to view official Investor Relations page"
                    >
                      <td className="px-4 py-3 font-bold text-blue-700">{eq.name}</td>
                      <td className="px-4 py-3 font-bold text-right text-slate-600">{eq.qtr}</td>
                      <td className="px-4 py-3 font-bold text-right">{eq.rev}</td>
                      <td className={`px-4 py-3 font-bold text-right ${isPositive ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>{eq.pat}</td>
                      <td className={`px-4 py-3 font-bold text-right ${isPositive ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>{eq.yoy}</td>
                    </tr>
                  )
                })
              ) : (
                <tr>
                  <td colSpan="5" className="px-4 py-8 text-center text-slate-500 font-medium">
                    No earnings data reported today.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Earnings;
