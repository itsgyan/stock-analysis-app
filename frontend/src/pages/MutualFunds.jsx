import { Link } from 'react-router-dom';

const FundRow = ({ name, category, nav, oneYear, threeYear, fiveYear, rating }) => (
  <tr className="border-b border-slate-200 hover:bg-slate-50 cursor-pointer">
    <td className="px-4 py-3">
      <p className="font-bold text-blue-700">{name}</p>
      <p className="text-xs text-slate-500">{category}</p>
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

const MutualFunds = () => {
  const trendingFunds = [
    { name: 'Parag Parikh Flexi Cap Fund', category: 'Equity - Flexi Cap', nav: '74.25', oneYear: '38.4', threeYear: '21.5', fiveYear: '24.2', rating: '5' },
    { name: 'Nippon India Small Cap Fund', category: 'Equity - Small Cap', nav: '156.80', oneYear: '52.1', threeYear: '35.4', fiveYear: '28.7', rating: '4' },
    { name: 'Quant Active Fund', category: 'Equity - Multi Cap', nav: '620.45', oneYear: '45.8', threeYear: '28.9', fiveYear: '26.4', rating: '5' },
    { name: 'SBI Equity Hybrid Fund', category: 'Hybrid - Aggressive', nav: '245.10', oneYear: '22.4', threeYear: '15.8', fiveYear: '14.5', rating: '4' },
    { name: 'HDFC Mid-Cap Opportunities', category: 'Equity - Mid Cap', nav: '168.30', oneYear: '48.5', threeYear: '26.2', fiveYear: '22.1', rating: '4' },
    { name: 'ICICI Pru Technology Fund', category: 'Equity - Sectoral', nav: '185.20', oneYear: '28.4', threeYear: '18.5', fiveYear: '25.6', rating: '3' },
  ];

  return (
    <div className="w-full">
      <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Trending Mutual Funds</h1>
          <p className="text-sm text-slate-500">Top performing mutual funds across categories based on historical returns</p>
        </div>
        <button className="bg-orange-500 hover:bg-orange-600 text-white font-bold py-2 px-4 rounded text-sm transition-colors">
          Explore All Funds
        </button>
      </div>

      <div className="mc-card overflow-hidden">
        <div className="bg-slate-100 px-4 py-3 font-bold text-slate-700 uppercase border-b-2 border-slate-200">
          Top Rated Funds to Invest
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
            {trendingFunds.map((fund, i) => (
              <FundRow key={i} {...fund} />
            ))}
          </tbody>
        </table>
      </div>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
        <div className="mc-card p-4">
          <h3 className="font-bold text-slate-800 border-b border-slate-200 pb-2 mb-2">SIP Calculator</h3>
          <p className="text-sm text-slate-600 mb-4">Calculate your wealth creation over time with systematic investments.</p>
          <Link to="/sip-calculator" className="w-full border border-orange-500 text-orange-500 font-bold py-2 rounded hover:bg-orange-50 transition-colors block text-center">Calculate Now</Link>
        </div>
        <div className="mc-card p-4">
          <h3 className="font-bold text-slate-800 border-b border-slate-200 pb-2 mb-2">New Fund Offers (NFO)</h3>
          <p className="text-sm text-slate-600 mb-4">Discover the latest mutual fund schemes launching this week.</p>
          <Link to="/nfos" className="w-full border border-orange-500 text-orange-500 font-bold py-2 rounded hover:bg-orange-50 transition-colors block text-center">View NFOs</Link>
        </div>
        <div className="mc-card p-4">
          <h3 className="font-bold text-slate-800 border-b border-slate-200 pb-2 mb-2">Tax Saving Funds (ELSS)</h3>
          <p className="text-sm text-slate-600 mb-4">Save up to ₹46,800 in taxes under section 80C.</p>
          <Link to="/elss" className="w-full border border-orange-500 text-orange-500 font-bold py-2 rounded hover:bg-orange-50 transition-colors block text-center">Invest to Save Tax</Link>
        </div>
      </div>
    </div>
  );
};

export default MutualFunds;
