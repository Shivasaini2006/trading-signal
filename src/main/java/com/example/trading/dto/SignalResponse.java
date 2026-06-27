package com.example.trading.dto;

import com.example.trading.enums.Direction;
import com.example.trading.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignalResponse {
    private Long id;
    private String symbol;
    private Direction direction;
    private BigDecimal entryPrice;
    private BigDecimal stopLoss;
    private BigDecimal targetPrice;
    private LocalDateTime entryTime;
    private LocalDateTime expiryTime;
    private LocalDateTime createdAt;
    private Status status;
    private BigDecimal realizedRoi;
}
