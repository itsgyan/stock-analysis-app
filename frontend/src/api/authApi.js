import api from './axios';

/**
 * Authenticate a user and store the JWT token in localStorage.
 * @param {string} username
 * @param {string} password
 * @returns {Promise<{ token: string, type: string }>}
 */
export const login = async (username, password) => {
  const response = await api.post('/api/auth/signin', { username, password });
  const { token } = response.data;

  if (token) {
    localStorage.setItem('token', token);
    localStorage.setItem('user', username);
  }

  return response.data;
};

/**
 * Register a new user account.
 * @param {string} username
 * @param {string} email
 * @param {string} password
 * @returns {Promise<{ message: string }>}
 */
export const signup = async (username, email, password) => {
  const response = await api.post('/api/auth/signup', { username, email, password });
  return response.data;
};
