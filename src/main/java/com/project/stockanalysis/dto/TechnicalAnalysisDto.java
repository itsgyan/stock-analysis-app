package com.project.stockanalysis.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalAnalysisDto {
    private String symbol;
    // Moving Averages
    private Double sma20;
    private Double sma50;
    private Double sma200;
    private Double ema20;
    private Double ema50;
    // RSI
    private Double rsi14;
    private String rsiSignal; // Overbought / Oversold / Neutral
    // MACD
    private Double macdLine;
    private Double signalLine;
    private Double macdHistogram;
    private String macdSignal; // Buy / Sell / Neutral
    // Bollinger Bands
    private Double bbUpper;
    private Double bbMiddle;
    private Double bbLower;
    private Double bbWidth;
    // Support & Resistance
    private Double support1;
    private Double support2;
    private Double resistance1;
    private Double resistance2;
    // Price data for chart
    private List<String> dates;
    private List<Double> closePrices;
    private List<Double> sma20Series;
    private List<Double> sma50Series;
    private List<Double> bbUpperSeries;
    private List<Double> bbLowerSeries;
    private List<Double> rsiSeries;
    private List<Double> macdSeries;
    private List<Double> signalSeries;
    private List<Double> histogramSeries;
}
