import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

const PageTitleUpdater = () => {
  const location = useLocation();

  useEffect(() => {
    const path = location.pathname;
    let pageName = 'Premium Market Analysis';
    
    if (path.includes('/dashboard')) pageName = 'Dashboard';
    else if (path.includes('/portfolio')) pageName = 'My Portfolio';
    else if (path.includes('/watchlist')) pageName = 'Watchlist';
    else if (path.includes('/news')) pageName = 'Market News';
    else if (path.includes('/indices')) pageName = 'Indian Indices';
    else if (path.includes('/earnings')) pageName = 'Corporate Earnings';
    else if (path.includes('/login')) pageName = 'Login';
    else if (path.includes('/register')) pageName = 'Register';

    document.title = `MarketLens | ${pageName}`;
  }, [location]);

  return null;
};

export default PageTitleUpdater;
