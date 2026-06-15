package com.project.stockanalysis.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileDto {
    private String symbol;
    private String name;
    private String description;
    private String industry;
    private String sector;
    private String country;
    private String currency;
    private String exchange;
    private String website;
    private Integer fullTimeEmployees;
    private String ceo;
    private String address;
    private String phone;
    private String fiscalYearEnd;
    private String latestQuarter;
    private Double dividendYield;
    private Double dividendPerShare;
    private Double exDividendDate;
}
