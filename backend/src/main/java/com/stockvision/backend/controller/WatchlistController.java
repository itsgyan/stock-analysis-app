package com.stockvision.backend.controller;
import com.stockvision.backend.entity.Watchlist;
import com.stockvision.backend.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/watchlists")
@RequiredArgsConstructor
public class WatchlistController {
    private final WatchlistService service;

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/my")
    public Watchlist getMyWatchlist() {
        return service.getMyWatchlist(getUsername());
    }

    @PostMapping("/add")
    public Watchlist addStock(@RequestParam String symbol) {
        return service.addStockToWatchlist(getUsername(), symbol);
    }

    @DeleteMapping("/remove")
    public Watchlist removeStock(@RequestParam String symbol) {
        return service.removeStockFromWatchlist(getUsername(), symbol);
    }
}

