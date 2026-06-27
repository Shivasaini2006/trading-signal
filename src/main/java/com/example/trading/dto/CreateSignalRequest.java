package com.example.trading.dto;

import com.example.trading.enums.Direction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CreateSignalRequest {

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotNull(message = "Direction is required")
    private Direction direction;

    @NotNull(message = "Entry price is required")
    @Positive(message = "Entry price must be greater than zero")
    private BigDecimal entryPrice;

    @NotNull(message = "Stop loss is required")
    @Positive(message = "Stop loss must be greater than zero")
    private BigDecimal stopLoss;

    @NotNull(message = "Target price is required")
    @Positive(message = "Target price must be greater than zero")
    private BigDecimal targetPrice;

    @NotNull(message = "Entry time is required")
    private LocalDateTime entryTime;

    @NotNull(message = "Expiry time is required")
    private LocalDateTime expiryTime;
}
