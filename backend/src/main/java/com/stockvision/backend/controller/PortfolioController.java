package com.stockvision.backend.controller;
import com.stockvision.backend.entity.Portfolio;
import com.stockvision.backend.entity.PortfolioItem;
import com.stockvision.backend.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService service;

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/my")
    public Portfolio getMyPortfolio() {
        return service.getMyPortfolio(getUsername());
    }

    @PostMapping("/add")
    public PortfolioItem addItem(@RequestParam String symbol, @RequestParam int quantity, @RequestParam(required = false) BigDecimal buyPrice) {
        return service.addItemToPortfolio(getUsername(), symbol, quantity, buyPrice);
    }
}
