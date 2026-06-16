import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../api/authApi';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    if (!username || !password) {
      setError('Please enter both username and password.');
      setIsLoading(false);
      return;
    }

    try {
      await login(username, password);
      // Force full re-render so Layout picks up the new auth state
      window.location.href = '/dashboard';
    } catch (err) {
      const message =
        err.response?.data?.message ||
        err.response?.data?.error ||
        err.message ||
        'Login failed. Please check your credentials.';
      setError(message);
      setIsLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[70vh]">
      <div className="mc-card w-full max-w-md p-8">
        <div className="text-center mb-8 flex flex-col items-center">
          <img src="/bear-bull.png" alt="MarketLens Logo" className="w-16 h-16 object-cover rounded-full shadow-lg border-2 border-orange-500 bg-slate-100 mb-4" />
          <h2 className="text-2xl font-bold text-slate-800">
            Login to Market<span className="text-orange-500">Lens</span>
          </h2>
          <p className="text-sm text-slate-500 mt-2">Access your portfolio and custom watchlists</p>
        </div>

        {error && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-600 text-sm rounded">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Username</label>
            <input 
              type="text" 
              required
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full px-4 py-2 border border-slate-300 rounded focus:outline-none focus:border-orange-500 focus:ring-1 focus:ring-orange-500"
              placeholder="Enter your username"
            />
          </div>
          
          <div>
            <div className="flex justify-between items-center mb-1">
              <label className="block text-sm font-bold text-slate-700">Password</label>
              <span className="text-xs text-blue-600 hover:underline cursor-pointer">Forgot Password?</span>
            </div>
            <input 
              type="password" 
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-2 border border-slate-300 rounded focus:outline-none focus:border-orange-500 focus:ring-1 focus:ring-orange-500"
              placeholder="Enter your password"
            />
          </div>

          <button 
            type="submit" 
            disabled={isLoading}
            className="w-full bg-orange-500 hover:bg-orange-600 text-white font-bold py-2.5 px-4 rounded transition-colors disabled:bg-orange-300"
          >
            {isLoading ? 'Authenticating...' : 'Secure Login'}
          </button>
        </form>

        <div className="mt-6 text-center text-sm text-slate-600">
          Don't have an account? <span className="text-blue-600 font-bold hover:underline cursor-pointer" onClick={() => navigate('/register')}>Register Here</span>
        </div>
      </div>
    </div>
  );
};

export default Login;
