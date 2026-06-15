import React from 'react';

const IndexRow = ({ name, price, change, pctChange, pos }) => (
  <tr className="border-b border-slate-200 hover:bg-slate-50 cursor-pointer">
    <td className="px-4 py-3 font-bold text-blue-700">{name}</td>
    <td className="px-4 py-3 font-bold text-right">{price}</td>
    <td className={`px-4 py-3 font-bold text-right ${pos ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
      {pos ? '+' : ''}{change}
    </td>
    <td className={`px-4 py-3 font-bold text-right ${pos ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>
      {pos ? '+' : ''}{pctChange}
    </td>
  </tr>
);

const GlobalMarkets = () => {
  const globalData = [
    { name: 'NASDAQ', price: '16,212.45', change: '150.20', pctChange: '0.95%', pos: true },
    { name: 'DOW JONES', price: '38,980.12', change: '-45.10', pctChange: '-0.11%', pos: false },
    { name: 'S&P 500', price: '5,140.50', change: '35.40', pctChange: '0.68%', pos: true },
    { name: 'FTSE 100', price: '7,950.20', change: '-10.50', pctChange: '-0.13%', pos: false },
    { name: 'NIKKEI 225', price: '39,500.10', change: '420.50', pctChange: '1.05%', pos: true },
    { name: 'HANG SENG', price: '16,750.20', change: '-210.30', pctChange: '-1.25%', pos: false },
    { name: 'SHANGHAI COMP', price: '3,050.40', change: '15.20', pctChange: '0.50%', pos: true },
    { name: 'DAX', price: '18,200.50', change: '50.10', pctChange: '0.28%', pos: true },
  ];

  return (
    <div className="w-full">
      <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Global Markets</h1>
          <p className="text-sm text-slate-500">Real-time performance of major global stock market indices</p>
        </div>
      </div>

      <div className="mc-card overflow-hidden">
        <div className="overflow-x-auto">
        <table className="w-full text-sm text-left">
          <thead className="bg-slate-100 text-slate-600 border-b-2 border-slate-200">
            <tr>
              <th className="px-4 py-3 font-bold uppercase tracking-wider">Index Name</th>
              <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Last Price</th>
              <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Change</th>
              <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">% Chg</th>
            </tr>
          </thead>
          <tbody>
            {globalData.map((idx, i) => (
              <IndexRow key={i} {...idx} />
            ))}
          </tbody>
        </table>
      </div>
      </div>
    </div>
  );
};

export default GlobalMarkets;
