import api from './axios';

/**
 * Fetch the authenticated user's portfolio.
 * Requires a valid JWT token in localStorage.
 * @returns {Promise<Object>} - Portfolio object with items
 */
export const getPortfolio = async () => {
  const response = await api.get('/api/portfolio');
  return response.data;
};

/**
 * Fetch the authenticated user's watchlist.
 * Requires a valid JWT token in localStorage.
 * @returns {Promise<Object>} - Watchlist object with stocks
 */
export const getWatchlist = async () => {
  const response = await api.get('/api/watchlist');
  return response.data;
};
