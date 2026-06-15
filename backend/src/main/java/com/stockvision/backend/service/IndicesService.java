package com.stockvision.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndicesService {

    private final RestTemplate restTemplate;

    // Yahoo Finance symbols for Indian indices
    private static final Map<String, String> INDEX_MAP = new LinkedHashMap<>() {{
        put("^BSESN", "SENSEX");
        put("^NSEI", "NIFTY 50");
        put("^NSEBANK", "NIFTY BANK");
        put("^CNXIT", "NIFTY IT");
        put("^CNXPHARMA", "NIFTY PHARMA");
        put("^CNXAUTO", "NIFTY AUTO");
        put("^CNXFMCG", "NIFTY FMCG");
        put("^CNXMETAL", "NIFTY METAL");
        put("^CNXREALTY", "NIFTY REALTY");
        put("^CNXENERGY", "NIFTY ENERGY");
        put("NIFTYMIDCAP150.NS", "NIFTY MIDCAP 150");
        put("NIFTYSMLCAP250.NS", "NIFTY SMALLCAP 250");
    }};

    @Cacheable(value = "indices", unless = "#result == null || #result.isEmpty()")
    public List<Map<String, Object>> getAllIndices() {
        log.info("Fetching live Indian indices from Yahoo Finance...");
        List<Map<String, Object>> indices = new ArrayList<>();

        for (Map.Entry<String, String> entry : INDEX_MAP.entrySet()) {
            try {
                Map<String, Object> indexData = fetchIndex(entry.getKey(), entry.getValue());
                if (indexData != null) {
                    indices.add(indexData);
                }
            } catch (Exception e) {
                log.warn("Failed to fetch index {}: {}", entry.getValue(), e.getMessage());
            }
        }

        log.info("Fetched {} indices successfully.", indices.size());
        return indices;
    }

    private Map<String, Object> fetchIndex(String yahooSymbol, String displayName) {
        try {
            String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + yahooSymbol + "?interval=1d&range=1d";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("chart")) {
                Map<String, Object> chart = (Map<String, Object>) response.get("chart");
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) chart.get("result");

                if (resultList != null && !resultList.isEmpty()) {
                    Map<String, Object> result = resultList.get(0);
                    Map<String, Object> meta = (Map<String, Object>) result.get("meta");

                    double price = toDouble(meta.get("regularMarketPrice"));
                    double prevClose = toDouble(meta.get("chartPreviousClose"));
                    double dayHigh = toDouble(meta.get("regularMarketDayHigh"));
                    double dayLow = toDouble(meta.get("regularMarketDayLow"));
                    double change = price - prevClose;
                    double pctChange = prevClose != 0 ? (change / prevClose) * 100 : 0;

                    Map<String, Object> indexData = new LinkedHashMap<>();
                    indexData.put("name", displayName);
                    indexData.put("symbol", yahooSymbol);
                    indexData.put("price", BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP));
                    indexData.put("change", BigDecimal.valueOf(change).setScale(2, RoundingMode.HALF_UP));
                    indexData.put("pctChange", BigDecimal.valueOf(pctChange).setScale(2, RoundingMode.HALF_UP));
                    indexData.put("dayHigh", BigDecimal.valueOf(dayHigh).setScale(2, RoundingMode.HALF_UP));
                    indexData.put("dayLow", BigDecimal.valueOf(dayLow).setScale(2, RoundingMode.HALF_UP));
                    indexData.put("previousClose", BigDecimal.valueOf(prevClose).setScale(2, RoundingMode.HALF_UP));
                    indexData.put("pos", change >= 0);

                    return indexData;
                }
            }
        } catch (Exception e) {
            log.warn("Yahoo Finance API error for {}: {}", displayName, e.getMessage());
        }
        return null;
    }

    private double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try { return Double.parseDouble(value.toString()); }
        catch (Exception e) { return 0.0; }
    }
}
