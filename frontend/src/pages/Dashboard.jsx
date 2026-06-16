import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllStocks } from '../api/stockApi';
import api from '../api/axios';

const LoadingSkeleton = () => (
  <div className="w-full space-y-4">
    <div className="mc-card p-4 mb-4 grid grid-cols-2 md:grid-cols-4 gap-4">
      {[1, 2, 3, 4].map((i) => (
        <div key={i} className="animate-pulse space-y-2 pl-4 first:pl-0">
          <div className="bg-slate-200 h-3 rounded w-20"></div>
          <div className="bg-slate-200 h-6 rounded w-28"></div>
          <div className="bg-slate-200 h-3 rounded w-24"></div>
        </div>
      ))}
    </div>
    <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
      <div className="lg:col-span-3 space-y-4">
        {[1, 2].map((i) => (
          <div key={i} className="mc-card p-4 animate-pulse space-y-3">
            <div className="bg-slate-200 h-4 rounded w-32"></div>
            {[1, 2, 3, 4].map((j) => (
              <div key={j} className="flex justify-between">
                <div className="bg-slate-200 h-3 rounded w-24"></div>
                <div className="bg-slate-200 h-3 rounded w-16"></div>
              </div>
            ))}
          </div>
        ))}
      </div>
      <div className="lg:col-span-6">
        <div className="mc-card h-96 animate-pulse bg-slate-100 rounded"></div>
      </div>
      <div className="lg:col-span-3">
        <div className="mc-card p-4 animate-pulse space-y-3">
          <div className="bg-slate-200 h-4 rounded w-40"></div>
          {[1, 2, 3, 4, 5].map((i) => (
            <div key={i} className="bg-slate-200 h-10 rounded w-full"></div>
          ))}
        </div>
      </div>
    </div>
  </div>
);

const TableWidget = ({ title, data, isGainer }) => (
  <div className="mc-card mb-4">
    <div className={`px-4 py-2 font-bold text-sm border-b border-slate-200 uppercase flex items-center justify-between ${isGainer ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
      <span>{title}</span>
      <span className="text-xs font-normal text-slate-500 cursor-pointer hover:underline">View All</span>
    </div>
    <div className="overflow-x-auto">
        <table className="w-full text-xs text-left">
      <thead className="bg-slate-50 text-slate-500 border-b border-slate-200">
        <tr>
          <th className="px-4 py-2 font-medium">Company</th>
          <th className="px-4 py-2 font-medium text-right">Price</th>
          <th className="px-4 py-2 font-medium text-right">% Chg</th>
        </tr>
      </thead>
      <tbody>
        {data.map((row, i) => (
          <tr key={i} className="border-b border-slate-100 hover:bg-slate-50 cursor-pointer">
            <td className="px-4 py-2 font-medium text-blue-700">
              <Link to={`/stock/${row.symbol}`} className="hover:underline">{row.name}</Link>
            </td>
            <td className="px-4 py-2 font-bold text-right">{row.price}</td>
            <td className={`px-4 py-2 font-bold text-right ${isGainer ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>{row.change}</td>
          </tr>
        ))}
      </tbody>
    </table>
      </div>
  </div>
);

const formatRelativeTime = (dateStr) => {
  if (!dateStr) return '';
  const diff = Date.now() - new Date(dateStr).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return 'Just now';
  if (mins < 60) return `${mins} min${mins > 1 ? 's' : ''} ago`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours} hour${hours > 1 ? 's' : ''} ago`;
  const days = Math.floor(hours / 24);
  return `${days} day${days > 1 ? 's' : ''} ago`;
};

const NewsWidget = ({ news }) => (
  <div className="mc-card mb-4">
    <div className="px-4 py-2 font-bold text-sm border-b border-slate-200 uppercase text-slate-800 bg-slate-100">
      Latest Business News
    </div>
    <div className="flex flex-col divide-y divide-slate-200">
      {news && news.length > 0 ? (
        news.slice(0, 5).map((item, i) => (
          <div key={item.id || i} className="p-4 hover:bg-slate-50 cursor-pointer group">
            <a href={item.url} target="_blank" rel="noopener noreferrer" className="text-sm font-medium text-slate-800 group-hover:text-orange-500 transition-colors leading-relaxed block">
              {item.title}
            </a>
            <span className="text-[10px] text-slate-400 mt-1 block">
              {item.source && <span className="font-bold text-orange-600 mr-2">{item.source.toUpperCase()}</span>}
              {formatRelativeTime(item.publishedAt)}
            </span>
          </div>
        ))
      ) : (
        <div className="p-4 text-center text-sm text-slate-500 font-medium">
          No news available
        </div>
      )}
    </div>
  </div>
);

const MarketIndicesWidget = ({ indices = [] }) => {
  // Use the top 4 actual indices (like SENSEX, NIFTY 50) passed from Dashboard
  const topIndices = indices.slice(0, 4);

  return (
    <div className="mc-card p-4 mb-4 grid grid-cols-2 md:grid-cols-4 gap-4 divide-x divide-slate-200">
      {topIndices.length > 0 ? (
        topIndices.map((idx, i) => (
          <div key={i} className="pl-4 first:pl-0">
            <p className="text-xs font-bold text-slate-500">{idx.name}</p>
            <p className="text-lg font-bold text-slate-800">{Number(idx.price).toLocaleString('en-IN', { maximumFractionDigits: 2 })}</p>
            <div className={`flex items-center gap-1 text-xs font-bold ${idx.pos ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
              <span>{idx.pos ? '▲' : '▼'} {Math.abs(idx.change).toFixed(2)}</span>
              <span>({idx.pos ? '+' : ''}{Number(idx.pctChange).toFixed(2)}%)</span>
            </div>
          </div>
        ))
      ) : (
        <div className="col-span-4 text-center text-slate-500 text-sm">Loading indices...</div>
      )}
    </div>
  );
};

