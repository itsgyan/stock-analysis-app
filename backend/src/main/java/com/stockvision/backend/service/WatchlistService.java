package com.stockvision.backend.service;
import com.stockvision.backend.entity.Watchlist;
import com.stockvision.backend.entity.Stock;
import com.stockvision.backend.entity.User;
import com.stockvision.backend.repository.WatchlistRepository;
import com.stockvision.backend.repository.StockRepository;
import com.stockvision.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {
    private final WatchlistRepository repository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public Watchlist getMyWatchlist(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Find existing or create new
        return repository.findAll().stream()
            .filter(w -> w.getUser().getId().equals(user.getId()))
            .findFirst()
            .orElseGet(() -> {
                Watchlist w = new Watchlist();
                w.setUser(user);
                return repository.save(w);
            });
    }

    public Watchlist addStockToWatchlist(String username, String symbol) {
        Watchlist watchlist = getMyWatchlist(username);
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        
        watchlist.getStocks().add(stock);
        return repository.save(watchlist);
    }

    public Watchlist removeStockFromWatchlist(String username, String symbol) {
        Watchlist watchlist = getMyWatchlist(username);
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        
        watchlist.getStocks().remove(stock);
        return repository.save(watchlist);
    }
}

