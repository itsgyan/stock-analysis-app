package com.project.stockanalysis.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsItemDto {
    private String title;
    private String url;
    private String source;
    private String summary;
    private String publishedAt;
    private String sentiment; // Bullish / Bearish / Neutral
    private Double sentimentScore;
    private String bannerImage;
    private List<String> relatedSymbols;
}
