import React, { Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';

import PrivateRoute from './components/PrivateRoute';

import PageTitleUpdater from './components/PageTitleUpdater';

// Implement route-level code splitting
const Home = React.lazy(() => import('./pages/Home'));
const Login = React.lazy(() => import('./pages/Login'));
const Register = React.lazy(() => import('./pages/Register'));
const Dashboard = React.lazy(() => import('./pages/Dashboard'));
const Watchlist = React.lazy(() => import('./pages/Watchlist'));
const Portfolio = React.lazy(() => import('./pages/Portfolio'));
const News = React.lazy(() => import('./pages/News'));
const Indices = React.lazy(() => import('./pages/Indices'));
const GlobalMarkets = React.lazy(() => import('./pages/GlobalMarkets'));
const StockAction = React.lazy(() => import('./pages/StockAction'));
const Earnings = React.lazy(() => import('./pages/Earnings'));
const MutualFunds = React.lazy(() => import('./pages/MutualFunds'));
const SIPCalculator = React.lazy(() => import('./pages/SIPCalculator'));
const NFOs = React.lazy(() => import('./pages/NFOs'));
const ELSS = React.lazy(() => import('./pages/ELSS'));
const StockDetail = React.lazy(() => import('./pages/StockDetail'));

function App() {
  return (
    <BrowserRouter>
      <PageTitleUpdater />
      <Suspense fallback={<div className="w-full h-screen flex items-center justify-center text-orange-500 font-bold">Loading MarketLens Core...</div>}>
        <Routes>
          <Route path="/" element={<Layout />}>
            {/* Public Routes */}
            <Route index element={<Home />} />
            <Route path="login" element={<Login />} />
            <Route path="register" element={<Register />} />
            
            {/* Protected Routes */}
            <Route element={<PrivateRoute />}>
              <Route path="dashboard" element={<Dashboard />} />
              <Route path="stock/:ticker" element={<StockDetail />} />
              <Route path="news" element={<News />} />
              <Route path="indices" element={<Indices />} />
              <Route path="global-markets" element={<GlobalMarkets />} />
              <Route path="stock-action" element={<StockAction />} />
              <Route path="earnings" element={<Earnings />} />
              <Route path="mutual-funds" element={<MutualFunds />} />
              <Route path="sip-calculator" element={<SIPCalculator />} />
              <Route path="nfos" element={<NFOs />} />
              <Route path="elss" element={<ELSS />} />
              <Route path="watchlist" element={<Watchlist />} />
              <Route path="portfolio" element={<Portfolio />} />
            </Route>
          </Route>
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
}

export default App;
