package com.example.trading.service;

import com.example.trading.dto.BinancePriceResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
public class BinanceService {

    private final RestClient restClient;
    private static final String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/price?symbol={symbol}";

    public BinanceService(RestClient restClient) {
        this.restClient = restClient;
    }

    public BigDecimal getCurrentPrice(String symbol) {
        try {
            BinancePriceResponse response = restClient.get()
                    .uri(BINANCE_API_URL, symbol)
                    .retrieve()
                    .body(BinancePriceResponse.class);
            
            if (response != null && response.getPrice() != null) {
                return response.getPrice();
            }
            throw new RuntimeException("Failed to fetch price from Binance for symbol: " + symbol);
        } catch (Exception e) {
            throw new RuntimeException("Binance API error: " + e.getMessage(), e);
        }
    }
}
