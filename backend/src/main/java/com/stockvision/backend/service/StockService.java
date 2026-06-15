package com.stockvision.backend.service;
import com.stockvision.backend.entity.Stock;
import com.stockvision.backend.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {
    private final StockRepository repository;
    private final AlphaVantageService alphaVantageService;

    /**
     * Fetches all stocks from DB and enriches them with live API prices.
     */
    public List<Stock> findAll() {
        List<Stock> stocks = repository.findAll();
        List<String> symbols = stocks.stream().map(Stock::getSymbol).collect(Collectors.toList());
        Map<String, Map<String, Object>> quotes = alphaVantageService.getFullQuotes(symbols);

        for (Stock stock : stocks) {
            Map<String, Object> quote = quotes.get(stock.getSymbol());
            if (quote != null) {
                stock.setCurrentPrice((BigDecimal) quote.get("currentPrice"));
                stock.setPreviousClose((BigDecimal) quote.get("previousClose"));
                stock.setDayHigh((BigDecimal) quote.get("dayHigh"));
                stock.setDayLow((BigDecimal) quote.get("dayLow"));
                stock.setWeekHigh52((BigDecimal) quote.get("weekHigh52"));
                stock.setWeekLow52((BigDecimal) quote.get("weekLow52"));
                stock.setLastUpdated(LocalDateTime.now());
            }
        }

        return repository.saveAll(stocks);
    }

    /**
     * Fetches a single live quote and updates the DB.
     */
    public BigDecimal getQuote(String symbol) {
        Stock stock = repository.findBySymbol(symbol).orElse(null);
        Map<String, Object> fullQuote = alphaVantageService.getFullQuote(symbol);

        if (stock != null && fullQuote != null) {
            stock.setCurrentPrice((BigDecimal) fullQuote.get("currentPrice"));
            stock.setPreviousClose((BigDecimal) fullQuote.get("previousClose"));
            stock.setDayHigh((BigDecimal) fullQuote.get("dayHigh"));
            stock.setDayLow((BigDecimal) fullQuote.get("dayLow"));
            stock.setWeekHigh52((BigDecimal) fullQuote.get("weekHigh52"));
            stock.setWeekLow52((BigDecimal) fullQuote.get("weekLow52"));
            stock.setLastUpdated(LocalDateTime.now());
            repository.save(stock);
            return (BigDecimal) fullQuote.get("currentPrice");
        }

        if (fullQuote != null) {
            return (BigDecimal) fullQuote.get("currentPrice");
        }

        return stock != null ? stock.getCurrentPrice() : null;
    }

    public List<Stock> searchStocks(String query) {
        return repository.findBySymbolContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(query, query);
    }
}
