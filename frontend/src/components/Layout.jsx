import React, { useState, useEffect, useRef, memo } from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { TrendingUp, TrendingDown, Menu, Search, User, Activity } from 'lucide-react';
import { searchStocksApi } from '../api/stockApi';

const tickerItems = [
  { name: "SENSEX", price: "73,158.24", change: "+124.50", pos: true },
  { name: "NIFTY 50", price: "22,212.70", change: "+45.20", pos: true },
  { name: "NIFTY BANK", price: "46,811.15", change: "-110.30", pos: false },
  { name: "RELIANCE", price: "2,985.40", change: "+15.20", pos: true },
  { name: "HDFC BANK", price: "1,440.10", change: "-5.40", pos: false },
  { name: "TCS", price: "4,120.50", change: "+42.10", pos: true },
  { name: "INFY", price: "1,480.20", change: "-12.50", pos: false }
];

const duplicatedTickerItems = [...tickerItems, ...tickerItems, ...tickerItems];

const TickerTape = memo(() => {
  return (
    <div className="bg-slate-900 text-white overflow-hidden whitespace-nowrap border-b border-slate-800 text-xs py-1.5 flex">
      <div className="flex animate-[ticker_30s_linear_infinite]" style={{ willChange: 'transform' }}>
        {duplicatedTickerItems.map((item, i) => (
          <div key={i} className="flex items-center mx-4 gap-2">
            <span className="font-bold">{item.name}</span>
            <span>{item.price}</span>
            <span className={`flex items-center font-bold ${item.pos ? 'text-green-500' : 'text-red-500'}`}>
              {item.pos ? <TrendingUp size={12} className="mr-1" /> : <TrendingDown size={12} className="mr-1" />}
              {item.change}
            </span>
            <span className="text-slate-600 ml-2">|</span>
          </div>
        ))}
      </div>
    </div>
  );
});



const SearchBar = memo(() => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [showDropdown, setShowDropdown] = useState(false);
  const [filteredResults, setFilteredResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const debounceRef = useRef(null);

  // Local fallback data in case the API call fails
  const fallbackItems = [
    { name: "Reliance Industries", type: "Stock", ticker: "RELIANCE" },
    { name: "Tata Consultancy Services", type: "Stock", ticker: "TCS" },
    { name: "HDFC Bank", type: "Stock", ticker: "HDFCBANK" },
    { name: "Infosys", type: "Stock", ticker: "INFY" },
    { name: "ICICI Bank", type: "Stock", ticker: "ICICIBANK" },
    { name: "Parag Parikh Flexi Cap", type: "Mutual Fund", ticker: "PPFAS" },
    { name: "SBI Equity Hybrid Fund", type: "Mutual Fund", ticker: "SBIHYB" },
    { name: "Sensex", type: "Index", ticker: "SENSEX" },
    { name: "Nifty 50", type: "Index", ticker: "NIFTY" },
  ];

  useEffect(() => {
    if (!searchQuery.trim()) {
      setFilteredResults([]);
      return;
    }

    // Debounce API calls by 300ms
    clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(async () => {
      setIsSearching(true);
      try {
        const stocks = await searchStocksApi(searchQuery);
        // Map backend Stock objects to the shape the dropdown expects
        const mapped = stocks.map((s) => ({
          name: s.companyName,
          type: 'Stock',
          ticker: s.symbol,
        }));
        setFilteredResults(mapped.length > 0 ? mapped : []);
      } catch {
        // API failed — fall back to local filtering
        const lowerQuery = searchQuery.toLowerCase();
        setFilteredResults(
          fallbackItems.filter(
            (item) =>
              item.name.toLowerCase().includes(lowerQuery) ||
              item.ticker.toLowerCase().includes(lowerQuery)
          )
        );
      } finally {
        setIsSearching(false);
      }
    }, 300);

    return () => clearTimeout(debounceRef.current);
  }, [searchQuery]);

  const handleSelectSearch = (item) => {
    setShowDropdown(false);
    setSearchQuery('');
    if (item.type === 'Stock') navigate(`/stock/${item.ticker}`);
    else if (item.type === 'Mutual Fund') navigate('/mutual-funds');
    else if (item.type === 'Index') navigate('/indices');
  };

  return (
    <div className="relative flex items-center bg-white rounded-sm px-2 py-1.5 w-full md:w-80">
      <input 
        type="text" 
        placeholder="Search Quotes, News, Mutual Fund..." 
        value={searchQuery}
        onChange={(e) => {
          setSearchQuery(e.target.value);
          setShowDropdown(true);
        }}
        onFocus={() => setShowDropdown(true)}
        onBlur={() => setTimeout(() => setShowDropdown(false), 200)}
        className="w-full text-sm text-slate-800 outline-none border-none"
      />
      <Search size={18} className="text-slate-400 cursor-pointer hover:text-orange-500" />
      
      {showDropdown && searchQuery && (
        <div className="absolute top-full left-0 w-full bg-white shadow-xl border border-slate-200 mt-1 rounded-b overflow-hidden z-50 text-slate-800">
          {isSearching ? (
            <div className="px-4 py-4 text-center text-sm text-slate-500 font-medium">
              Searching...
            </div>
          ) : filteredResults.length > 0 ? (
            <ul className="max-h-64 overflow-y-auto">
              {filteredResults.map((item, i) => (
                <li 
                  key={i} 
                  onMouseDown={() => handleSelectSearch(item)}
                  className="px-4 py-3 border-b border-slate-100 hover:bg-slate-50 cursor-pointer flex justify-between items-center"
                >
                  <div>
                    <p className="font-bold text-blue-700 text-sm leading-tight">{item.name}</p>
                    <p className="text-xs text-slate-500">{item.ticker}</p>
                  </div>
                  <span className="bg-slate-100 text-slate-600 text-[10px] uppercase font-bold px-2 py-1 rounded">
                    {item.type}
                  </span>
                </li>
              ))}
            </ul>
          ) : (
            <div className="px-4 py-4 text-center text-sm text-slate-500 font-medium">
              No matching stocks or funds found.
            </div>
          )}
        </div>
      )}
    </div>
  );
});

