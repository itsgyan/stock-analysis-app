package com.project.stockanalysis.repository;

import com.project.stockanalysis.entity.WatchlistItem;
import com.project.stockanalysis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {
    List<WatchlistItem> findByUser(User user);
    Optional<WatchlistItem> findByUserAndSymbol(User user, String symbol);
    boolean existsByUserAndSymbol(User user, String symbol);
    void deleteByUserAndSymbol(User user, String symbol);
}
