package com.project.stockanalysis.controller;

import com.project.stockanalysis.dto.PortfolioItemDto;
import com.project.stockanalysis.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<List<PortfolioItemDto>> getPortfolio(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(portfolioService.getPortfolio(userDetails.getUsername()));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(portfolioService.getPortfolioSummary(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<PortfolioItemDto> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PortfolioItemDto dto) {
        PortfolioItemDto created = portfolioService.addItem(userDetails.getUsername(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioItemDto> updateItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody PortfolioItemDto dto) {
        return ResponseEntity.ok(portfolioService.updateItem(userDetails.getUsername(), id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        portfolioService.deleteItem(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
