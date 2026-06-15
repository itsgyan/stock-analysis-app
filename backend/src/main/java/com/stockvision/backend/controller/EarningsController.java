package com.stockvision.backend.controller;

import com.stockvision.backend.service.EarningsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/earnings")
@RequiredArgsConstructor
public class EarningsController {

    private final EarningsService earningsService;

    @GetMapping
    public List<Map<String, String>> getDailyEarnings() {
        return earningsService.getDailyEarnings();
    }
}
