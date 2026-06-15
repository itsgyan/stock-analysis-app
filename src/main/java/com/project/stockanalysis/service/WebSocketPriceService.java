package com.project.stockanalysis.service;

import com.project.stockanalysis.dto.StockQuoteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketPriceService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AlphaVantageService alphaVantageService;

    // Subscribed symbols (added by controller)
    private final Set<String> subscribedSymbols = ConcurrentHashMap.newKeySet();

    private static final String[] DEFAULT_SYMBOLS = {"AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "META", "NVDA"};

    public void subscribe(String symbol) {
        subscribedSymbols.add(symbol.toUpperCase());
        log.info("Subscribed to live prices for: {}", symbol);
    }

    public void unsubscribe(String symbol) {
        subscribedSymbols.remove(symbol.toUpperCase());
        log.info("Unsubscribed from live prices for: {}", symbol);
    }

    @Scheduled(fixedDelayString = "${websocket.price-update-interval-ms:15000}")
    public void broadcastPrices() {
        Set<String> symbols = new HashSet<>(subscribedSymbols);
        for (String d : DEFAULT_SYMBOLS) symbols.add(d);

        List<Map<String, Object>> updates = new ArrayList<>();
        for (String symbol : symbols) {
            try {
                StockQuoteDto quote = alphaVantageService.getMockQuote(symbol);
                // Simulate slight real-time variation
                double jitter = (new Random().nextDouble() - 0.5) * 0.5;
                double newPrice = Math.round((quote.getPrice() + jitter) * 100.0) / 100.0;
                double newChange = Math.round((quote.getChange() + jitter) * 100.0) / 100.0;

                Map<String, Object> update = new LinkedHashMap<>();
                update.put("symbol", symbol);
                update.put("price", newPrice);
                update.put("change", newChange);
                update.put("changePercent", quote.getChangePercent());
                update.put("volume", quote.getVolume());
                update.put("timestamp", System.currentTimeMillis());
                updates.add(update);
            } catch (Exception e) {
                log.warn("Failed to get price for {}: {}", symbol, e.getMessage());
            }
        }

        if (!updates.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/prices", updates);
            log.debug("Broadcasted price updates for {} symbols", updates.size());
        }
    }

    @Scheduled(fixedDelayString = "${websocket.market-status-interval-ms:60000}")
    public void broadcastMarketStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        java.time.LocalTime now = java.time.LocalTime.now(java.time.ZoneId.of("America/New_York"));
        boolean isOpen = now.isAfter(java.time.LocalTime.of(9, 30))
                && now.isBefore(java.time.LocalTime.of(16, 0));
        status.put("marketOpen", isOpen);
        status.put("timestamp", System.currentTimeMillis());
        status.put("exchange", "NYSE/NASDAQ");
        messagingTemplate.convertAndSend("/topic/market-status", status);
    }
}
