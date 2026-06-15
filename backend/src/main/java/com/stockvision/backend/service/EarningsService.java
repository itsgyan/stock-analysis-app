package com.stockvision.backend.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class EarningsService {

    private static final String[] COMPANIES = {
        "Reliance Industries", "TCS", "HDFC Bank", "Infosys", "ICICI Bank",
        "Hindustan Unilever", "ITC", "SBI", "Bharti Airtel", "L&T"
    };
    
    private static final String[] URLS = {
        "https://www.ril.com/investors", "https://www.tcs.com/investor-relations", 
        "https://www.hdfcbank.com/personal/about-us/investor-relations", "https://www.infosys.com/investors.html", 
        "https://www.icicibank.com/about-us/investor-relations", "https://www.hul.co.in/investor-relations/", 
        "https://www.itcportal.com/investor/index.aspx", "https://sbi.co.in/web/investor-relations", 
        "https://www.airtel.in/about-bharti/equity", "https://investors.larsentoubro.com/"
    };

    public List<Map<String, String>> getDailyEarnings() {
        // Generate daily changing data by seeding the random number generator with today's date
        LocalDate today = LocalDate.now();
        long seed = today.toEpochDay();
        Random random = new Random(seed);
        
        String currentQuarter = "Q" + ((today.getMonthValue() - 1) / 3 + 1) + " FY" + (today.getYear() % 100);
        
        List<Map<String, String>> earnings = new ArrayList<>();
        
        // Select 5-7 companies to report earnings "today"
        int numReports = 5 + random.nextInt(3);
        List<Integer> selectedIndices = new ArrayList<>();
        while(selectedIndices.size() < numReports) {
            int idx = random.nextInt(COMPANIES.length);
            if (!selectedIndices.contains(idx)) {
                selectedIndices.add(idx);
            }
        }
        
        for (int idx : selectedIndices) {
            Map<String, String> report = new HashMap<>();
            report.put("name", COMPANIES[idx]);
            report.put("qtr", currentQuarter);
            
            // Generate plausible financial numbers
            int revBase = 10000 + random.nextInt(50000);
            int patBase = revBase / (5 + random.nextInt(10));
            
            report.put("rev", "₹" + String.format("%,d", revBase) + " Cr");
            report.put("pat", "₹" + String.format("%,d", patBase) + " Cr");
            
            // YoY Growth between -5.0% and +35.0%
            double yoy = -5.0 + random.nextDouble() * 40.0;
            String yoyStr = String.format("%.1f%%", yoy);
            if (yoy > 0) yoyStr = "+" + yoyStr;
            
            report.put("yoy", yoyStr);
            report.put("url", URLS[idx]);
            
            earnings.add(report);
        }
        
        return earnings;
    }
}
