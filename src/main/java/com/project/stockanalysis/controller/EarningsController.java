package com.project.stockanalysis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/earnings")
public class EarningsController {

    @GetMapping
    public List<Map<String, String>> getEarnings() {
        return Arrays.asList(
                Map.of(
                        "name", "Reliance Industries",
                        "qtr", "Q3 FY24",
                        "rev", "₹ 2,25,000 Cr",
                        "pat", "₹ 17,265 Cr",
                        "yoy", "+9.3%",
                        "url", "https://www.ril.com/investors"
                ),
                Map.of(
                        "name", "TCS",
                        "qtr", "Q3 FY24",
                        "rev", "₹ 60,583 Cr",
                        "pat", "₹ 11,058 Cr",
                        "yoy", "+8.2%",
                        "url", "https://www.tcs.com/investor-relations"
                ),
                Map.of(
                        "name", "HDFC Bank",
                        "qtr", "Q3 FY24",
                        "rev", "₹ 71,772 Cr",
                        "pat", "₹ 16,372 Cr",
                        "yoy", "+33.5%",
                        "url", "https://www.hdfcbank.com/personal/about-us/investor-relations"
                ),
                Map.of(
                        "name", "Infosys",
                        "qtr", "Q3 FY24",
                        "rev", "₹ 38,821 Cr",
                        "pat", "₹ 6,106 Cr",
                        "yoy", "-7.3%",
                        "url", "https://www.infosys.com/investors.html"
                ),
                Map.of(
                        "name", "ICICI Bank",
                        "qtr", "Q3 FY24",
                        "rev", "₹ 39,400 Cr",
                        "pat", "₹ 10,272 Cr",
                        "yoy", "+23.6%",
                        "url", "https://www.icicibank.com/about-us/investor-relations"
                )
        );
    }
}
