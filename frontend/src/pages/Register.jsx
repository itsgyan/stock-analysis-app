import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { signup } from '../api/authApi';

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    setSuccess('');

    try {
      const data = await signup(formData.username, formData.email, formData.password);
      setSuccess(data.message || 'Registration successful! Redirecting to login...');
      setTimeout(() => navigate('/login'), 1500);
    } catch (err) {
      const message =
        err.response?.data?.message ||
        err.response?.data?.error ||
        err.message ||
        'Registration failed. Please try again.';
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[70vh]">
      <div className="mc-card w-full max-w-md p-8">
        <div className="text-center mb-8 flex flex-col items-center">
          <img src="/bear-bull.png" alt="MarketLens Logo" className="w-16 h-16 object-cover rounded-full shadow-lg border-2 border-orange-500 bg-slate-100 mb-4" />
          <h2 className="text-2xl font-bold text-slate-800">
            Create an Account
          </h2>
          <p className="text-sm text-slate-500 mt-2">Join MarketLens today</p>
        </div>

        {error && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-600 text-sm rounded">
            {error}
          </div>
        )}

        {success && (
          <div className="mb-4 p-3 bg-green-50 border border-green-200 text-green-700 text-sm rounded">
            {success}
          </div>
        )}

        <form onSubmit={handleRegister} className="space-y-4">
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Username</label>
            <input 
              type="text" name="username" required
              value={formData.username}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-slate-300 rounded focus:outline-none focus:border-orange-500 focus:ring-1 focus:ring-orange-500"
              placeholder="Choose a username"
            />
          </div>

          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Email Address</label>
            <input 
              type="email" name="email" required
              value={formData.email}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-slate-300 rounded focus:outline-none focus:border-orange-500 focus:ring-1 focus:ring-orange-500"
              placeholder="Enter your email"
            />
          </div>
          
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Password</label>
            <input 
              type="password" name="password" required
              value={formData.password}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-slate-300 rounded focus:outline-none focus:border-orange-500 focus:ring-1 focus:ring-orange-500"
              placeholder="Create a password"
            />
          </div>

          <button 
            type="submit" 
            disabled={isLoading}
            className="w-full bg-orange-500 hover:bg-orange-600 text-white font-bold py-2.5 px-4 rounded transition-colors disabled:bg-orange-300 mt-2"
          >
            {isLoading ? 'Creating Account...' : 'Register'}
          </button>
        </form>

        <div className="mt-6 text-center text-sm text-slate-600">
          Already have an account? <span className="text-blue-600 font-bold hover:underline cursor-pointer" onClick={() => navigate('/login')}>Login Here</span>
        </div>
      </div>
    </div>
  );
};

export default Register;