const Dashboard = () => {
  const [stocks, setStocks] = useState([]);
  const [news, setNews] = useState([]);
  const [indices, setIndices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);

        const [stocksData, newsRes, indicesRes] = await Promise.allSettled([
          getAllStocks(),
          api.get('/api/news'),
          api.get('/api/indices'),
        ]);

        if (stocksData.status === 'fulfilled') {
          setStocks(stocksData.value);
        } else {
          setError('Unable to fetch market data');
        }

        if (newsRes.status === 'fulfilled') {
          setNews(newsRes.value.data);
        }
        
        if (indicesRes.status === 'fulfilled') {
          setIndices(indicesRes.value.data);
        }
      } catch {
        setError('Unable to fetch market data');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) return <LoadingSkeleton />;

  if (error && stocks.length === 0) {
    return (
      <div className="mc-card p-8 text-center text-red-500 font-bold">
        {error}
      </div>
    );
  }

  // Calculate gainers and losers
  const stocksWithChange = stocks
    .filter((s) => s.previousClose && s.currentPrice)
    .map((s) => ({
      ...s,
      changePct: ((s.currentPrice - s.previousClose) / s.previousClose) * 100,
    }));

  const gainers = stocksWithChange
    .filter((s) => s.changePct > 0)
    .sort((a, b) => b.changePct - a.changePct)
    .slice(0, 5)
    .map((s) => ({
      name: s.companyName,
      symbol: s.symbol,
      price: `₹${s.currentPrice.toLocaleString('en-IN')}`,
      change: `+${s.changePct.toFixed(2)}%`,
    }));

  const losers = stocksWithChange
    .filter((s) => s.changePct < 0)
    .sort((a, b) => a.changePct - b.changePct)
    .slice(0, 5)
    .map((s) => ({
      name: s.companyName,
      symbol: s.symbol,
      price: `₹${s.currentPrice.toLocaleString('en-IN')}`,
      change: `${s.changePct.toFixed(2)}%`,
    }));

  return (
    <div className="w-full">
      <MarketIndicesWidget indices={indices} />
      
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* Left Column - Market Action */}
        <div className="lg:col-span-3">
          <TableWidget 
            title="Top Gainers" 
            isGainer={true}
            data={gainers}
          />
          <TableWidget 
            title="Top Losers" 
            isGainer={false}
            data={losers}
          />
        </div>

        {/* Middle Column - Main Content / Charts */}
        <div className="lg:col-span-6">
          <div className="mc-card h-96 flex flex-col mb-4">
            <div className="px-4 py-2 border-b border-slate-200 bg-slate-100 flex justify-between items-center">
              <span className="font-bold text-sm text-slate-800 uppercase">Intraday Chart - NIFTY 50</span>
              <div className="flex gap-2">
                <button className="px-2 py-0.5 bg-orange-500 text-white text-xs font-bold rounded">1D</button>
                <button className="px-2 py-0.5 bg-white border border-slate-300 text-slate-600 text-xs font-bold rounded hover:bg-slate-50">1W</button>
                <button className="px-2 py-0.5 bg-white border border-slate-300 text-slate-600 text-xs font-bold rounded hover:bg-slate-50">1M</button>
              </div>
            </div>
            <div className="flex-1 bg-slate-50 flex items-center justify-center border-b border-slate-200">
              <p className="text-slate-400 text-sm font-medium">Interactive Chart Container</p>
            </div>
            <div className="h-12 bg-white flex items-center px-4 gap-6 text-xs font-medium text-slate-600">
              {stocks.length > 0 && (
                <>
                  <div className="flex gap-1"><span className="text-slate-400">High</span><span className="text-slate-800">{stocks[0]?.dayHigh?.toLocaleString('en-IN')}</span></div>
                  <div className="flex gap-1"><span className="text-slate-400">Low</span><span className="text-slate-800">{stocks[0]?.dayLow?.toLocaleString('en-IN')}</span></div>
                  <div className="flex gap-1"><span className="text-slate-400">Prev Close</span><span className="text-slate-800">{stocks[0]?.previousClose?.toLocaleString('en-IN')}</span></div>
                </>
              )}
            </div>
          </div>
        </div>

        {/* Right Column - News & Info */}
        <div className="lg:col-span-3">
          <NewsWidget news={news} />
        </div>

      </div>
    </div>
  );
};

export default Dashboard;
