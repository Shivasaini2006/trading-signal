package com.example.trading.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BinancePriceResponse {
    private String symbol;
    private BigDecimal price;
}
