import React, { useState } from 'react';

const SIPCalculator = () => {
  const [investment, setInvestment] = useState(5000);
  const [rate, setRate] = useState(12);
  const [years, setYears] = useState(10);

  // SIP Future Value Formula: P × ({[1 + i]^n - 1} / i) × (1 + i)
  const calculateSIP = () => {
    const P = Number(investment);
    const i = Number(rate) / 12 / 100;
    const n = Number(years) * 12;

    const futureValue = P * ((Math.pow(1 + i, n) - 1) / i) * (1 + i);
    const totalInvested = P * n;
    const wealthGained = futureValue - totalInvested;

    return {
      futureValue: Math.round(futureValue),
      totalInvested: Math.round(totalInvested),
      wealthGained: Math.round(wealthGained)
    };
  };

  const formatCurrency = (val) => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(val);

  const results = calculateSIP();

  return (
    <div className="max-w-4xl mx-auto w-full">
      <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Systematic Investment Plan (SIP) Calculator</h1>
          <p className="text-sm text-slate-500">Estimate wealth creation through regular mutual fund investments</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="mc-card p-6">
          <div className="mb-6">
            <div className="flex justify-between mb-2">
              <label className="font-bold text-slate-700">Monthly Investment (₹)</label>
              <span className="font-bold text-orange-500 bg-orange-50 px-2 rounded border border-orange-200">{formatCurrency(investment)}</span>
            </div>
            <input 
              type="range" 
              min="500" max="100000" step="500" 
              value={investment} 
              onChange={(e) => setInvestment(e.target.value)} 
              className="w-full accent-orange-500"
            />
          </div>

          <div className="mb-6">
            <div className="flex justify-between mb-2">
              <label className="font-bold text-slate-700">Expected Return Rate (p.a)</label>
              <span className="font-bold text-orange-500 bg-orange-50 px-2 rounded border border-orange-200">{rate}%</span>
            </div>
            <input 
              type="range" 
              min="1" max="30" step="0.5" 
              value={rate} 
              onChange={(e) => setRate(e.target.value)} 
              className="w-full accent-orange-500"
            />
          </div>

          <div className="mb-6">
            <div className="flex justify-between mb-2">
              <label className="font-bold text-slate-700">Time Period (Years)</label>
              <span className="font-bold text-orange-500 bg-orange-50 px-2 rounded border border-orange-200">{years} Yr</span>
            </div>
            <input 
              type="range" 
              min="1" max="40" step="1" 
              value={years} 
              onChange={(e) => setYears(e.target.value)} 
              className="w-full accent-orange-500"
            />
          </div>
        </div>

        <div className="mc-card p-6 bg-slate-50 flex flex-col justify-center">
          <h3 className="text-center font-bold text-slate-600 mb-6 uppercase">Estimated Returns</h3>
          
          <div className="space-y-4">
            <div className="flex justify-between items-center pb-2 border-b border-slate-200">
              <span className="text-slate-600 font-medium">Invested Amount</span>
              <span className="text-lg font-bold text-slate-800">{formatCurrency(results.totalInvested)}</span>
            </div>
            <div className="flex justify-between items-center pb-2 border-b border-slate-200">
              <span className="text-slate-600 font-medium">Est. Returns</span>
              <span className="text-lg font-bold text-[#138150]">+{formatCurrency(results.wealthGained)}</span>
            </div>
            <div className="flex justify-between items-center pt-2">
              <span className="text-slate-800 font-bold">Total Value</span>
              <span className="text-2xl font-black text-blue-700">{formatCurrency(results.futureValue)}</span>
            </div>
          </div>
          
          <button className="mt-8 w-full bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 px-4 rounded transition-colors shadow-sm">
            Invest Now
          </button>
        </div>
      </div>
    </div>
  );
};

export default SIPCalculator;
