package com.project.stockanalysis.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundamentalDataDto {
    private String symbol;
    // Valuation
    private Double peRatio;
    private Double forwardPE;
    private Double priceToBbook;
    private Double priceToSales;
    private Double evToEbitda;
    // Profitability
    private Double eps;
    private Double epsGrowth;
    private Double revenue;
    private Double revenueGrowth;
    private Double grossProfit;
    private Double netProfit;
    private Double profitMargin;
    private Double operatingMargin;
    private Double returnOnEquity;
    private Double returnOnAssets;
    // Debt
    private Double debtToEquity;
    private Double currentRatio;
    private Double quickRatio;
    // Dividends
    private Double dividendYield;
    private Double dividendPerShare;
    private Double payoutRatio;
    // Market data
    private Long sharesOutstanding;
    private Long sharesFloat;
    private Double beta;
    private String analystTargetPrice;
    private String analystRating;
}
