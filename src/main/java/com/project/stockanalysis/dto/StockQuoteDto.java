package com.project.stockanalysis.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockQuoteDto {
    private String symbol;
    private String companyName;
    private Double price;
    private Double change;
    private Double changePercent;
    private Double open;
    private Double high;
    private Double low;
    private Double previousClose;
    private Long volume;
    private Long marketCap;
    private Double week52High;
    private Double week52Low;
    private String currency;
    private String exchange;
    private String lastUpdated;
    private boolean marketOpen;
}
