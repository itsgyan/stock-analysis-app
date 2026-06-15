package com.project.stockanalysis.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenerFilterDto {
    private Double minMarketCap;
    private Double maxMarketCap;
    private Double minPE;
    private Double maxPE;
    private List<String> sectors;
    private Double minDividendYield;
    private Double maxDividendYield;
    private Double minROE;
    private Double minPrice;
    private Double maxPrice;
    private Integer limit = 20;
}
