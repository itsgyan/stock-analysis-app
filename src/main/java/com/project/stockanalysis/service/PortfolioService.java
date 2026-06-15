package com.project.stockanalysis.service;

import com.project.stockanalysis.dto.*;
import com.project.stockanalysis.entity.PortfolioItem;
import com.project.stockanalysis.entity.User;
import com.project.stockanalysis.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AlphaVantageService alphaVantageService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<PortfolioItemDto> getPortfolio(String username) {
        User user = userService.getByUsername(username);
        List<PortfolioItem> items = portfolioRepository.findByUser(user);
        return items.stream().map(this::enrichWithLivePrice).collect(Collectors.toList());
    }

    @Transactional
    public PortfolioItemDto addItem(String username, PortfolioItemDto dto) {
        User user = userService.getByUsername(username);
        PortfolioItem item = PortfolioItem.builder()
                .user(user)
                .symbol(dto.getSymbol().toUpperCase())
                .companyName(dto.getCompanyName())
                .quantity(dto.getQuantity())
                .buyPrice(dto.getBuyPrice())
                .buyDate(dto.getBuyDate() != null ? dto.getBuyDate() : LocalDate.now())
                .build();
        PortfolioItem saved = portfolioRepository.save(item);
        return enrichWithLivePrice(saved);
    }

    @Transactional
    public PortfolioItemDto updateItem(String username, Long id, PortfolioItemDto dto) {
        User user = userService.getByUsername(username);
        PortfolioItem item = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio item not found: " + id));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        item.setQuantity(dto.getQuantity());
        item.setBuyPrice(dto.getBuyPrice());
        if (dto.getBuyDate() != null) item.setBuyDate(dto.getBuyDate());
        if (dto.getCompanyName() != null) item.setCompanyName(dto.getCompanyName());
        PortfolioItem saved = portfolioRepository.save(item);
        return enrichWithLivePrice(saved);
    }

    @Transactional
    public void deleteItem(String username, Long id) {
        User user = userService.getByUsername(username);
        PortfolioItem item = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio item not found: " + id));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        portfolioRepository.delete(item);
    }

    public Map<String, Object> getPortfolioSummary(String username) {
        List<PortfolioItemDto> items = getPortfolio(username);

        double totalInvested = items.stream().mapToDouble(PortfolioItemDto::getTotalInvested).sum();
        double currentValue = items.stream().mapToDouble(PortfolioItemDto::getCurrentValue).sum();
        double totalPnL = currentValue - totalInvested;
        double totalPnLPct = totalInvested > 0 ? (totalPnL / totalInvested) * 100 : 0;

        // Sector allocation mock (top holdings)
        Map<String, Double> allocation = new LinkedHashMap<>();
        for (PortfolioItemDto item : items) {
            allocation.merge(item.getSymbol(), item.getCurrentValue(), Double::sum);
        }

        // Sort by value descending
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(allocation.entrySet());
        sorted.sort(Map.Entry.<String, Double>comparingByValue().reversed());
        Map<String, Double> topHoldings = new LinkedHashMap<>();
        sorted.stream().limit(10).forEach(e -> topHoldings.put(e.getKey(), round(e.getValue())));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalInvested", round(totalInvested));
        summary.put("currentValue", round(currentValue));
        summary.put("totalPnL", round(totalPnL));
        summary.put("totalPnLPercent", round(totalPnLPct));
        summary.put("numberOfHoldings", items.size());
        summary.put("topHoldings", topHoldings);
        summary.put("items", items);
        return summary;
    }

    private PortfolioItemDto enrichWithLivePrice(PortfolioItem item) {
        StockQuoteDto quote = alphaVantageService.getMockQuote(item.getSymbol());
        double currentPrice = quote.getPrice();
        double totalInvested = item.getBuyPrice() * item.getQuantity();
        double currentValue = currentPrice * item.getQuantity();
        double pnl = currentValue - totalInvested;
        double pnlPct = totalInvested > 0 ? (pnl / totalInvested) * 100 : 0;

        return PortfolioItemDto.builder()
                .id(item.getId())
                .symbol(item.getSymbol())
                .companyName(item.getCompanyName() != null ? item.getCompanyName() : item.getSymbol() + " Corp")
                .quantity(item.getQuantity())
                .buyPrice(item.getBuyPrice())
                .currentPrice(round(currentPrice))
                .totalInvested(round(totalInvested))
                .currentValue(round(currentValue))
                .profitLoss(round(pnl))
                .profitLossPercent(round(pnlPct))
                .buyDate(item.getBuyDate())
                .build();
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
