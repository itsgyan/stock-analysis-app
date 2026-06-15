package com.stockvision.backend.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "watchlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToMany
    @JoinTable(name = "watchlist_stocks",
               joinColumns = @JoinColumn(name = "watchlist_id"),
               inverseJoinColumns = @JoinColumn(name = "stock_id"))
    private Set<Stock> stocks = new HashSet<>();
}
