package com.stockvision.backend.service;
import com.stockvision.backend.entity.News;
import com.stockvision.backend.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository repository;

    public List<News> findAll() {
        List<News> newsList = repository.findAll();
        if (newsList.isEmpty()) {
            return java.util.Arrays.asList(
                createMockNews(1L, "Sensex surges 500 points, Nifty above 22,200", "https://economictimes.indiatimes.com", "Economic Times", java.time.LocalDateTime.now().minusMinutes(15)),
                createMockNews(2L, "Reliance Industries announces new green energy gigafactory", "https://moneycontrol.com", "MoneyControl", java.time.LocalDateTime.now().minusHours(2)),
                createMockNews(3L, "TCS wins $1.5 billion contract from UK telecom giant", "https://livemint.com", "LiveMint", java.time.LocalDateTime.now().minusHours(5)),
                createMockNews(4L, "HDFC Bank Q4 profit beats estimates, asset quality improves", "https://bloombergquint.com", "Bloomberg", java.time.LocalDateTime.now().minusDays(1)),
                createMockNews(5L, "Foreign investors pump $3B into Indian equities this month", "https://business-standard.com", "Business Standard", java.time.LocalDateTime.now().minusDays(2))
            );
        }
        return newsList;
    }

    private News createMockNews(Long id, String title, String url, String source, java.time.LocalDateTime publishedAt) {
        News news = new News();
        news.setId(id);
        news.setTitle(title);
        news.setUrl(url);
        news.setSource(source);
        news.setPublishedAt(publishedAt);
        return news;
    }
}
