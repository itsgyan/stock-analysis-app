package com.project.stockanalysis.service;

import com.project.stockanalysis.dto.TechnicalAnalysisDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TechnicalAnalysisService {

    private final AlphaVantageService alphaVantageService;

    public TechnicalAnalysisDto analyze(String symbol) {
        try {
            Map<String, Object> tsData = alphaVantageService.getDailyTimeSeries(symbol, 252);
            @SuppressWarnings("unchecked")
            Map<String, Map<String, String>> timeSeries =
                    (Map<String, Map<String, String>>) tsData.get("Time Series (Daily)");

            if (timeSeries == null || timeSeries.isEmpty()) {
                return buildMockAnalysis(symbol);
            }

            List<String> sortedDates = new ArrayList<>(timeSeries.keySet());
            Collections.sort(sortedDates);
            if (sortedDates.size() > 252) {
                sortedDates = sortedDates.subList(sortedDates.size() - 252, sortedDates.size());
            }

            List<Double> closes = sortedDates.stream()
                    .map(d -> parseDoubleSafe(timeSeries.get(d).getOrDefault("4. close", "0")))
                    .collect(Collectors.toList());
            List<Double> highs = sortedDates.stream()
                    .map(d -> parseDoubleSafe(timeSeries.get(d).getOrDefault("2. high", "0")))
                    .collect(Collectors.toList());
            List<Double> lows = sortedDates.stream()
                    .map(d -> parseDoubleSafe(timeSeries.get(d).getOrDefault("3. low", "0")))
                    .collect(Collectors.toList());

            List<Double> sma20Series = calcSMA(closes, 20);
            List<Double> sma50Series = calcSMA(closes, 50);
            List<Double> sma200Series = calcSMA(closes, 200);
            List<Double> ema20Series = calcEMA(closes, 20);
            List<Double> ema50Series = calcEMA(closes, 50);
            List<Double> rsiSeries = calcRSI(closes, 14);
            Map<String, List<Double>> macdData = calcMACD(closes);
            Map<String, List<Double>> bbData = calcBollingerBands(closes, 20, 2.0);

            double rsi = rsiSeries.isEmpty() ? 50 : last(rsiSeries);
            double macdLine = macdData.get("macd").isEmpty() ? 0 : last(macdData.get("macd"));
            double signalLine = macdData.get("signal").isEmpty() ? 0 : last(macdData.get("signal"));
            double histogram = macdLine - signalLine;

            double[] sr = calcSupportResistance(highs, lows, closes);

            // Use only last 60 data points for chart to keep response small
            int chartLen = Math.min(60, closes.size());
            int offset = closes.size() - chartLen;
            List<String> chartDates = sortedDates.subList(sortedDates.size() - chartLen, sortedDates.size());
            List<Double> chartCloses = closes.subList(offset, closes.size());

            List<Double> sma20Padded = padLeft(sma20Series, closes.size());
            List<Double> sma50Padded = padLeft(sma50Series, closes.size());
            List<Double> bbUpperPadded = padLeft(bbData.get("upper"), closes.size());
            List<Double> bbLowerPadded = padLeft(bbData.get("lower"), closes.size());
            List<Double> rsiPadded = padLeft(rsiSeries, closes.size());
            List<Double> macdPadded = padLeft(macdData.get("macd"), closes.size());
            List<Double> signalPadded = padLeft(macdData.get("signal"), closes.size());
            List<Double> histPadded = padLeft(macdData.get("histogram"), closes.size());

            return TechnicalAnalysisDto.builder()
                    .symbol(symbol)
                    .sma20(last(sma20Series)).sma50(last(sma50Series)).sma200(last(sma200Series))
                    .ema20(last(ema20Series)).ema50(last(ema50Series))
                    .rsi14(round(rsi))
                    .rsiSignal(rsi > 70 ? "Overbought" : rsi < 30 ? "Oversold" : "Neutral")
                    .macdLine(round(macdLine)).signalLine(round(signalLine)).macdHistogram(round(histogram))
                    .macdSignal(macdLine > signalLine ? "Buy" : "Sell")
                    .bbUpper(round(last(bbData.get("upper"))))
                    .bbMiddle(round(last(bbData.get("middle"))))
                    .bbLower(round(last(bbData.get("lower"))))
                    .bbWidth(round((last(bbData.get("upper")) - last(bbData.get("lower"))) / last(bbData.get("middle"))))
                    .support1(round(sr[0])).support2(round(sr[1]))
                    .resistance1(round(sr[2])).resistance2(round(sr[3]))
                    .dates(chartDates)
                    .closePrices(chartCloses)
                    .sma20Series(sma20Padded.subList(offset, closes.size()))
                    .sma50Series(sma50Padded.subList(offset, closes.size()))
                    .bbUpperSeries(bbUpperPadded.subList(offset, closes.size()))
                    .bbLowerSeries(bbLowerPadded.subList(offset, closes.size()))
                    .rsiSeries(rsiPadded.subList(offset, closes.size()))
                    .macdSeries(macdPadded.subList(offset, closes.size()))
                    .signalSeries(signalPadded.subList(offset, closes.size()))
                    .histogramSeries(histPadded.subList(offset, closes.size()))
                    .build();
        } catch (Exception e) {
            log.error("Error computing technical analysis for {}", symbol, e);
            return buildMockAnalysis(symbol);
        }
    }

    // ─── SMA ─────────────────────────────────────────────────────────────────
    private List<Double> calcSMA(List<Double> data, int period) {
        List<Double> sma = new ArrayList<>();
        if (data.size() < period) return sma;
        for (int i = period - 1; i < data.size(); i++) {
            double sum = 0;
            for (int j = i - period + 1; j <= i; j++) sum += data.get(j);
            sma.add(round(sum / period));
        }
        return sma;
    }

    // ─── EMA ─────────────────────────────────────────────────────────────────
    private List<Double> calcEMA(List<Double> data, int period) {
        List<Double> ema = new ArrayList<>();
        if (data.size() < period) return ema;
        double k = 2.0 / (period + 1);
        double sum = 0;
        for (int i = 0; i < period; i++) sum += data.get(i);
        double prevEma = sum / period;
        ema.add(round(prevEma));
        for (int i = period; i < data.size(); i++) {
            prevEma = data.get(i) * k + prevEma * (1 - k);
            ema.add(round(prevEma));
        }
        return ema;
    }

    // ─── RSI ─────────────────────────────────────────────────────────────────
    private List<Double> calcRSI(List<Double> data, int period) {
        List<Double> rsi = new ArrayList<>();
        if (data.size() < period + 1) return rsi;

        double avgGain = 0, avgLoss = 0;
        for (int i = 1; i <= period; i++) {
            double diff = data.get(i) - data.get(i - 1);
            if (diff > 0) avgGain += diff;
            else avgLoss += Math.abs(diff);
        }
        avgGain /= period;
        avgLoss /= period;

        if (avgLoss == 0) {
            rsi.add(100.0);
        } else {
            double rs = avgGain / avgLoss;
            rsi.add(round(100.0 - (100.0 / (1 + rs))));
        }

        for (int i = period + 1; i < data.size(); i++) {
            double diff = data.get(i) - data.get(i - 1);
            double gain = diff > 0 ? diff : 0;
            double loss = diff < 0 ? Math.abs(diff) : 0;
            avgGain = (avgGain * (period - 1) + gain) / period;
            avgLoss = (avgLoss * (period - 1) + loss) / period;
            if (avgLoss == 0) {
                rsi.add(100.0);
            } else {
                double rs = avgGain / avgLoss;
                rsi.add(round(100.0 - (100.0 / (1 + rs))));
            }
        }
        return rsi;
    }

    // ─── MACD ─────────────────────────────────────────────────────────────────
    private Map<String, List<Double>> calcMACD(List<Double> data) {
        List<Double> ema12 = calcEMA(data, 12);
        List<Double> ema26 = calcEMA(data, 26);

        // Align EMA12 and EMA26 (EMA26 is shorter)
        int diff = ema12.size() - ema26.size();
        List<Double> macdLine = new ArrayList<>();
        for (int i = 0; i < ema26.size(); i++) {
            macdLine.add(round(ema12.get(i + diff) - ema26.get(i)));
        }

        List<Double> signalLine = calcEMA(macdLine, 9);
        int sigDiff = macdLine.size() - signalLine.size();
        List<Double> histogram = new ArrayList<>();
        for (int i = 0; i < signalLine.size(); i++) {
            histogram.add(round(macdLine.get(i + sigDiff) - signalLine.get(i)));
        }

        Map<String, List<Double>> result = new HashMap<>();
        result.put("macd", macdLine);
        result.put("signal", signalLine);
        result.put("histogram", histogram);
        return result;
    }

    // ─── Bollinger Bands ──────────────────────────────────────────────────────
    private Map<String, List<Double>> calcBollingerBands(List<Double> data, int period, double stdDevMultiplier) {
        List<Double> upper = new ArrayList<>();
        List<Double> middle = new ArrayList<>();
        List<Double> lower = new ArrayList<>();

        if (data.size() < period) {
            Map<String, List<Double>> empty = new HashMap<>();
            empty.put("upper", upper);
            empty.put("middle", middle);
            empty.put("lower", lower);
            return empty;
        }

        for (int i = period - 1; i < data.size(); i++) {
            double sum = 0;
            for (int j = i - period + 1; j <= i; j++) sum += data.get(j);
            double avg = sum / period;

            double variance = 0;
            for (int j = i - period + 1; j <= i; j++) {
                double d = data.get(j) - avg;
                variance += d * d;
            }
            double stdDev = Math.sqrt(variance / period);

            middle.add(round(avg));
            upper.add(round(avg + stdDevMultiplier * stdDev));
            lower.add(round(avg - stdDevMultiplier * stdDev));
        }

        Map<String, List<Double>> result = new HashMap<>();
        result.put("upper", upper);
        result.put("middle", middle);
        result.put("lower", lower);
        return result;
    }

    // ─── Support & Resistance ─────────────────────────────────────────────────
    private double[] calcSupportResistance(List<Double> highs, List<Double> lows, List<Double> closes) {
        if (closes.isEmpty()) return new double[]{0, 0, 0, 0};

        int lookback = Math.min(20, closes.size());
        List<Double> recentHighs = highs.subList(highs.size() - lookback, highs.size());
        List<Double> recentLows = lows.subList(lows.size() - lookback, lows.size());

        double maxHigh = recentHighs.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double minLow = recentLows.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double currentPrice = closes.get(closes.size() - 1);

        double range = maxHigh - minLow;
        double resistance1 = currentPrice + range * 0.15;
        double resistance2 = currentPrice + range * 0.30;
        double support1 = currentPrice - range * 0.15;
        double support2 = currentPrice - range * 0.30;

        return new double[]{support1, support2, resistance1, resistance2};
    }

    // ─── Pad List to target size ──────────────────────────────────────────────
    private List<Double> padLeft(List<Double> data, int targetSize) {
        List<Double> padded = new ArrayList<>();
        int padding = targetSize - data.size();
        for (int i = 0; i < padding; i++) padded.add(null);
        padded.addAll(data);
        return padded;
    }

    private double last(List<Double> list) {
        if (list == null || list.isEmpty()) return 0.0;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i) != null) return list.get(i);
        }
        return 0.0;
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    private double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s); }
        catch (Exception e) { return 0.0; }
    }

    // ─── Mock fallback analysis ───────────────────────────────────────────────
    private TechnicalAnalysisDto buildMockAnalysis(String symbol) {
        Random r = new Random(symbol.hashCode());
        double base = 100 + r.nextDouble() * 400;
        double sma20 = base * (0.95 + r.nextDouble() * 0.10);
        double sma50 = base * (0.90 + r.nextDouble() * 0.10);
        double sma200 = base * (0.80 + r.nextDouble() * 0.10);
        double rsi = 30 + r.nextDouble() * 40;
        double macd = (r.nextDouble() - 0.5) * 5;
        double signal = macd - (r.nextDouble() - 0.5) * 2;
        double bbMiddle = sma20;
        double bbUpper = bbMiddle * 1.05;
        double bbLower = bbMiddle * 0.95;

        // Generate 60 days of mock chart data
        List<String> dates = new ArrayList<>();
        List<Double> closes = new ArrayList<>();
        List<Double> sma20s = new ArrayList<>();
        List<Double> sma50s = new ArrayList<>();
        List<Double> bbUppers = new ArrayList<>();
        List<Double> bbLowers = new ArrayList<>();
        List<Double> rsis = new ArrayList<>();
        List<Double> macds = new ArrayList<>();
        List<Double> signals = new ArrayList<>();
        List<Double> histograms = new ArrayList<>();

        java.time.LocalDate d = java.time.LocalDate.now().minusDays(60);
        double price = base;
        for (int i = 0; i < 60; i++) {
            d = d.plusDays(1);
            if (d.getDayOfWeek().getValue() >= 6) { i--; continue; }
            double change = (r.nextDouble() - 0.48) * price * 0.02;
            price += change;
            double s20 = price * (0.97 + r.nextDouble() * 0.06);
            double s50 = price * (0.94 + r.nextDouble() * 0.06);
            double rsiVal = 35 + r.nextDouble() * 30;
            double macdVal = (r.nextDouble() - 0.5) * 4;
            double sigVal = macdVal * 0.9;
            dates.add(d.toString());
            closes.add(round(price));
            sma20s.add(round(s20));
            sma50s.add(round(s50));
            bbUppers.add(round(s20 * 1.04));
            bbLowers.add(round(s20 * 0.96));
            rsis.add(round(rsiVal));
            macds.add(round(macdVal));
            signals.add(round(sigVal));
            histograms.add(round(macdVal - sigVal));
        }

        return TechnicalAnalysisDto.builder()
                .symbol(symbol)
                .sma20(round(sma20)).sma50(round(sma50)).sma200(round(sma200))
                .ema20(round(sma20 * 1.01)).ema50(round(sma50 * 1.01))
                .rsi14(round(rsi))
                .rsiSignal(rsi > 70 ? "Overbought" : rsi < 30 ? "Oversold" : "Neutral")
                .macdLine(round(macd)).signalLine(round(signal)).macdHistogram(round(macd - signal))
                .macdSignal(macd > signal ? "Buy" : "Sell")
                .bbUpper(round(bbUpper)).bbMiddle(round(bbMiddle)).bbLower(round(bbLower))
                .bbWidth(round((bbUpper - bbLower) / bbMiddle))
                .support1(round(base * 0.92)).support2(round(base * 0.85))
                .resistance1(round(base * 1.08)).resistance2(round(base * 1.15))
                .dates(dates).closePrices(closes)
                .sma20Series(sma20s).sma50Series(sma50s)
                .bbUpperSeries(bbUppers).bbLowerSeries(bbLowers)
                .rsiSeries(rsis).macdSeries(macds).signalSeries(signals).histogramSeries(histograms)
                .build();
    }
}
