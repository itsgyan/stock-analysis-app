import React, { useState, useEffect } from 'react';

const NFOs = () => {
  const [nfos, setNfos] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Simulating dynamic daily fetch for NFOs from the Spring Boot Backend
    // In production, the backend runs a daily scheduled task to scrape AMFI/SEBI for new NFOs
    const fetchDailyNFOs = async () => {
      setIsLoading(true);
      try {
        // Simulating API call: await fetch('http://localhost:8080/api/mutual-funds/nfos/today')
        setTimeout(() => {
          setNfos([
            { name: 'SBI Energy Opportunities Fund', type: 'Equity - Sectoral', openDate: '15-Feb-2026', closeDate: '28-Feb-2026', minInvest: '₹5,000', url: 'https://www.sbimf.com/' },
            { name: 'HDFC Defence Fund', type: 'Equity - Thematic', openDate: '20-Feb-2026', closeDate: '05-Mar-2026', minInvest: '₹1,000', url: 'https://www.hdfcfund.com/' },
            { name: 'ICICI Pru Auto Index Fund', type: 'Index Fund', openDate: '22-Feb-2026', closeDate: '08-Mar-2026', minInvest: '₹100', url: 'https://www.icicipruamc.com/' },
            { name: 'Nippon India AI & Tech Fund', type: 'Equity - Thematic', openDate: '01-Mar-2026', closeDate: '15-Mar-2026', minInvest: '₹500', url: 'https://mf.nipponindiaim.com/' },
          ]);
          setIsLoading(false);
        }, 700);
      } catch (err) {
        console.error("Failed to fetch daily NFOs");
        setIsLoading(false);
      }
    };
    
    fetchDailyNFOs();
  }, []);

  return (
    <div className="w-full">
      <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">New Fund Offers (NFO)</h1>
          <p className="text-sm text-slate-500">Upcoming and currently open mutual fund schemes</p>
        </div>
      </div>

      <div className="mc-card overflow-hidden">
        <div className="overflow-x-auto">
        <table className="w-full text-sm text-left">
          <thead className="bg-slate-100 text-slate-600 border-b-2 border-slate-200">
            <tr>
              <th className="px-4 py-3 font-bold uppercase tracking-wider">Fund Name</th>
              <th className="px-4 py-3 font-bold uppercase tracking-wider">Category</th>
              <th className="px-4 py-3 font-bold uppercase tracking-wider">Open Date</th>
              <th className="px-4 py-3 font-bold uppercase tracking-wider">Close Date</th>
              <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Min. Investment</th>
              <th className="px-4 py-3 font-bold uppercase tracking-wider text-right">Action</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              <tr>
                <td colSpan="6" className="px-4 py-8 text-center text-slate-500 font-medium">
                  Fetching latest daily NFO data from AMC portals...
                </td>
              </tr>
            ) : (
              nfos.map((nfo, i) => (
                <tr key={i} className="border-b border-slate-200 hover:bg-slate-50 transition-colors">
                  <td className="px-4 py-3 font-bold text-blue-700">{nfo.name}</td>
                  <td className="px-4 py-3 text-slate-600">{nfo.type}</td>
                  <td className="px-4 py-3 font-medium text-slate-800">{nfo.openDate}</td>
                  <td className="px-4 py-3 font-medium text-slate-800">{nfo.closeDate}</td>
                  <td className="px-4 py-3 font-bold text-right">{nfo.minInvest}</td>
                  <td className="px-4 py-3 text-right">
                    <button 
                      onClick={() => window.open(nfo.url, '_blank', 'noopener,noreferrer')}
                      className="bg-orange-500 hover:bg-orange-600 text-white font-bold py-1 px-3 rounded text-xs transition-colors"
                    >
                      Apply Now
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
      </div>
    </div>
  );
};

export default NFOs;
