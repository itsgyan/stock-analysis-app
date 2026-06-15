package com.project.stockanalysis.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockSearchResultDto {
    private String symbol;
    private String name;
    private String type;
    private String region;
    private String currency;
    private String exchange;
    private String matchScore;
}
