package com.project.stockanalysis.controller;

import com.project.stockanalysis.service.WebSocketPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * WebSocket STOMP controller.
 * Clients connect and send subscribe/unsubscribe messages to manage
 * which symbols they receive live price updates for.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final WebSocketPriceService webSocketPriceService;

    /**
     * Client sends: {"action":"subscribe","symbol":"AAPL"}
     * to /app/price.subscribe
     */
    @MessageMapping("/price.subscribe")
    public void subscribe(@Payload Map<String, String> payload,
                          SimpMessageHeaderAccessor headerAccessor) {
        String symbol = payload.get("symbol");
        if (symbol != null && !symbol.isBlank()) {
            log.info("WS subscribe request for: {}", symbol);
            webSocketPriceService.subscribe(symbol.toUpperCase());
        }
    }

    /**
     * Client sends: {"action":"unsubscribe","symbol":"AAPL"}
     * to /app/price.unsubscribe
     */
    @MessageMapping("/price.unsubscribe")
    public void unsubscribe(@Payload Map<String, String> payload,
                            SimpMessageHeaderAccessor headerAccessor) {
        String symbol = payload.get("symbol");
        if (symbol != null && !symbol.isBlank()) {
            log.info("WS unsubscribe request for: {}", symbol);
            webSocketPriceService.unsubscribe(symbol.toUpperCase());
        }
    }
}