const Layout = () => {

  return (
    <div className="min-h-screen flex flex-col bg-[#f1f2f4]">
      {/* Top Utility Bar */}
      <div className="h-8 bg-slate-100 border-b border-slate-200 flex items-center justify-end px-4 gap-4 text-xs font-medium text-slate-600">
        <span className="hover:text-blue-600 cursor-pointer hidden sm:inline">English</span>
        <Link to="/portfolio" className="hover:text-blue-600 cursor-pointer">Portfolio</Link>
        <Link to="/watchlist" className="hover:text-blue-600 cursor-pointer">Watchlist</Link>
        
        {localStorage.getItem('token') ? (
          <div className="flex items-center gap-3">
            <span className="font-bold text-slate-700">Hi, {localStorage.getItem('user')?.split('@')[0] || 'User'}</span>
            <button 
              onClick={() => {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login';
              }} 
              className="text-red-500 hover:text-red-700 font-bold"
            >
              Logout
            </button>
          </div>
        ) : (
          <Link to="/login" className="flex items-center gap-1 cursor-pointer hover:text-blue-600">
            <User size={14} /> Login
          </Link>
        )}
      </div>

      {/* Main Header */}
      <header className="mc-header py-3 px-4 flex flex-col md:flex-row items-start md:items-center justify-between bg-slate-800 text-white gap-4 md:gap-0">
        <div className="flex items-center gap-4 md:gap-6 w-full md:w-auto justify-between md:justify-start">
          <div className="flex items-center gap-4">
            <Menu size={24} className="cursor-pointer hover:text-orange-400" />
            <Link to="/dashboard" className="text-2xl font-black tracking-tighter text-white flex items-center gap-1 md:border-r md:border-slate-600 md:pr-6">
              <Activity className="text-orange-500" size={28} />
              Market<span className="text-orange-500">Lens</span>
            </Link>
          </div>
          <nav className="hidden lg:flex items-center gap-6 text-sm font-medium">
            <Link to="/dashboard" className="hover:text-orange-400 transition-colors">Markets</Link>
            <Link to="/news" className="hover:text-orange-400 transition-colors">News</Link>
            <Link to="/portfolio" className="hover:text-orange-400 transition-colors">Portfolio</Link>
            <Link to="/mutual-funds" className="hover:text-orange-400 transition-colors">Mutual Funds</Link>
          </nav>
        </div>
        <SearchBar />
      </header>

      <TickerTape />

      {/* Sub Navigation */}
      <div className="bg-white border-b border-slate-200 px-4 py-2 flex items-center gap-6 text-xs font-bold text-slate-700 uppercase tracking-wide">
        <Link to="/dashboard" className="text-orange-500 cursor-pointer border-b-2 border-orange-500 pb-1 hover:text-orange-600">Home</Link>
        <Link to="/indices" className="cursor-pointer hover:text-orange-500">Indian Indices</Link>
        <Link to="/global-markets" className="cursor-pointer hover:text-orange-500">Global Markets</Link>
        <Link to="/stock-action" className="cursor-pointer hover:text-orange-500">Stock Action</Link>
        <Link to="/earnings" className="cursor-pointer hover:text-orange-500">Earnings</Link>
      </div>

      {/* Main Content Area */}
      <main className="flex-1 w-full max-w-[1400px] mx-auto p-4">
        <Outlet />
      </main>
      
      {/* Footer */}
      <footer className="bg-[#202b36] text-slate-400 text-xs py-8 px-4 text-center mt-12 border-t-4 border-orange-500">
        <p>Copyright © 2026 MarketLens. All rights reserved.</p>
        <p className="mt-2">Data is delayed by at least 15 minutes. For informational purposes only.</p>
      </footer>
    </div>
  );
};

export default Layout;
