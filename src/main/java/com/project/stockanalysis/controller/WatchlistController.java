package com.project.stockanalysis.controller;

import com.project.stockanalysis.dto.WatchlistItemDto;
import com.project.stockanalysis.service.WatchlistService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public ResponseEntity<List<WatchlistItemDto>> getWatchlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(watchlistService.getWatchlist(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<WatchlistItemDto> addToWatchlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody WatchlistRequest request) {
        WatchlistItemDto item = watchlistService.addToWatchlist(
                userDetails.getUsername(),
                request.getSymbol(),
                request.getCompanyName(),
                request.getAlertPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/{id}/alert")
    public ResponseEntity<WatchlistItemDto> updateAlert(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        Double alertPrice = body.get("alertPrice");
        return ResponseEntity.ok(watchlistService.updateAlertPrice(userDetails.getUsername(), id, alertPrice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        watchlistService.removeById(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/symbol/{symbol}")
    public ResponseEntity<Void> removeBySymbol(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String symbol) {
        watchlistService.removeFromWatchlist(userDetails.getUsername(), symbol);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{symbol}")
    public ResponseEntity<Map<String, Boolean>> isWatched(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String symbol) {
        boolean watched = watchlistService.isWatched(userDetails.getUsername(), symbol);
        return ResponseEntity.ok(Map.of("watched", watched));
    }

    @Data
    public static class WatchlistRequest {
        private String symbol;
        private String companyName;
        private Double alertPrice;
    }
}
