package com.stockvision.backend.controller;
import com.stockvision.backend.entity.Stock;
import com.stockvision.backend.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService service;

    @GetMapping
    public List<Stock> getAll() {
        return service.findAll();
    }

    @GetMapping("/{symbol}/quote")
    public org.springframework.http.ResponseEntity<java.math.BigDecimal> getQuote(@PathVariable String symbol) {
        // we can fetch from service. Actually StockService needs a getQuote method or we inject AlphaVantageService
        // But since StockService doesn't have it, let's add it there or just use it from StockService
        java.math.BigDecimal price = service.getQuote(symbol);
        if (price != null) {
            return org.springframework.http.ResponseEntity.ok(price);
        }
        return org.springframework.http.ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public List<Stock> searchStocks(@RequestParam String q) {
        return service.searchStocks(q);
    }
}
