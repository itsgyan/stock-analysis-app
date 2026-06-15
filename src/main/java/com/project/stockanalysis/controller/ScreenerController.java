package com.project.stockanalysis.controller;

import com.project.stockanalysis.service.StockScreenerService;
import com.project.stockanalysis.dto.ScreenerFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/screener")
@RequiredArgsConstructor
public class ScreenerController {

    private final StockScreenerService stockScreenerService;

    /**
     * POST /api/screener  — Filter stocks by criteria
     */
    @PostMapping
    public ResponseEntity<List<Map<String, Object>>> screen(
            @RequestBody ScreenerFilterDto filter) {
        return ResponseEntity.ok(stockScreenerService.screen(filter));
    }

    /**
     * GET /api/screener/preset/{name} — Predefined screens
     */
    @GetMapping("/preset/{name}")
    public ResponseEntity<List<Map<String, Object>>> preset(@PathVariable String name) {
        ScreenerFilterDto filter = switch (name.toLowerCase()) {
            case "value" -> {
                ScreenerFilterDto f = new ScreenerFilterDto();
                f.setMaxPE(15.0);
                f.setMinDividendYield(0.02);
                f.setLimit(20);
                yield f;
            }
            case "growth" -> {
                ScreenerFilterDto f = new ScreenerFilterDto();
                f.setMinROE(0.20);
                f.setMinMarketCap(10_000_000_000.0);
                f.setLimit(20);
                yield f;
            }
            case "dividend" -> {
                ScreenerFilterDto f = new ScreenerFilterDto();
                f.setMinDividendYield(0.03);
                f.setMaxPE(25.0);
                f.setLimit(20);
                yield f;
            }
            case "largecap" -> {
                ScreenerFilterDto f = new ScreenerFilterDto();
                f.setMinMarketCap(200_000_000_000.0);
                f.setLimit(20);
                yield f;
            }
            default -> new ScreenerFilterDto();
        };
        return ResponseEntity.ok(stockScreenerService.screen(filter));
    }
}
