import React, { useState, useEffect } from 'react';
import { Star, TrendingUp, TrendingDown, Trash2 } from 'lucide-react';
import api from '../api/axios';

const Watchlist = () => {
  const [watchlist, setWatchlist] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchWatchlist = async () => {
    try {
      setLoading(true);
      const res = await api.get('/api/watchlists/my');
      
      const mapped = res.data.stocks.map(stock => {
        const change = stock.currentPrice - stock.previousClose;
        const percent = stock.previousClose ? ((change / stock.previousClose) * 100) : 0;
        return {
          ticker: stock.symbol,
          name: stock.companyName,
          cmp: stock.currentPrice,
          change: change,
          percent: percent,
          pos: change >= 0
        };
      });
      setWatchlist(mapped);
    } catch (err) {
      console.error("Failed to fetch watchlist:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWatchlist();
  }, []);

  const handleAddStock = async () => {
    const symbol = window.prompt("Enter Stock Symbol to Add to Watchlist (e.g. RELIANCE, TCS):");
    if (!symbol) return;
    
    try {
      await api.post(`/api/watchlists/add?symbol=${symbol.toUpperCase()}`);
      alert(`Successfully added ${symbol.toUpperCase()} to your watchlist!`);
      fetchWatchlist();
    } catch (err) {
      alert("Failed to add stock. Check if the symbol is valid.");
    }
  };

  const handleRemoveStock = async (symbol) => {
    if(!window.confirm(`Are you sure you want to remove ${symbol}?`)) return;
    try {
      await api.delete(`/api/watchlists/remove?symbol=${symbol.toUpperCase()}`);
      fetchWatchlist();
    } catch (err) {
      alert("Failed to remove stock.");
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-slate-500">Loading Watchlist...</div>;
  }

  return (
    <div className="w-full">
      <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
        <div className="flex items-center gap-2">
          <Star className="text-orange-500 fill-orange-500" size={28} />
          <div>
            <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">My Watchlist</h1>
            <p className="text-sm text-slate-500">Track your favorite stocks in real-time</p>
          </div>
        </div>
        <button 
          onClick={handleAddStock}
          className="bg-orange-500 hover:bg-orange-600 text-white font-bold py-2 px-4 rounded text-sm transition-colors"
        >
          Search to Add
        </button>
      </div>

      <div className="mc-card overflow-hidden">
        <div className="bg-slate-100 px-4 py-3 font-bold text-slate-700 uppercase border-b-2 border-slate-200">
          Watchlist 1
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="bg-slate-50 text-slate-500 border-b border-slate-200">
              <tr>
                <th className="px-4 py-3 font-medium uppercase tracking-wider">Company</th>
                <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">LTP (₹)</th>
                <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">Change</th>
                <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">% Change</th>
                <th className="px-4 py-3 font-medium uppercase tracking-wider text-center">Action</th>
              </tr>
            </thead>
            <tbody>
              {watchlist.map((stock, i) => (
                <tr key={i} className="border-b border-slate-200 hover:bg-slate-50 transition-colors cursor-pointer group">
                  <td className="px-4 py-3">
                    <p className="font-bold text-blue-700">{stock.ticker}</p>
                    <p className="text-xs text-slate-500">{stock.name}</p>
                  </td>
                  <td className="px-4 py-3 font-bold text-slate-800 text-right">{stock.cmp?.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
                  <td className={`px-4 py-3 font-bold text-right flex justify-end items-center gap-1 ${stock.pos ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
                    {stock.pos ? <TrendingUp size={14} /> : <TrendingDown size={14} />}
                    {stock.pos ? '+' : ''}{stock.change?.toFixed(2)}
                  </td>
                  <td className={`px-4 py-3 font-bold text-right ${stock.pos ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
                    {stock.pos ? '+' : ''}{stock.percent?.toFixed(2)}%
                  </td>
                  <td className="px-4 py-3 text-center">
                    <button 
                      onClick={() => handleRemoveStock(stock.ticker)}
                      className="text-slate-400 hover:text-red-500 transition-colors p-1" title="Remove from Watchlist"
                    >
                      <Trash2 size={16} />
                    </button>
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

export default Watchlist;
