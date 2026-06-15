package com.stockvision.backend.config;

import com.stockvision.backend.entity.Stock;
import com.stockvision.backend.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final StockRepository stockRepository;

    @Override
    public void run(String... args) {
        if (stockRepository.count() == 0) {
            log.info("Seeding stocks table with initial data...");
            LocalDateTime now = LocalDateTime.now();

            List<Stock> stocks = Arrays.asList(
                createStock("RELIANCE", "Reliance Industries", "2985.40", "2970.15", "Oil & Gas", now),
                createStock("TCS", "Tata Consultancy Services", "4120.50", "4095.80", "IT", now),
                createStock("HDFCBANK", "HDFC Bank", "1440.10", "1432.50", "Banking", now),
                createStock("INFY", "Infosys", "1480.20", "1472.60", "IT", now),
                createStock("ICICIBANK", "ICICI Bank", "1245.60", "1238.90", "Banking", now),
                createStock("HINDUNILVR", "Hindustan Unilever", "2650.80", "2638.40", "FMCG", now),
                createStock("SBIN", "State Bank of India", "825.40", "819.70", "Banking", now),
                createStock("BHARTIARTL", "Bharti Airtel", "1580.30", "1568.50", "Telecom", now),
                createStock("ITC", "ITC Ltd", "465.20", "462.80", "FMCG", now),
                createStock("KOTAKBANK", "Kotak Mahindra Bank", "1820.50", "1810.30", "Banking", now),
                createStock("LT", "Larsen & Toubro", "3450.70", "3435.20", "Infrastructure", now),
                createStock("AXISBANK", "Axis Bank", "1150.30", "1142.60", "Banking", now),
                createStock("BAJFINANCE", "Bajaj Finance", "7250.40", "7215.80", "Banking", now),
                createStock("MARUTI", "Maruti Suzuki", "12450.60", "12380.40", "Auto", now),
                createStock("WIPRO", "Wipro", "485.30", "482.10", "IT", now),
                createStock("ADANIENT", "Adani Enterprises", "2850.40", "2825.60", "Infrastructure", now),
                createStock("TATAMOTORS", "Tata Motors", "985.20", "978.50", "Auto", now),
                createStock("SUNPHARMA", "Sun Pharma", "1680.40", "1670.20", "Pharma", now),
                createStock("ULTRACEMCO", "UltraTech Cement", "10250.30", "10195.60", "Cement", now),
                createStock("TITAN", "Titan Company", "3450.60", "3428.90", "Retail", now),
                createStock("NESTLEIND", "Nestle India", "2580.40", "2565.30", "FMCG", now),
                createStock("HCLTECH", "HCL Technologies", "1620.80", "1608.40", "IT", now),
                createStock("TECHM", "Tech Mahindra", "1380.50", "1372.30", "IT", now),
                createStock("POWERGRID", "Power Grid Corp", "310.40", "308.20", "Infrastructure", now),
                createStock("NTPC", "NTPC Ltd", "380.20", "377.80", "Infrastructure", now),
                createStock("JSWSTEEL", "JSW Steel", "885.60", "879.40", "Metals", now),
                createStock("TATASTEEL", "Tata Steel", "165.80", "164.20", "Metals", now),
                createStock("ZOMATO", "Zomato Ltd", "245.30", "243.10", "Internet", now),
                createStock("PAYTM", "One97 Communications", "410.15", "406.80", "Internet", now),
                createStock("DMART", "Avenue Supermarts", "4250.60", "4230.40", "Retail", now)
            );

            stockRepository.saveAll(stocks);
            log.info("Successfully seeded {} stocks.", stocks.size());
        } else {
            log.info("Stocks table already contains data. Skipping seed.");
        }
    }

    private Stock createStock(String symbol, String companyName, String price, String prevClose, String sector, LocalDateTime now) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setCompanyName(companyName);
        stock.setCurrentPrice(new BigDecimal(price));
        stock.setPreviousClose(new BigDecimal(prevClose));
        stock.setSector(sector);
        stock.setLastUpdated(now);
        return stock;
    }
}
