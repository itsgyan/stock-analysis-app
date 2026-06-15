import api from './axios';

/**
 * Fetch all stocks from the backend.
 * @returns {Promise<Array<{ id, symbol, companyName, currentPrice, lastUpdated }>>}
 */
export const getAllStocks = async () => {
  const response = await api.get('/api/stocks');
  return response.data;
};

/**
 * Fetch a real-time quote for a single stock symbol.
 * @param {string} symbol - Stock ticker symbol (e.g. "RELIANCE")
 * @returns {Promise<number>} - Current price as a number
 */
export const getStockQuote = async (symbol) => {
  const response = await api.get(`/api/stocks/${symbol}/quote`);
  return response.data;
};

/**
 * Search stocks client-side by filtering the full stock list.
 * Matches against both symbol and companyName.
 * @param {string} query - Search query string
 * @returns {Promise<Array>} - Filtered list of matching stocks
 */
export const searchStocks = async (query) => {
  const stocks = await getAllStocks();
  if (!query || query.trim() === '') return stocks;

  const lowerQuery = query.toLowerCase();
  return stocks.filter(
    (stock) =>
      stock.symbol.toLowerCase().includes(lowerQuery) ||
      stock.companyName.toLowerCase().includes(lowerQuery)
  );
};

/**
 * Search stocks using the dedicated backend search endpoint.
 * Preferred over searchStocks() as it avoids fetching the full list.
 * @param {string} query - Search query string
 * @returns {Promise<Array>} - Matching stocks from backend
 */
export const searchStocksApi = async (query) => {
  const res = await api.get(`/api/stocks/search?q=${encodeURIComponent(query)}`);
  return res.data;
};
