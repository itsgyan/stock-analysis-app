import { useState, useEffect } from 'react';
import api from '../api/axios';

const formatRelativeTime = (dateStr) => {
  if (!dateStr) return '';
  const diff = Date.now() - new Date(dateStr).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return 'Just now';
  if (mins < 60) return `${mins} min${mins > 1 ? 's' : ''} ago`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours} hour${hours > 1 ? 's' : ''} ago`;
  const days = Math.floor(hours / 24);
  return `${days} day${days > 1 ? 's' : ''} ago`;
};

const NewsCard = ({ source, time, headline, summary, url }) => (
  <a href={url} target="_blank" rel="noopener noreferrer" className="mc-card p-4 flex gap-6 hover:bg-slate-50 cursor-pointer transition-colors mb-4 block">
    <div className="w-48 h-32 bg-gradient-to-br from-slate-800 to-slate-600 shrink-0 overflow-hidden rounded flex items-center justify-center">
      <span className="text-white text-3xl font-black opacity-20">{source?.charAt(0) || 'N'}</span>
    </div>
    <div className="flex flex-col justify-between">
      <div>
        <div className="flex items-center gap-2 text-xs font-bold mb-2">
          <span className="text-orange-600">{source || 'NEWS'}</span>
          <span className="text-slate-300">|</span>
          <span className="text-slate-500">{time}</span>
        </div>
        <h2 className="text-lg font-bold text-slate-800 hover:text-blue-700 leading-snug mb-2">
          {headline}
        </h2>
        {summary && (
          <p className="text-sm text-slate-600 leading-relaxed">{summary}</p>
        )}
      </div>
    </div>
  </a>
);

const News = () => {
  const [newsItems, setNewsItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchNews = async () => {
      try {
        const res = await api.get('/api/news');
        setNewsItems(res.data || []);
      } catch (err) {
        console.error('Failed to fetch news:', err);
        setNewsItems([]);
      } finally {
        setLoading(false);
      }
    };

    fetchNews();
  }, []);

  if (loading) {
    return (
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <div className="lg:col-span-3 space-y-4">
          <div className="flex items-center justify-between border-b-2 border-slate-800 pb-2 mb-6">
            <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Market News</h1>
          </div>
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="mc-card p-4 flex gap-6 animate-pulse mb-4">
              <div className="w-48 h-32 bg-slate-200 shrink-0 rounded"></div>
              <div className="flex-1 space-y-3">
                <div className="bg-slate-200 h-3 rounded w-32"></div>
                <div className="bg-slate-200 h-6 rounded w-full"></div>
                <div className="bg-slate-200 h-4 rounded w-3/4"></div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
      {/* Main News Feed */}
      <div className="lg:col-span-3">
        <div className="flex items-center justify-between border-b-2 border-slate-800 pb-2 mb-6">
          <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">Market News</h1>
          <div className="flex gap-4 text-sm font-bold text-slate-500">
            <span className="text-orange-500 cursor-pointer">Top Stories</span>
            <span className="cursor-pointer hover:text-orange-500">Earnings</span>
            <span className="cursor-pointer hover:text-orange-500">Economy</span>
            <span className="cursor-pointer hover:text-orange-500">Global</span>
          </div>
        </div>

        {newsItems.length > 0 ? (
          newsItems.map((news, idx) => (
            <NewsCard 
              key={news.id || idx}
              source={news.source}
              time={formatRelativeTime(news.publishedAt)}
              headline={news.title}
              summary=""
              url={news.url}
            />
          ))
        ) : (
          <div className="mc-card p-8 text-center">
            <p className="text-slate-500 font-bold text-lg mb-2">No news articles available</p>
            <p className="text-slate-400 text-sm">News will appear here once the backend populates the news table.</p>
          </div>
        )}
      </div>

      {/* Sidebar */}
      <div className="lg:col-span-1">
        <div className="mc-card">
          <div className="px-4 py-2 font-bold text-sm border-b border-slate-200 uppercase text-white bg-slate-800">
            Most Read
          </div>
          <div className="flex flex-col divide-y divide-slate-200">
            {newsItems.length > 0 ? (
              newsItems.slice(0, 4).map((news, i) => (
                <a key={news.id || i} href={news.url} target="_blank" rel="noopener noreferrer" className="p-4 hover:bg-slate-50 cursor-pointer block">
                  <span className="text-2xl font-black text-slate-200 float-left mr-3 leading-none">{i + 1}</span>
                  <p className="text-sm font-medium text-slate-700 hover:text-orange-500 transition-colors">
                    {news.title}
                  </p>
                </a>
              ))
            ) : (
              <div className="p-4 text-sm text-slate-400 text-center">No articles yet</div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default News;
