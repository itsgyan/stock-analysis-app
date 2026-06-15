package com.project.stockanalysis.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistItemDto {
    private Long id;
    private String symbol;
    private String companyName;
    private Double currentPrice;
    private Double change;
    private Double changePercent;
    private Double alertPrice;
    private LocalDateTime addedAt;
}
