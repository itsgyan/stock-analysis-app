package com.project.stockanalysis.dto;

import lombok.*;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialStatementDto {
    private String symbol;
    private String type;   // income, balance, cashflow
    private String period; // annual, quarterly
    private List<String> periods;
    private List<Map<String, Object>> data;
}
