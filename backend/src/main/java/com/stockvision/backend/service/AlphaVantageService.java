package com.stockvision.backend.service;

import com.stockvision.backend.entity.Stock;
import com.stockvision.backend.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlphaVantageService {

    private final RestTemplate restTemplate;

    /**
     * Fetches live stock quote from Yahoo Finance unofficial API (no key required).
     * Falls back to a simulated price based on a seed if the API call fails.
     */
    @Cacheable(value = "stockQuotes", key = "#symbol", unless = "#result == null")
    public Map<String, Object> getFullQuote(String symbol) {
        log.info("Fetching live quote for: {}.NS", symbol);
        try {
            // Yahoo Finance API for NSE stocks (append .NS suffix)
            String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + ".NS?interval=1d&range=1d";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("chart")) {
                Map<String, Object> chart = (Map<String, Object>) response.get("chart");
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) chart.get("result");

                if (resultList != null && !resultList.isEmpty()) {
                    Map<String, Object> result = resultList.get(0);
                    Map<String, Object> meta = (Map<String, Object>) result.get("meta");

                    double regularMarketPrice = toDouble(meta.get("regularMarketPrice"));
                    double previousClose = toDouble(meta.get("chartPreviousClose"));
                    double dayHigh = toDouble(meta.get("regularMarketDayHigh"));
                    double dayLow = toDouble(meta.get("regularMarketDayLow"));
                    double fiftyTwoWeekHigh = toDouble(meta.get("fiftyTwoWeekHigh"));
                    double fiftyTwoWeekLow = toDouble(meta.get("fiftyTwoWeekLow"));

                    Map<String, Object> quoteData = new HashMap<>();
                    quoteData.put("currentPrice", BigDecimal.valueOf(regularMarketPrice).setScale(2, RoundingMode.HALF_UP));
                    quoteData.put("previousClose", BigDecimal.valueOf(previousClose).setScale(2, RoundingMode.HALF_UP));
                    quoteData.put("dayHigh", BigDecimal.valueOf(dayHigh).setScale(2, RoundingMode.HALF_UP));
                    quoteData.put("dayLow", BigDecimal.valueOf(dayLow).setScale(2, RoundingMode.HALF_UP));
                    quoteData.put("weekHigh52", BigDecimal.valueOf(fiftyTwoWeekHigh).setScale(2, RoundingMode.HALF_UP));
                    quoteData.put("weekLow52", BigDecimal.valueOf(fiftyTwoWeekLow).setScale(2, RoundingMode.HALF_UP));

                    log.info("Got live price for {}: ₹{}", symbol, regularMarketPrice);
                    return quoteData;
                }
            }
        } catch (Exception e) {
            log.warn("Yahoo Finance API failed for {}: {}. Using fallback.", symbol, e.getMessage());
        }
        return null;
    }

    @Cacheable(value = "stockQuotes", key = "'price_' + #symbol", unless = "#result == null")
    public BigDecimal getQuote(String symbol) {
        Map<String, Object> fullQuote = getFullQuote(symbol);
        if (fullQuote != null) {
            return (BigDecimal) fullQuote.get("currentPrice");
        }
        return null;
    }

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    private AlphaVantageService self;

    public Map<String, Map<String, Object>> getFullQuotes(List<String> symbols) {
        Map<String, Map<String, Object>> quotes = new HashMap<>();
        for (String symbol : symbols) {
            try {
                Map<String, Object> quote = self.getFullQuote(symbol);
                if (quote != null) {
                    quotes.put(symbol, quote);
                }
            } catch (Exception e) {
                log.warn("Skipping quote for {}: {}", symbol, e.getMessage());
            }
        }
        return quotes;
    }

    public Map<String, BigDecimal> getQuotes(List<String> symbols) {
        Map<String, BigDecimal> quotes = new HashMap<>();
        for (String symbol : symbols) {
            BigDecimal quote = self.getQuote(symbol);
            if (quote != null) {
                quotes.put(symbol, quote);
            }
        }
        return quotes;
    }

    private double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
