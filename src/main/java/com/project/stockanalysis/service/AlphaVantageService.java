package com.project.stockanalysis.service;

import com.project.stockanalysis.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlphaVantageService {

    private final RestTemplate restTemplate;

    @Value("${alphavantage.api.key}")
    private String apiKey;

    @Value("${alphavantage.api.base-url}")
    private String baseUrl;

    @Value("${alphavantage.live-enabled:false}")
    private boolean liveEnabled;

    // ─── Symbol Search ────────────────────────────────────────────────────────
    public List<StockSearchResultDto> searchSymbols(String query) {
        if (!liveEnabled) {
            return getMockSearchResults(query);
        }

        try {
            String url = buildUrl("SYMBOL_SEARCH", null) + "&keywords=" + query;
            Map<String, Object> response = callApi(url);
            @SuppressWarnings("unchecked")
            List<Map<String, String>> matches = (List<Map<String, String>>) response.get("bestMatches");
            if (matches == null) return getMockSearchResults(query);
            return matches.stream().map(m -> StockSearchResultDto.builder()
                    .symbol(m.getOrDefault("1. symbol", ""))
                    .name(m.getOrDefault("2. name", ""))
                    .type(m.getOrDefault("3. type", ""))
                    .region(m.getOrDefault("4. region", ""))
                    .currency(m.getOrDefault("8. currency", ""))
                    .exchange(m.getOrDefault("4. region", ""))
                    .matchScore(m.getOrDefault("9. matchScore", ""))
                    .build()).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching symbols for query: {}", query, e);
            return getMockSearchResults(query);
        }
    }

    // ─── Global Quote ─────────────────────────────────────────────────────────
    public StockQuoteDto getGlobalQuote(String symbol) {
        if (!liveEnabled) {
            return getMockQuote(symbol);
        }

        try {
            String url = buildUrl("GLOBAL_QUOTE", symbol);
            Map<String, Object> response = callApi(url);
            @SuppressWarnings("unchecked")
            Map<String, String> quote = (Map<String, String>) response.get("Global Quote");
            if (quote == null || quote.isEmpty()) return getMockQuote(symbol);

            String changePctRaw = quote.getOrDefault("10. change percent", "0%").replace("%", "");
            double price = parseDouble(quote.get("05. price"));
            double change = parseDouble(quote.get("09. change"));
            double changePct = parseDouble(changePctRaw);

            return StockQuoteDto.builder()
                    .symbol(quote.getOrDefault("01. symbol", symbol))
                    .price(price)
                    .change(change)
                    .changePercent(changePct)
                    .open(parseDouble(quote.get("02. open")))
                    .high(parseDouble(quote.get("03. high")))
                    .low(parseDouble(quote.get("04. low")))
                    .previousClose(parseDouble(quote.get("08. previous close")))
                    .volume(parseLong(quote.get("06. volume")))
                    .lastUpdated(quote.getOrDefault("07. latest trading day", ""))
                    .currency("USD")
                    .exchange("NASDAQ")
                    .marketOpen(change != 0)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching quote for {}", symbol, e);
            return getMockQuote(symbol);
        }
    }

    // ─── Company Overview ─────────────────────────────────────────────────────
    public CompanyProfileDto getCompanyProfile(String symbol) {
        if (!liveEnabled) {
            return getMockProfile(symbol);
        }

        try {
            String url = buildUrl("OVERVIEW", symbol);
            Map<String, Object> data = callApi(url);
            if (data == null || data.isEmpty() || data.containsKey("Information")) return getMockProfile(symbol);

            return CompanyProfileDto.builder()
                    .symbol(symbol)
                    .name(str(data, "Name"))
                    .description(str(data, "Description"))
                    .industry(str(data, "Industry"))
                    .sector(str(data, "Sector"))
                    .country(str(data, "Country"))
                    .currency(str(data, "Currency"))
                    .exchange(str(data, "Exchange"))
                    .website(str(data, "OfficialSite"))
                    .fiscalYearEnd(str(data, "FiscalYearEnd"))
                    .latestQuarter(str(data, "LatestQuarter"))
                    .dividendYield(parseDouble(str(data, "DividendYield")))
                    .dividendPerShare(parseDouble(str(data, "DividendPerShare")))
                    .marketCap(parseLong(str(data, "MarketCapitalization")))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching company profile for {}", symbol, e);
            return getMockProfile(symbol);
        }
    }

    // ─── Fundamental Data ─────────────────────────────────────────────────────
    public FundamentalDataDto getFundamentalData(String symbol) {
        if (!liveEnabled) {
            return getMockFundamentals(symbol);
        }

        try {
            String url = buildUrl("OVERVIEW", symbol);
            Map<String, Object> data = callApi(url);
            if (data == null || data.isEmpty() || data.containsKey("Information")) return getMockFundamentals(symbol);

            return FundamentalDataDto.builder()
                    .symbol(symbol)
                    .peRatio(parseDouble(str(data, "PERatio")))
                    .forwardPE(parseDouble(str(data, "ForwardPE")))
                    .priceToBbook(parseDouble(str(data, "PriceToBookRatio")))
                    .priceToSales(parseDouble(str(data, "PriceToSalesRatioTTM")))
                    .evToEbitda(parseDouble(str(data, "EVToEBITDA")))
                    .eps(parseDouble(str(data, "EPS")))
                    .revenue(parseDouble(str(data, "RevenueTTM")))
                    .grossProfit(parseDouble(str(data, "GrossProfitTTM")))
                    .profitMargin(parseDouble(str(data, "ProfitMargin")))
                    .operatingMargin(parseDouble(str(data, "OperatingMarginTTM")))
                    .returnOnEquity(parseDouble(str(data, "ReturnOnEquityTTM")))
                    .returnOnAssets(parseDouble(str(data, "ReturnOnAssetsTTM")))
                    .debtToEquity(parseDouble(str(data, "DebtToEquityRatio")))
                    .beta(parseDouble(str(data, "Beta")))
                    .dividendYield(parseDouble(str(data, "DividendYield")))
                    .dividendPerShare(parseDouble(str(data, "DividendPerShare")))
                    .sharesOutstanding(parseLong(str(data, "SharesOutstanding")))
                    .sharesFloat(parseLong(str(data, "SharesFloat")))
                    .analystTargetPrice(str(data, "AnalystTargetPrice"))
                    .analystRating(str(data, "AnalystRatingStrongBuy"))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching fundamentals for {}", symbol, e);
            return getMockFundamentals(symbol);
        }
    }

    // ─── Daily Time Series ────────────────────────────────────────────────────
    public Map<String, Object> getDailyTimeSeries(String symbol, int outputSize) {
        if (!liveEnabled) {
            return getMockTimeSeries(symbol);
        }

        try {
            String size = outputSize > 100 ? "full" : "compact";
            String url = buildUrl("TIME_SERIES_DAILY", symbol) + "&outputsize=" + size;
            Map<String, Object> response = callApi(url);
            return (response != null) ? response : getMockTimeSeries(symbol);
        } catch (Exception e) {
            log.error("Error fetching time series for {}", symbol, e);
            return getMockTimeSeries(symbol);
        }
    }

    // ─── Financial Statements ─────────────────────────────────────────────────
    public FinancialStatementDto getIncomeStatement(String symbol, String period) {
        if (!liveEnabled) {
            return getMockIncomeStatement(symbol, period);
        }

        try {
            String url = buildUrl("INCOME_STATEMENT", symbol);
            Map<String, Object> data = callApi(url);
            return parseFinancialStatement(symbol, "income", period, data);
        } catch (Exception e) {
            log.error("Error fetching income statement for {}", symbol, e);
            return getMockIncomeStatement(symbol, period);
        }
    }

    public FinancialStatementDto getBalanceSheet(String symbol, String period) {
        if (!liveEnabled) {
            return getMockBalanceSheet(symbol, period);
        }

        try {
            String url = buildUrl("BALANCE_SHEET", symbol);
            Map<String, Object> data = callApi(url);
            return parseFinancialStatement(symbol, "balance", period, data);
        } catch (Exception e) {
            log.error("Error fetching balance sheet for {}", symbol, e);
            return getMockBalanceSheet(symbol, period);
        }
    }

    public FinancialStatementDto getCashFlow(String symbol, String period) {
        if (!liveEnabled) {
            return getMockCashFlow(symbol, period);
        }

        try {
            String url = buildUrl("CASH_FLOW", symbol);
            Map<String, Object> data = callApi(url);
            return parseFinancialStatement(symbol, "cashflow", period, data);
        } catch (Exception e) {
            log.error("Error fetching cash flow for {}", symbol, e);
            return getMockCashFlow(symbol, period);
        }
    }

    // ─── News Sentiment ───────────────────────────────────────────────────────
    public List<NewsItemDto> getNewsSentiment(String symbols, int limit) {
        if (!liveEnabled) {
            return getMockNews().stream().limit(limit).collect(Collectors.toList());
        }

        try {
            String url = buildUrl("NEWS_SENTIMENT", null)
                    + "&tickers=" + symbols
                    + "&limit=" + limit
                    + "&sort=LATEST";
            Map<String, Object> response = callApi(url);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> feed = (List<Map<String, Object>>) response.get("feed");
            if (feed == null) return getMockNews();

            return feed.stream().map(item -> {
                String sentiment = str(item, "overall_sentiment_label");
                double score = parseDouble(str(item, "overall_sentiment_score"));
                return NewsItemDto.builder()
                        .title(str(item, "title"))
                        .url(str(item, "url"))
                        .source(str(item, "source"))
                        .summary(str(item, "summary"))
                        .publishedAt(str(item, "time_published"))
                        .sentiment(sentiment)
                        .sentimentScore(score)
                        .bannerImage(str(item, "banner_image"))
                        .build();
            }).limit(limit).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching news for {}", symbols, e);
            return getMockNews();
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private String buildUrl(String function, String symbol) {
        StringBuilder sb = new StringBuilder(baseUrl)
                .append("?function=").append(function)
                .append("&apikey=").append(apiKey);
        if (symbol != null) sb.append("&symbol=").append(symbol);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callApi(String url) {
        log.debug("Calling Alpha Vantage: {}", url.replaceAll("apikey=[^&]+", "apikey=***"));
        return restTemplate.getForObject(url, Map.class);
    }

    @SuppressWarnings("unchecked")
    private FinancialStatementDto parseFinancialStatement(String symbol, String type,
                                                           String period, Map<String, Object> data) {
        if (data == null || data.containsKey("Information")) {
            return getMockIncomeStatement(symbol, period);
        }
        String key = period.equalsIgnoreCase("quarterly") ? "quarterlyReports" : "annualReports";
        List<Map<String, Object>> reports = (List<Map<String, Object>>) data.get(key);
        if (reports == null || reports.isEmpty()) return getMockIncomeStatement(symbol, period);

        List<String> periods = new ArrayList<>();
        for (Map<String, Object> r : reports) {
            periods.add(str(r, "fiscalDateEnding"));
        }

        return FinancialStatementDto.builder()
                .symbol(symbol).type(type).period(period)
                .periods(periods)
                .data(reports)
                .build();
    }

    private String str(Map<String, ?> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    private double parseDouble(String s) {
        if (s == null || s.isEmpty() || s.equalsIgnoreCase("None") || s.equalsIgnoreCase("N/A")) return 0.0;
        try {
            return Double.parseDouble(s.replaceAll("%", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private long parseLong(String s) {
        if (s == null || s.isEmpty() || s.equalsIgnoreCase("None")) return 0L;
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    // ─── Mock Data (fallback when API limit reached) ──────────────────────────
    private List<StockSearchResultDto> getMockSearchResults(String query) {
        List<StockSearchResultDto> results = new ArrayList<>();
        String[][] stocks = {
            {"AAPL","Apple Inc.","NASDAQ"},
            {"MSFT","Microsoft Corp","NASDAQ"},
            {"GOOGL","Alphabet Inc","NASDAQ"},
            {"AMZN","Amazon.com Inc","NASDAQ"},
            {"TSLA","Tesla Inc","NASDAQ"},
            {"META","Meta Platforms","NASDAQ"},
            {"NVDA","NVIDIA Corporation","NASDAQ"},
            {"JPM","JPMorgan Chase","NYSE"},
            {"NFLX","Netflix Inc","NASDAQ"},
            {"BABA","Alibaba Group","NYSE"},
            {"RELIANCE.BSE","Reliance Industries","BSE"},
            {"TCS.BSE","Tata Consultancy Services","BSE"},
            {"INFY.BSE","Infosys Limited","BSE"},
            {"HDFCBANK.BSE","HDFC Bank","BSE"},
            {"RELIANCE.NSE","Reliance Industries","NSE"},
            {"TCS.NSE","Tata Consultancy Services","NSE"},
            {"INFY.NSE","Infosys Limited","NSE"}
        };
        for (String[] s : stocks) {
            if (s[0].toUpperCase().contains(query.toUpperCase()) ||
                s[1].toUpperCase().contains(query.toUpperCase())) {
                results.add(StockSearchResultDto.builder()
                        .symbol(s[0]).name(s[1]).type("Equity")
                        .region("India/US").currency("INR/USD").exchange(s[2]).build());
            }
        }
        if (results.isEmpty() && !query.isEmpty()) {
            results.add(StockSearchResultDto.builder()
                    .symbol(query.toUpperCase())
                    .name(query.toUpperCase() + " Corp")
                    .type("Equity").region("United States").currency("USD").exchange("NASDAQ")
                    .build());
        }
        return results;
    }

    public StockQuoteDto getMockQuote(String symbol) {
        Random r = new Random(symbol.hashCode());
        double base = 100 + r.nextDouble() * 900;
        
        String currency = "USD";
        String exchange = "NASDAQ";
        if (symbol.toUpperCase().endsWith(".BSE")) {
            currency = "INR";
            exchange = "BSE";
            base *= 80; // Scale up for INR
        } else if (symbol.toUpperCase().endsWith(".NSE")) {
            currency = "INR";
            exchange = "NSE";
            base *= 80; // Scale up for INR
        }

        double change = (r.nextDouble() - 0.45) * 10;
        if (currency.equals("INR")) {
            change *= 80;
        }

        return StockQuoteDto.builder()
                .symbol(symbol)
                .companyName(symbol.split("\\.")[0] + " Corporation")
                .price(Math.round(base * 100.0) / 100.0)
                .change(Math.round(change * 100.0) / 100.0)
                .changePercent(Math.round((change / base * 100) * 100.0) / 100.0)
                .open(Math.round((base - (r.nextDouble() * 5 * (currency.equals("INR") ? 80 : 1))) * 100.0) / 100.0)
                .high(Math.round((base + (r.nextDouble() * 8 * (currency.equals("INR") ? 80 : 1))) * 100.0) / 100.0)
                .low(Math.round((base - (r.nextDouble() * 8 * (currency.equals("INR") ? 80 : 1))) * 100.0) / 100.0)
                .previousClose(Math.round((base - change) * 100.0) / 100.0)
                .volume((long)(r.nextDouble() * 50_000_000 + 1_000_000))
                .marketCap((long)(base * (r.nextDouble() * 5_000_000_000L + 1_000_000_000L)))
                .week52High(Math.round((base * 1.3) * 100.0) / 100.0)
                .week52Low(Math.round((base * 0.7) * 100.0) / 100.0)
                .currency(currency).exchange(exchange).marketOpen(true)
                .lastUpdated(LocalDate.now().toString())
                .build();
    }

    private CompanyProfileDto getMockProfile(String symbol) {
        Map<String, String[]> profiles = new HashMap<>();
        profiles.put("AAPL", new String[]{"Apple Inc.", "Technology", "Consumer Electronics", "Tim Cook", "Cupertino, CA",
                "Apple designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories worldwide."});
        profiles.put("MSFT", new String[]{"Microsoft Corporation", "Technology", "Software—Infrastructure", "Satya Nadella", "Redmond, WA",
                "Microsoft develops, licenses, and supports software, services, devices, and solutions worldwide."});
        profiles.put("GOOGL", new String[]{"Alphabet Inc.", "Technology", "Internet Content & Information", "Sundar Pichai", "Mountain View, CA",
                "Alphabet provides various products and platforms including Google Search, YouTube, Android, and Google Cloud."});
        profiles.put("AMZN", new String[]{"Amazon.com Inc.", "Consumer Cyclical", "Internet Retail", "Andy Jassy", "Seattle, WA",
                "Amazon engages in retail sale of consumer products and subscriptions through online stores and physical stores worldwide."});
        profiles.put("TSLA", new String[]{"Tesla, Inc.", "Consumer Cyclical", "Auto Manufacturers", "Elon Musk", "Austin, TX",
                "Tesla designs, develops, manufactures, leases, and sells electric vehicles, energy generation and storage systems."});
        String[] p = profiles.getOrDefault(symbol.toUpperCase(),
                new String[]{symbol + " Corporation", "Technology", "Technology", "John Doe", "New York, NY",
                        "A leading technology company providing innovative solutions."});
        return CompanyProfileDto.builder()
                .symbol(symbol)
                .name(p[0])
                .sector(p[1])
                .industry(p[2])
                .ceo(p[3])
                .address(p[4])
                .description(p[5])
                .exchange(symbol.endsWith(".BSE") ? "BSE" : (symbol.endsWith(".NSE") ? "NSE" : "NASDAQ"))
                .currency(symbol.endsWith(".BSE") || symbol.endsWith(".NSE") ? "INR" : "USD")
                .dividendYield(0.015)
                .marketCap(150000000000L)
                .build();
    }

    private FundamentalDataDto getMockFundamentals(String symbol) {
        Random r = new Random(symbol.hashCode());
        return FundamentalDataDto.builder()
                .symbol(symbol)
                .peRatio(15 + r.nextDouble() * 25)
                .forwardPE(12 + r.nextDouble() * 20)
                .eps(2 + r.nextDouble() * 15)
                .revenue(1_000_000_000.0 + r.nextDouble() * 500_000_000_000.0)
                .grossProfit(500_000_000.0 + r.nextDouble() * 100_000_000_000.0)
                .netProfit(100_000_000.0 + r.nextDouble() * 50_000_000_000.0)
                .profitMargin(0.05 + r.nextDouble() * 0.35)
                .returnOnEquity(0.10 + r.nextDouble() * 0.50)
                .returnOnAssets(0.05 + r.nextDouble() * 0.20)
                .debtToEquity(0.1 + r.nextDouble() * 2.0)
                .beta(0.5 + r.nextDouble() * 1.5)
                .dividendYield(r.nextDouble() * 0.04)
                .dividendPerShare(r.nextDouble() * 5)
                .sharesOutstanding((long)(r.nextDouble() * 10_000_000_000L + 100_000_000L))
                .analystTargetPrice(String.valueOf(Math.round((150 + r.nextDouble() * 300) * 100.0) / 100.0))
                .analystRating("Buy")
                .build();
    }

    public Map<String, Object> getMockTimeSeries(String symbol) {
        Random r = new Random(symbol.hashCode());
        double base = 100 + r.nextDouble() * 400;
        Map<String, Object> series = new LinkedHashMap<>();
        Map<String, Map<String, String>> timeSeries = new LinkedHashMap<>();
        LocalDate date = LocalDate.now();
        double price = base;
        int count = 0;
        while (count < 252) {
            date = date.minusDays(1);
            if (date.getDayOfWeek().getValue() >= 6) continue;
            double dayChange = (r.nextDouble() - 0.48) * price * 0.03;
            price += dayChange;
            if (price < 10) price = 10;
            Map<String, String> day = new LinkedHashMap<>();
            day.put("1. open", String.format("%.2f", price - r.nextDouble() * 2));
            day.put("2. high", String.format("%.2f", price + r.nextDouble() * 5));
            day.put("3. low", String.format("%.2f", price - r.nextDouble() * 5));
            day.put("4. close", String.format("%.2f", price));
            day.put("5. volume", String.valueOf((long)(r.nextDouble() * 50_000_000 + 1_000_000)));
            timeSeries.put(date.toString(), day);
            count++;
        }
        series.put("Time Series (Daily)", timeSeries);
        series.put("Meta Data", Map.of("2. Symbol", symbol));
        return series;
    }

    public List<NewsItemDto> getMockNews() {
        List<NewsItemDto> news = new ArrayList<>();
        String[][] items = {
            {"Fed signals potential rate cuts amid cooling inflation", "Reuters",
             "The Federal Reserve indicated it may begin cutting interest rates as inflation shows signs of easing toward the 2% target.", "Bullish"},
            {"Apple reports record Q4 earnings, iPhone sales surge", "CNBC",
             "Apple Inc. posted record quarterly revenue driven by strong iPhone 15 sales and growing services revenue.", "Bullish"},
            {"Tech sector faces regulatory scrutiny over AI practices", "Bloomberg",
             "Major tech companies face increased regulatory pressure as governments worldwide draft new AI governance frameworks.", "Bearish"},
            {"NVIDIA announces next-gen GPU architecture, stock surges", "TechCrunch",
             "NVIDIA unveiled its next-generation Blackwell GPU architecture, sending shares up 8% in after-hours trading.", "Bullish"},
            {"Global markets mixed amid geopolitical tensions", "Financial Times",
             "Stock markets showed mixed performance as investors weighed geopolitical risks against strong corporate earnings.", "Neutral"},
            {"Microsoft Azure cloud revenue grows 29% year-over-year", "WSJ",
             "Microsoft reported strong cloud computing growth, with Azure revenue increasing 29% driven by AI workloads.", "Bullish"},
            {"Oil prices decline on demand concerns, energy stocks fall", "Reuters",
             "Crude oil prices dropped 3% as weak Chinese economic data raised concerns about global energy demand.", "Bearish"},
            {"Tesla deliveries beat expectations in Q3", "Electrek",
             "Tesla delivered 462,890 vehicles in Q3 2024, beating analyst expectations and sending shares higher.", "Bullish"}
        };
        for (String[] item : items) {
            news.add(NewsItemDto.builder()
                    .title(item[0]).source(item[1]).summary(item[2]).sentiment(item[3])
                    .sentimentScore(item[3].equals("Bullish") ? 0.7 : item[3].equals("Bearish") ? -0.6 : 0.0)
                    .publishedAt(LocalDateTime.now().minusHours((long)(Math.random() * 48)).toString())
                    .url("#").build());
        }
        return news;
    }

    private FinancialStatementDto getMockIncomeStatement(String symbol, String period) {
        Random r = new Random(symbol.hashCode());
        double rev = 1_000_000_000.0 + r.nextDouble() * 100_000_000_000.0;
        List<String> pList = List.of("2023", "2022", "2021", "2020");
        List<Map<String, Object>> data = new ArrayList<>();
        for (String p : pList) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("fiscalDateEnding", p + "-12-31");
            row.put("totalRevenue", String.format("%.0f", rev));
            row.put("grossProfit", String.format("%.0f", rev * 0.42));
            row.put("ebitda", String.format("%.0f", rev * 0.28));
            row.put("netIncome", String.format("%.0f", rev * 0.21));
            row.put("eps", String.format("%.2f", 3 + r.nextDouble() * 12));
            data.add(row);
            rev *= 0.88;
        }
        return FinancialStatementDto.builder()
                .symbol(symbol).type("income").period(period).periods(pList).data(data).build();
    }

    private FinancialStatementDto getMockBalanceSheet(String symbol, String period) {
        Random r = new Random(symbol.hashCode() + 1);
        double assets = 10_000_000_000.0 + r.nextDouble() * 500_000_000_000.0;
        List<String> pList = List.of("2023", "2022", "2021", "2020");
        List<Map<String, Object>> data = new ArrayList<>();
        for (String p : pList) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("fiscalDateEnding", p + "-12-31");
            row.put("totalAssets", String.format("%.0f", assets));
            row.put("totalLiabilities", String.format("%.0f", assets * 0.55));
            row.put("totalShareholderEquity", String.format("%.0f", assets * 0.45));
            row.put("cashAndEquivalents", String.format("%.0f", assets * 0.15));
            row.put("totalDebt", String.format("%.0f", assets * 0.30));
            data.add(row);
            assets *= 0.9;
        }
        return FinancialStatementDto.builder()
                .symbol(symbol).type("balance").period(period).periods(pList).data(data).build();
    }

    private FinancialStatementDto getMockCashFlow(String symbol, String period) {
        Random r = new Random(symbol.hashCode() + 2);
        double ocf = 5_000_000_000.0 + r.nextDouble() * 100_000_000_000.0;
        List<String> pList = List.of("2023", "2022", "2021", "2020");
        List<Map<String, Object>> data = new ArrayList<>();
        for (String p : pList) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("fiscalDateEnding", p + "-12-31");
            row.put("operatingCashflow", String.format("%.0f", ocf));
            row.put("capitalExpenditures", String.format("%.0f", -ocf * 0.25));
            row.put("freeCashFlow", String.format("%.0f", ocf * 0.75));
            row.put("dividendPayout", String.format("%.0f", -ocf * 0.15));
            row.put("netIncome", String.format("%.0f", ocf * 0.85));
            data.add(row);
            ocf *= 0.88;
        }
        return FinancialStatementDto.builder()
                .symbol(symbol).type("cashflow").period(period).periods(pList).data(data).build();
    }
}
