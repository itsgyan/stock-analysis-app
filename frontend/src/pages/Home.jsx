import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Home = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // Automatically route to the dashboard on app load
    navigate('/dashboard');
  }, [navigate]);

  return (
    <div className="w-full h-screen flex items-center justify-center">
      <div className="text-slate-500 font-bold">Redirecting to Market Dashboard...</div>
    </div>
  );
};

export default Home;
