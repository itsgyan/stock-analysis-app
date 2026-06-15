package com.project.stockanalysis.controller;

import com.project.stockanalysis.dto.NewsItemDto;
import com.project.stockanalysis.service.AlphaVantageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for market news and sentiment.
 * Base path: /api/news
 */
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final AlphaVantageService alphaVantageService;

    /**
     * GET /api/news?symbols=AAPL,MSFT&limit=20
     * Returns latest news with sentiment for given tickers.
     */
    @GetMapping
    public ResponseEntity<List<NewsItemDto>> getNews(
            @RequestParam(defaultValue = "AAPL,MSFT,GOOGL,AMZN,TSLA") String symbols,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(alphaVantageService.getNewsSentiment(symbols, limit));
    }

    /**
     * GET /api/news/AAPL?limit=10
     * Returns news specific to one symbol.
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<List<NewsItemDto>> getNewsBySymbol(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(alphaVantageService.getNewsSentiment(symbol.toUpperCase(), limit));
    }
}
