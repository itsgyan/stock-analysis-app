package com.project.stockanalysis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/indices")
public class IndicesController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getIndices() {
        return ResponseEntity.ok(List.of(
            Map.of("name", "NIFTY 50", "price", 23500.5, "change", 150.2, "pctChange", 0.64, "pos", true),
            Map.of("name", "SENSEX", "price", 77200.0, "change", 500.0, "pctChange", 0.65, "pos", true),
            Map.of("name", "BANK NIFTY", "price", 51000.0, "change", -120.0, "pctChange", -0.23, "pos", false),
            Map.of("name", "NASDAQ", "price", 17600.0, "change", 200.0, "pctChange", 1.15, "pos", true)
        ));
    }
}
