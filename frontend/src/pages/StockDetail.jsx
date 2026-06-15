import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { TrendingUp, TrendingDown, Star } from 'lucide-react';
import { getAllStocks } from '../api/stockApi';

const StockDetail = () => {
  const { ticker } = useParams();
  const [stock, setStock] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchStock = async () => {
      setIsLoading(true);
      setError(null);
      try {
        const stocks = await getAllStocks();
        const found = stocks.find(
          (s) => s.symbol.toUpperCase() === ticker.toUpperCase()
        );
        if (found) {
          setStock(found);
        } else {
          setError(`Stock ${ticker} not found`);
        }
      } catch {
        setError('Unable to fetch stock data');
      } finally {
        setIsLoading(false);
      }
    };

    fetchStock();
  }, [ticker]);

  if (isLoading) {
    return (
      <div className="w-full space-y-6">
        <div className="flex justify-between items-start mb-6 border-b-2 border-slate-800 pb-4 animate-pulse">
          <div className="space-y-2">
            <div className="bg-slate-200 h-8 rounded w-40"></div>
            <div className="bg-slate-200 h-4 rounded w-56"></div>
          </div>
          <div className="text-right space-y-2">
            <div className="bg-slate-200 h-10 rounded w-36 ml-auto"></div>
            <div className="bg-slate-200 h-4 rounded w-28 ml-auto"></div>
          </div>
        </div>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 mc-card h-96 animate-pulse bg-slate-100 rounded"></div>
          <div className="lg:col-span-1 mc-card p-4 animate-pulse space-y-4">
            {[1, 2, 3, 4, 5, 6].map((i) => (
              <div key={i} className="flex justify-between">
                <div className="bg-slate-200 h-4 rounded w-24"></div>
                <div className="bg-slate-200 h-4 rounded w-20"></div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="w-full h-64 flex items-center justify-center">
        <div className="mc-card p-8 text-center text-red-500 font-bold">
          {error}
        </div>
      </div>
    );
  }

  const dayChange = stock.currentPrice - stock.previousClose;
  const dayChangePct = stock.previousClose
    ? (dayChange / stock.previousClose) * 100
    : 0;
  const isPositive = dayChange >= 0;

  const formatMarketCap = (val) => {
    if (!val) return '—';
    if (val >= 1e12) return `₹${(val / 1e12).toFixed(2)}L Cr`;
    if (val >= 1e7) return `₹${(val / 1e7).toFixed(2)} Cr`;
    return `₹${val.toLocaleString('en-IN')}`;
  };

  return (
    <div className="w-full">
      {/* Stock Header */}
      <div className="flex justify-between items-start mb-6 border-b-2 border-slate-800 pb-4">
        <div>
          <div className="flex items-center gap-3 mb-1">
            <h1 className="text-3xl font-black text-slate-800 tracking-tight">{stock.symbol}</h1>
            <span className="bg-slate-200 text-slate-700 text-xs font-bold px-2 py-1 rounded">NSE</span>
            <span className="bg-slate-200 text-slate-700 text-xs font-bold px-2 py-1 rounded">BSE</span>
          </div>
          <p className="text-sm text-slate-500 font-medium">
            {stock.companyName} • Equity{stock.sector ? ` • ${stock.sector}` : ''}
          </p>
        </div>
        <div className="text-right">
          <div className="flex items-end justify-end gap-3 mb-1">
            <h2 className="text-4xl font-black text-slate-800">₹{stock.currentPrice?.toLocaleString('en-IN')}</h2>
            <span className={`flex items-center text-lg font-bold mb-1 ${isPositive ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
              {isPositive ? <TrendingUp size={20} className="mr-1" /> : <TrendingDown size={20} className="mr-1" />}
              {isPositive ? '+' : ''}{dayChange.toFixed(2)} ({isPositive ? '+' : ''}{dayChangePct.toFixed(2)}%)
            </span>
          </div>
          <p className="text-xs text-slate-500 font-medium">
            {stock.lastUpdated
              ? `Last updated: ${new Date(stock.lastUpdated).toLocaleString('en-IN')}`
              : 'As of Today'}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Chart Area */}
        <div className="lg:col-span-2">
          <div className="mc-card p-4">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-bold text-slate-700 uppercase">Advanced Chart</h3>
              <div className="flex bg-slate-100 rounded text-xs font-bold border border-slate-200 overflow-hidden">
                <button className="px-3 py-1 hover:bg-slate-200 border-r border-slate-200">1D</button>
                <button className="px-3 py-1 hover:bg-slate-200 border-r border-slate-200 text-orange-500 bg-white">1W</button>
                <button className="px-3 py-1 hover:bg-slate-200 border-r border-slate-200">1M</button>
                <button className="px-3 py-1 hover:bg-slate-200 border-r border-slate-200">1Y</button>
                <button className="px-3 py-1 hover:bg-slate-200">5Y</button>
              </div>
            </div>
            
            <div className="w-full h-80 bg-slate-900 rounded overflow-hidden relative">
              {/* Using the generated stock chart image as a placeholder for the advanced chart */}
              <img src="/market.jpg" alt="Stock Chart" className="w-full h-full object-cover opacity-80" />
              <div className="absolute inset-0 bg-gradient-to-t from-slate-900/50 to-transparent flex items-center justify-center">
                <div className="bg-black/70 text-white px-4 py-2 rounded text-sm font-bold backdrop-blur-sm">
                  Interactive Charting Engine Active
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Stats Area */}
        <div className="lg:col-span-1">
          <div className="mc-card">
            <div className="bg-slate-100 px-4 py-3 font-bold text-slate-700 uppercase border-b-2 border-slate-200 flex justify-between items-center">
              Overview
              <button className="text-orange-500 flex items-center gap-1 text-xs hover:text-orange-600">
                <Star size={14} /> Add to Watchlist
              </button>
            </div>
            
            <div className="p-4 space-y-4">
              <div className="flex justify-between items-center border-b border-slate-100 pb-2">
                <span className="text-slate-500 text-sm font-medium">Previous Close</span>
                <span className="font-bold text-slate-800">{stock.previousClose?.toLocaleString('en-IN')}</span>
              </div>
              <div className="flex justify-between items-center border-b border-slate-100 pb-2">
                <span className="text-slate-500 text-sm font-medium">Day High</span>
                <span className="font-bold text-[#138150]">{stock.dayHigh?.toLocaleString('en-IN')}</span>
              </div>
              <div className="flex justify-between items-center border-b border-slate-100 pb-2">
                <span className="text-slate-500 text-sm font-medium">Day Low</span>
                <span className="font-bold text-[#d52b2b]">{stock.dayLow?.toLocaleString('en-IN')}</span>
              </div>
              <div className="flex justify-between items-center border-b border-slate-100 pb-2">
                <span className="text-slate-500 text-sm font-medium">52 Week High</span>
                <span className="font-bold text-slate-800">{stock.weekHigh52?.toLocaleString('en-IN')}</span>
              </div>
              <div className="flex justify-between items-center border-b border-slate-100 pb-2">
                <span className="text-slate-500 text-sm font-medium">52 Week Low</span>
                <span className="font-bold text-slate-800">{stock.weekLow52?.toLocaleString('en-IN')}</span>
              </div>
              <div className="flex justify-between items-center border-b border-slate-100 pb-2">
                <span className="text-slate-500 text-sm font-medium">Market Cap</span>
                <span className="font-bold text-slate-800">{formatMarketCap(stock.marketCap)}</span>
              </div>
              <div className="flex justify-between items-center pt-2">
                <span className="text-slate-500 text-sm font-medium">Sector</span>
                <span className="font-bold text-slate-800">{stock.sector || '—'}</span>
              </div>
            </div>
            
            <div className="px-4 py-3 bg-slate-50 border-t border-slate-200">
              <button className="w-full bg-[#138150] hover:bg-[#0f6840] text-white font-bold py-2 rounded shadow transition-colors mb-2">
                BUY {stock.symbol}
              </button>
              <button className="w-full bg-[#d52b2b] hover:bg-[#b02222] text-white font-bold py-2 rounded shadow transition-colors">
                SELL {stock.symbol}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StockDetail;
