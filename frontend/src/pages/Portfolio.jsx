import { useState, useEffect } from 'react';
import { Briefcase } from 'lucide-react';
import api from '../api/axios';

const Portfolio = () => {
  const [holdings, setHoldings] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchPortfolio = async () => {
    try {
      setLoading(true);
      const res = await api.get('/api/portfolios/my');
      
      const mapped = res.data.items.map(h => {
        const change = h.stock.currentPrice - h.stock.previousClose;
        const pct = h.stock.previousClose ? ((change / h.stock.previousClose) * 100) : 0;
        return {
          ticker: h.stock.symbol,
          name: h.stock.companyName,
          qty: h.quantity,
          avgPrice: h.averagePrice,
          cmp: h.stock.currentPrice,
          dayChange: `${change >= 0 ? '+' : ''}${pct.toFixed(2)}%`
        };
      });

      setHoldings(mapped);
    } catch (err) {
      console.error("Failed to fetch portfolio:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPortfolio();
  }, []);

  const handleAddTransaction = async () => {
    const symbol = window.prompt("Enter Stock Symbol to Buy (e.g. RELIANCE, TCS, ZOMATO):");
    if (!symbol) return;
    
    const qtyStr = window.prompt(`How many shares of ${symbol} would you like to buy?`);
    const quantity = parseInt(qtyStr, 10);
    if (!quantity || isNaN(quantity) || quantity <= 0) {
      alert("Invalid quantity.");
      return;
    }

    try {
      // Pass buyPrice as null to use current market price
      await api.post(`/api/portfolios/add?symbol=${symbol.toUpperCase()}&quantity=${quantity}`);
      alert(`Successfully bought ${quantity} shares of ${symbol.toUpperCase()} at market price!`);
      fetchPortfolio(); // Refresh list
    } catch {
      alert("Failed to add transaction. Check if the symbol is valid.");
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-slate-500">Loading Portfolio...</div>;
  }

  const totalInvested = holdings.reduce((acc, h) => acc + (h.qty * h.avgPrice), 0);
  const currentValue = holdings.reduce((acc, h) => acc + (h.qty * h.cmp), 0);
  const totalReturn = currentValue - totalInvested;
  const returnPercentage = totalInvested ? ((totalReturn / totalInvested) * 100).toFixed(2) : 0;

  return (
    <div className="w-full">
      <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
        <div className="flex items-center gap-2">
          <Briefcase className="text-orange-500" size={28} />
          <div>
            <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">My Portfolio</h1>
            <p className="text-sm text-slate-500">Track your equity holdings and performance</p>
          </div>
        </div>
        <button 
          onClick={handleAddTransaction}
          className="bg-orange-500 hover:bg-orange-600 text-white font-bold py-2 px-4 rounded text-sm transition-colors"
        >
          + Add Transaction
        </button>
      </div>

      {/* Portfolio Summary Widgets */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div className="mc-card p-6 border-l-4 border-blue-500">
          <h3 className="text-slate-500 font-bold uppercase text-xs mb-1">Total Invested</h3>
          <p className="text-2xl font-black text-slate-800">₹{totalInvested.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</p>
        </div>
        <div className="mc-card p-6 border-l-4 border-slate-800">
          <h3 className="text-slate-500 font-bold uppercase text-xs mb-1">Current Value</h3>
          <p className="text-2xl font-black text-slate-800">₹{currentValue.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</p>
        </div>
        <div className={`mc-card p-6 border-l-4 ${totalReturn >= 0 ? 'border-[#138150]' : 'border-[#d52b2b]'}`}>
          <h3 className="text-slate-500 font-bold uppercase text-xs mb-1">Overall Return</h3>
          <div className="flex items-end gap-2">
            <p className={`text-2xl font-black ${totalReturn >= 0 ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
              {totalReturn >= 0 ? '+' : ''}₹{totalReturn.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </p>
            <p className={`font-bold pb-1 ${totalReturn >= 0 ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
              ({totalReturn >= 0 ? '+' : ''}{returnPercentage}%)
            </p>
          </div>
        </div>
      </div>

      <div className="mc-card overflow-hidden">
        <div className="bg-slate-100 px-4 py-3 font-bold text-slate-700 uppercase border-b-2 border-slate-200">
          Equity Holdings
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="bg-slate-50 text-slate-500 border-b border-slate-200">
              <tr>
                <th className="px-4 py-3 font-medium uppercase tracking-wider">Stock</th>
                <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">Qty</th>
                <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">Avg Price</th>
                <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">CMP</th>
                <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">Invested</th>
                <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">Current Value</th>
                <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">P&L</th>
              </tr>
            </thead>
            <tbody>
              {holdings.map((h, i) => {
                const invested = h.qty * h.avgPrice;
                const current = h.qty * h.cmp;
                const pnl = current - invested;
                const pnlPercent = ((pnl / invested) * 100).toFixed(2);
                
                return (
                  <tr key={i} className="border-b border-slate-200 hover:bg-slate-50 transition-colors cursor-pointer">
                    <td className="px-4 py-3">
                      <p className="font-bold text-blue-700">{h.ticker}</p>
                      <p className="text-xs text-slate-500">{h.name}</p>
                    </td>
                    <td className="px-4 py-3 font-bold text-slate-800 text-right">{h.qty}</td>
                    <td className="px-4 py-3 text-slate-600 text-right">₹{h.avgPrice.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
                    <td className="px-4 py-3 font-bold text-slate-800 text-right">
                      ₹{h.cmp?.toLocaleString('en-IN', {minimumFractionDigits: 2})}
                      <span className={`block text-xs ${h.dayChange.startsWith('+') ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
                        {h.dayChange}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-slate-600 text-right">₹{invested.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
                    <td className="px-4 py-3 font-bold text-slate-800 text-right">₹{current.toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
                    <td className={`px-4 py-3 font-bold text-right ${pnl >= 0 ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
                      {pnl >= 0 ? '+' : ''}₹{pnl.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                      <span className="block text-xs">({pnl >= 0 ? '+' : ''}{pnlPercent}%)</span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Portfolio;
