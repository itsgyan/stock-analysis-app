import React, { useState, useEffect } from 'react';

const FundRow = ({ name, nav, oneYear, threeYear, fiveYear, rating }) => (
  <tr className="border-b border-slate-200 hover:bg-slate-50 cursor-pointer">
    <td className="px-4 py-3">
      <p className="font-bold text-blue-700">{name}</p>
      <p className="text-xs text-slate-500">Equity - ELSS (Tax Saving)</p>
    </td>
    <td className="px-4 py-3 font-bold text-right">₹{nav}</td>
    <td className="px-4 py-3 font-bold text-right text-[#138150]">{oneYear}%</td>
    <td className="px-4 py-3 font-bold text-right text-[#138150]">{threeYear}%</td>
    <td className="px-4 py-3 font-bold text-right text-[#138150]">{fiveYear}%</td>
    <td className="px-4 py-3 text-right">
      <span className="bg-orange-100 text-orange-700 font-bold px-2 py-1 rounded text-xs">
        {rating} ★
      </span>
    </td>
  </tr>
);

const ELSS = () => {
  const [elssFunds, setElssFunds] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [updateFreq, setUpdateFreq] = useState('daily');

  useEffect(() => {
    const fetchFunds = async () => {
      setIsLoading(true);
      // Simulating a fetch from the backend based on Daily vs Weekly frequency
      setTimeout(() => {
        if (updateFreq === 'daily') {
          setElssFunds([
            { name: 'Quant Tax Plan', nav: '345.10', oneYear: '58.2', threeYear: '32.1', fiveYear: '29.5', rating: '5' },
            { name: 'Mirae Asset Tax Saver', nav: '124.50', oneYear: '28.4', threeYear: '18.2', fiveYear: '19.4', rating: '4' },
            { name: 'DSP Tax Saver Fund', nav: '98.20', oneYear: '32.1', threeYear: '21.5', fiveYear: '18.7', rating: '4' },
            { name: 'SBI Long Term Equity', nav: '310.80', oneYear: '41.2', threeYear: '24.8', fiveYear: '17.5', rating: '5' },
            { name: 'Axis Long Term Equity', nav: '85.40', oneYear: '18.5', threeYear: '12.4', fiveYear: '14.2', rating: '3' },
          ]);
        } else {
          setElssFunds([
            { name: 'Quant Tax Plan', nav: '340.50', oneYear: '57.8', threeYear: '31.9', fiveYear: '29.1', rating: '5' },
            { name: 'Mirae Asset Tax Saver', nav: '123.10', oneYear: '28.1', threeYear: '18.0', fiveYear: '19.2', rating: '4' },
            { name: 'DSP Tax Saver Fund', nav: '97.50', oneYear: '31.8', threeYear: '21.1', fiveYear: '18.5', rating: '4' },
            { name: 'SBI Long Term Equity', nav: '308.20', oneYear: '40.5', threeYear: '24.2', fiveYear: '17.1', rating: '5' },
            { name: 'Axis Long Term Equity', nav: '84.90', oneYear: '18.2', threeYear: '12.1', fiveYear: '14.0', rating: '3' },
          ]);
        }
        setIsLoading(false);
      }, 600);
    };

    fetchFunds();
  }, [updateFreq]);

  return (
    <div className="w-full">
      <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Tax Saving Funds (ELSS)</h1>
          <p className="text-sm text-slate-500">Save up to ₹46,800 in taxes under Section 80C with the lowest lock-in period of 3 years.</p>
        </div>
        <div className="flex bg-slate-200 rounded p-1">
          <button 
            onClick={() => setUpdateFreq('daily')}
            className={`px-4 py-1 text-sm font-bold rounded ${updateFreq === 'daily' ? 'bg-white text-orange-500 shadow-sm' : 'text-slate-600'}`}
          >
            Daily Updates
          </button>
          <button 
            onClick={() => setUpdateFreq('weekly')}
            className={`px-4 py-1 text-sm font-bold rounded ${updateFreq === 'weekly' ? 'bg-white text-orange-500 shadow-sm' : 'text-slate-600'}`}
          >
            Weekly Updates
          </button>
        </div>
      </div>

      <div className="mc-card overflow-hidden">
        <div className="bg-slate-100 px-4 py-3 font-bold text-slate-700 uppercase border-b-2 border-slate-200">
          Top Rated ELSS Funds ({updateFreq === 'daily' ? 'End of Day NAV' : 'End of Week NAV'})
        </div>
        <div className="overflow-x-auto">
        <table className="w-full text-sm text-left">
          <thead className="bg-slate-50 text-slate-500 border-b border-slate-200">
            <tr>
              <th className="px-4 py-3 font-medium uppercase tracking-wider">Fund Name</th>
              <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">NAV</th>
              <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">1Y Return</th>
              <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">3Y Return</th>
              <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">5Y Return</th>
              <th className="px-4 py-3 font-medium uppercase tracking-wider text-right">CRISIL Rating</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              <tr>
                <td colSpan="6" className="px-4 py-8 text-center text-slate-500 font-medium">
                  Fetching {updateFreq} ELSS NAV data...
                </td>
              </tr>
            ) : (
              elssFunds.map((fund, i) => (
                <FundRow key={i} {...fund} />
              ))
            )}
          </tbody>
        </table>
      </div>
      </div>
    </div>
  );
};

export default ELSS;
