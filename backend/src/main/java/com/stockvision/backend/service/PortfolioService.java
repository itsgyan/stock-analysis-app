package com.stockvision.backend.service;
import com.stockvision.backend.entity.Portfolio;
import com.stockvision.backend.entity.PortfolioItem;
import com.stockvision.backend.entity.Stock;
import com.stockvision.backend.entity.User;
import com.stockvision.backend.repository.PortfolioItemRepository;
import com.stockvision.backend.repository.PortfolioRepository;
import com.stockvision.backend.repository.StockRepository;
import com.stockvision.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository repository;
    private final PortfolioItemRepository itemRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public Portfolio getMyPortfolio(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return repository.findAll().stream()
            .filter(p -> p.getUser().getId().equals(user.getId()))
            .findFirst()
            .orElseGet(() -> {
                Portfolio p = new Portfolio();
                p.setUser(user);
                return repository.save(p);
            });
    }

    public PortfolioItem addItemToPortfolio(String username, String symbol, int quantity, BigDecimal buyPrice) {
        Portfolio portfolio = getMyPortfolio(username);
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        if (buyPrice == null) {
            buyPrice = stock.getCurrentPrice();
            if (buyPrice == null) {
                throw new RuntimeException("Market price is unavailable");
            }
        }

        // Check if item already exists in portfolio
        Optional<PortfolioItem> existing = portfolio.getItems().stream()
                .filter(i -> i.getStock().getId().equals(stock.getId()))
                .findFirst();

        if (existing.isPresent()) {
            PortfolioItem item = existing.get();
            // simple average price calculation
            BigDecimal totalOldValue = item.getAveragePrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal totalNewValue = buyPrice.multiply(BigDecimal.valueOf(quantity));
            int newQuantity = item.getQuantity() + quantity;
            BigDecimal newAvg = totalOldValue.add(totalNewValue).divide(BigDecimal.valueOf(newQuantity), 2, java.math.RoundingMode.HALF_UP);
            
            item.setQuantity(newQuantity);
            item.setAveragePrice(newAvg);
            return itemRepository.save(item);
        }

        PortfolioItem item = new PortfolioItem();
        item.setPortfolio(portfolio);
        item.setStock(stock);
        item.setQuantity(quantity);
        item.setAveragePrice(buyPrice);
        
        return itemRepository.save(item);
    }
}

