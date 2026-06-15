package com.project.stockanalysis.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItemDto {
    private Long id;
    private String symbol;
    private String companyName;
    private Double quantity;
    private Double buyPrice;
    private Double currentPrice;
    private Double totalInvested;
    private Double currentValue;
    private Double profitLoss;
    private Double profitLossPercent;
    private LocalDate buyDate;
}
