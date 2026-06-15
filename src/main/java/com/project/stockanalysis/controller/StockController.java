package com.project.stockanalysis.controller;

import com.project.stockanalysis.dto.*;
import com.project.stockanalysis.service.AlphaVantageService;
import com.project.stockanalysis.service.TechnicalAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final AlphaVantageService alphaVantageService;
    private final TechnicalAnalysisService technicalAnalysisService;

    /** Search for stock symbols */
    @GetMapping("/search")
    public ResponseEntity<List<StockSearchResultDto>> search(@RequestParam String query) {
        return ResponseEntity.ok(alphaVantageService.searchSymbols(query));
    }

    /** Get real-time quote */
    @GetMapping("/{symbol}/quote")
    public ResponseEntity<StockQuoteDto> getQuote(@PathVariable String symbol) {
        return ResponseEntity.ok(alphaVantageService.getGlobalQuote(symbol.toUpperCase()));
    }

    /** Get company profile / overview */
    @GetMapping("/{symbol}/profile")
    public ResponseEntity<CompanyProfileDto> getProfile(@PathVariable String symbol) {
        return ResponseEntity.ok(alphaVantageService.getCompanyProfile(symbol.toUpperCase()));
    }

    /** Get fundamental financial data */
    @GetMapping("/{symbol}/fundamentals")
    public ResponseEntity<FundamentalDataDto> getFundamentals(@PathVariable String symbol) {
        return ResponseEntity.ok(alphaVantageService.getFundamentalData(symbol.toUpperCase()));
    }

    /** Get daily time series for charting */
    @GetMapping("/{symbol}/history")
    public ResponseEntity<Map<String, Object>> getHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "252") int days) {
        return ResponseEntity.ok(alphaVantageService.getDailyTimeSeries(symbol.toUpperCase(), days));
    }

    /** Get technical analysis (MA, RSI, MACD, BB) */
    @GetMapping("/{symbol}/technical")
    public ResponseEntity<TechnicalAnalysisDto> getTechnical(@PathVariable String symbol) {
        return ResponseEntity.ok(technicalAnalysisService.analyze(symbol.toUpperCase()));
    }

    /** Get income statement */
    @GetMapping("/{symbol}/financials/income")
    public ResponseEntity<FinancialStatementDto> getIncomeStatement(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "annual") String period) {
        return ResponseEntity.ok(alphaVantageService.getIncomeStatement(symbol.toUpperCase(), period));
    }

    /** Get balance sheet */
    @GetMapping("/{symbol}/financials/balance")
    public ResponseEntity<FinancialStatementDto> getBalanceSheet(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "annual") String period) {
        return ResponseEntity.ok(alphaVantageService.getBalanceSheet(symbol.toUpperCase(), period));
    }

    /** Get cash flow statement */
    @GetMapping("/{symbol}/financials/cashflow")
    public ResponseEntity<FinancialStatementDto> getCashFlow(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "annual") String period) {
        return ResponseEntity.ok(alphaVantageService.getCashFlow(symbol.toUpperCase(), period));
    }

    /** Get news & sentiment */
    @GetMapping("/{symbol}/news")
    public ResponseEntity<List<NewsItemDto>> getNews(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(alphaVantageService.getNewsSentiment(symbol.toUpperCase(), limit));
    }

    /** Get market-wide news */
    @GetMapping("/news")
    public ResponseEntity<List<NewsItemDto>> getMarketNews(
            @RequestParam(defaultValue = "10") int limit) {
        // Fetch news for major Indian companies to get Indian market news
        return ResponseEntity.ok(alphaVantageService.getNewsSentiment("RELIANCE.BSE,TCS.BSE,INFY.BSE,HDFCBANK.BSE,SBIN.BSE", limit));
    }

    /** Multi-quote for dashboard */
    @GetMapping("/quotes")
    public ResponseEntity<List<StockQuoteDto>> getMultipleQuotes(
            @RequestParam List<String> symbols) {
        List<StockQuoteDto> quotes = symbols.stream()
                .map(s -> alphaVantageService.getGlobalQuote(s.toUpperCase()))
                .toList();
        return ResponseEntity.ok(quotes);
    }

    /** Market movers - top gainers / losers mock */
    @GetMapping("/movers")
    public ResponseEntity<Map<String, Object>> getMarketMovers() {
        String[] gainers = {"NVDA", "META", "TSLA", "AMZN", "NFLX"};
        String[] losers = {"XOM", "CVX", "T", "VZ", "BA"};
        List<StockQuoteDto> gainerList = java.util.Arrays.stream(gainers)
                .map(s -> alphaVantageService.getMockQuote(s)).toList();
        List<StockQuoteDto> loserList = java.util.Arrays.stream(losers)
                .map(s -> alphaVantageService.getMockQuote(s)).toList();
        return ResponseEntity.ok(Map.of("gainers", gainerList, "losers", loserList));
    }
}
