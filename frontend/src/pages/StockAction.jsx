const StockAction = () => {
  const volumes = [
    { name: 'Vodafone Idea', vol: '45.2 Cr', price: '12.45', pos: true },
    { name: 'Yes Bank', vol: '32.1 Cr', price: '24.10', pos: false },
    { name: 'Suzlon Energy', vol: '18.5 Cr', price: '41.20', pos: true },
    { name: 'Zomato', vol: '15.2 Cr', price: '190.50', pos: true },
    { name: 'Tata Motors', vol: '8.4 Cr', price: '1,012.45', pos: true },
  ];

  return (
    <div className="w-full">
      <div className="flex justify-between items-end mb-6 border-b-2 border-slate-800 pb-2">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Stock Action</h1>
          <p className="text-sm text-slate-500">Most active stocks, volume shockers, and 52-week highs/lows</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="mc-card overflow-hidden">
          <div className="bg-slate-100 px-4 py-3 font-bold text-slate-700 uppercase border-b-2 border-slate-200">
            Most Active By Volume
          </div>
          <div className="overflow-x-auto">
        <table className="w-full text-sm text-left">
            <tbody>
              {volumes.map((stock, i) => (
                <tr key={i} className="border-b border-slate-100 hover:bg-slate-50">
                  <td className="px-4 py-3 font-medium text-blue-700">{stock.name}</td>
                  <td className="px-4 py-3 text-right font-bold text-slate-600">{stock.vol}</td>
                  <td className={`px-4 py-3 text-right font-bold ${stock.pos ? 'text-[#138150]' : 'text-[#d52b2b]'}`}>{stock.price}</td>
                </tr>
              ))}
            </tbody>
          </table>
      </div>
        </div>

        <div className="mc-card overflow-hidden">
          <div className="bg-slate-100 px-4 py-3 font-bold text-slate-700 uppercase border-b-2 border-slate-200">
            52-Week Highs
          </div>
          <div className="p-4 flex items-center justify-center h-48 text-slate-500 bg-slate-50">
            No stocks hitting 52-week highs in current session.
          </div>
        </div>
      </div>
    </div>
  );
};

export default StockAction;
