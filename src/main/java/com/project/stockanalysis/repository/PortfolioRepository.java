package com.project.stockanalysis.repository;

import com.project.stockanalysis.entity.PortfolioItem;
import com.project.stockanalysis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioItem, Long> {
    List<PortfolioItem> findByUser(User user);
    List<PortfolioItem> findByUserAndSymbol(User user, String symbol);
}
