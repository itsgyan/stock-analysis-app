package com.project.stockanalysis.service;

import com.project.stockanalysis.dto.StockQuoteDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlphaVantageServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AlphaVantageService alphaVantageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(alphaVantageService, "apiKey", "demo");
        ReflectionTestUtils.setField(alphaVantageService, "baseUrl", "https://www.alphavantage.co/query");
        ReflectionTestUtils.setField(alphaVantageService, "liveEnabled", true);
    }

    @Test
    void testGetGlobalQuote_Success() {
        // Arrange: Mock the external API response
        Map<String, Object> mockResponse = new HashMap<>();
        Map<String, String> quoteData = new HashMap<>();
        quoteData.put("01. symbol", "AAPL");
        quoteData.put("05. price", "150.00");
        quoteData.put("09. change", "2.50");
        quoteData.put("10. change percent", "1.69%");
        quoteData.put("02. open", "148.00");
        quoteData.put("03. high", "151.00");
        quoteData.put("04. low", "147.00");
        quoteData.put("08. previous close", "147.50");
        quoteData.put("06. volume", "50000000");
        quoteData.put("07. latest trading day", "2024-05-15");
        
        mockResponse.put("Global Quote", quoteData);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        // Act
        StockQuoteDto result = alphaVantageService.getGlobalQuote("AAPL");

        // Assert
        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        assertEquals(150.0, result.getPrice());
        assertEquals(2.50, result.getChange());
        assertEquals(1.69, result.getChangePercent());
        assertTrue(result.isMarketOpen());
    }

    @Test
    void testGetGlobalQuote_FallbackToMock() {
        // Arrange: Simulate API limit reached or empty response
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(new HashMap<>());

        // Act
        StockQuoteDto result = alphaVantageService.getGlobalQuote("MSFT");

        // Assert
        assertNotNull(result);
        // Symbol should be MSFT and price should be randomly generated fallback > 0
        assertEquals("MSFT", result.getSymbol());
        assertTrue(result.getPrice() > 0);
    }

    @Test
    void testGetGlobalQuote_UsesMockWhenLiveDisabled() {
        ReflectionTestUtils.setField(alphaVantageService, "liveEnabled", false);

        StockQuoteDto result = alphaVantageService.getGlobalQuote("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        assertTrue(result.getPrice() > 0);
    }
}
