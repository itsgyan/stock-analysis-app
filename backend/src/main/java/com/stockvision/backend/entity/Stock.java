package com.stockvision.backend.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String symbol;
    private String companyName;
    private BigDecimal currentPrice;
    private LocalDateTime lastUpdated;
    private BigDecimal dayHigh;
    private BigDecimal dayLow;
    private BigDecimal weekHigh52;
    private BigDecimal weekLow52;
    private BigDecimal previousClose;
    private String sector;
    private BigDecimal marketCap;
}
