package com.project.stockanalysis.service;

import com.project.stockanalysis.dto.StockQuoteDto;
import com.project.stockanalysis.dto.WatchlistItemDto;
import com.project.stockanalysis.entity.User;
import com.project.stockanalysis.entity.WatchlistItem;
import com.project.stockanalysis.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final AlphaVantageService alphaVantageService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<WatchlistItemDto> getWatchlist(String username) {
        User user = userService.getByUsername(username);
        return watchlistRepository.findByUser(user).stream()
                .map(this::enrichWithLivePrice)
                .collect(Collectors.toList());
    }

    @Transactional
    public WatchlistItemDto addToWatchlist(String username, String symbol, String companyName, Double alertPrice) {
        User user = userService.getByUsername(username);
        String upperSymbol = symbol.toUpperCase();
        if (watchlistRepository.existsByUserAndSymbol(user, upperSymbol)) {
            return watchlistRepository.findByUserAndSymbol(user, upperSymbol)
                    .map(this::enrichWithLivePrice)
                    .orElseThrow();
        }
        WatchlistItem item = WatchlistItem.builder()
                .user(user)
                .symbol(upperSymbol)
                .companyName(companyName)
                .alertPrice(alertPrice)
                .build();
        WatchlistItem saved = watchlistRepository.save(item);
        return enrichWithLivePrice(saved);
    }

    @Transactional
    public WatchlistItemDto updateAlertPrice(String username, Long id, Double alertPrice) {
        User user = userService.getByUsername(username);
        WatchlistItem item = watchlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Watchlist item not found: " + id));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        item.setAlertPrice(alertPrice);
        WatchlistItem saved = watchlistRepository.save(item);
        return enrichWithLivePrice(saved);
    }

    @Transactional
    public void removeFromWatchlist(String username, String symbol) {
        User user = userService.getByUsername(username);
        watchlistRepository.deleteByUserAndSymbol(user, symbol.toUpperCase());
    }

    @Transactional
    public void removeById(String username, Long id) {
        User user = userService.getByUsername(username);
        WatchlistItem item = watchlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Watchlist item not found: " + id));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        watchlistRepository.delete(item);
    }

    public boolean isWatched(String username, String symbol) {
        User user = userService.getByUsername(username);
        return watchlistRepository.existsByUserAndSymbol(user, symbol.toUpperCase());
    }

    private WatchlistItemDto enrichWithLivePrice(WatchlistItem item) {
        StockQuoteDto quote = alphaVantageService.getMockQuote(item.getSymbol());
        return WatchlistItemDto.builder()
                .id(item.getId())
                .symbol(item.getSymbol())
                .companyName(item.getCompanyName() != null ? item.getCompanyName() : quote.getCompanyName())
                .currentPrice(quote.getPrice())
                .change(quote.getChange())
                .changePercent(quote.getChangePercent())
                .alertPrice(item.getAlertPrice())
                .addedAt(item.getAddedAt())
                .build();
    }
}
