package com.project.stockanalysis.service;

import com.project.stockanalysis.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockScreenerService {

    private final AlphaVantageService alphaVantageService;

    // Well-known tickers to screen across
    private static final String[] UNIVERSE = {
        "AAPL","MSFT","GOOGL","AMZN","TSLA","META","NVDA","JPM","NFLX","BABA",
        "BRK.B","JNJ","V","PG","UNH","HD","MA","DIS","PYPL","ADBE",
        "CRM","INTC","CMCSA","VZ","PEP","ABT","T","NKE","MRK","XOM",
        "CVX","BAC","WMT","LLY","KO","AVGO","CSCO","ACN","MCD","TMO",
        "PFE","ABBV","COST","QCOM","TXN","DHR","UPS","HON","LOW","NEE"
    };

    public List<Map<String, Object>> screen(ScreenerFilterDto filter) {
        int limit = filter.getLimit() != null ? filter.getLimit() : 20;
        List<Map<String, Object>> results = new ArrayList<>();

        for (String symbol : UNIVERSE) {
            try {
                StockQuoteDto quote = alphaVantageService.getMockQuote(symbol);
                FundamentalDataDto fundamentals = alphaVantageService.getFundamentalData(symbol);
                CompanyProfileDto profile = alphaVantageService.getCompanyProfile(symbol);

                if (!passesFilter(quote, fundamentals, profile, filter)) continue;

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("symbol", symbol);
                row.put("companyName", profile.getName());
                row.put("sector", profile.getSector());
                row.put("price", quote.getPrice());
                row.put("change", quote.getChange());
                row.put("changePercent", quote.getChangePercent());
                row.put("marketCap", quote.getMarketCap());
                row.put("peRatio", round(fundamentals.getPeRatio()));
                row.put("eps", round(fundamentals.getEps()));
                row.put("dividendYield", round(fundamentals.getDividendYield()));
                row.put("returnOnEquity", round(fundamentals.getReturnOnEquity()));
                row.put("beta", round(fundamentals.getBeta()));
                row.put("volume", quote.getVolume());
                row.put("week52High", quote.getWeek52High());
                row.put("week52Low", quote.getWeek52Low());
                results.add(row);

                if (results.size() >= limit) break;
            } catch (Exception e) {
                log.warn("Error screening {}: {}", symbol, e.getMessage());
            }
        }

        return results;
    }

    private boolean passesFilter(StockQuoteDto quote, FundamentalDataDto fund,
                                  CompanyProfileDto profile, ScreenerFilterDto f) {
        if (quote.getPrice() == null) return false;

        if (f.getMinPrice() != null && quote.getPrice() < f.getMinPrice()) return false;
        if (f.getMaxPrice() != null && quote.getPrice() > f.getMaxPrice()) return false;

        if (f.getMinMarketCap() != null && quote.getMarketCap() != null
                && quote.getMarketCap() < f.getMinMarketCap()) return false;
        if (f.getMaxMarketCap() != null && quote.getMarketCap() != null
                && quote.getMarketCap() > f.getMaxMarketCap()) return false;

        if (f.getMinPE() != null && fund.getPeRatio() != null
                && fund.getPeRatio() < f.getMinPE()) return false;
        if (f.getMaxPE() != null && fund.getPeRatio() != null
                && fund.getPeRatio() > f.getMaxPE()) return false;

        if (f.getMinDividendYield() != null && fund.getDividendYield() != null
                && fund.getDividendYield() < f.getMinDividendYield()) return false;
        if (f.getMaxDividendYield() != null && fund.getDividendYield() != null
                && fund.getDividendYield() > f.getMaxDividendYield()) return false;

        if (f.getMinROE() != null && fund.getReturnOnEquity() != null
                && fund.getReturnOnEquity() < f.getMinROE()) return false;

        if (f.getSectors() != null && !f.getSectors().isEmpty()) {
            if (profile.getSector() == null) return false;
            boolean sectorMatch = f.getSectors().stream()
                    .anyMatch(s -> profile.getSector().equalsIgnoreCase(s));
            if (!sectorMatch) return false;
        }
        return true;
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
