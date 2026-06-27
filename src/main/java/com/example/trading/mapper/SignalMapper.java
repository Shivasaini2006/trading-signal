package com.example.trading.mapper;

import com.example.trading.dto.CreateSignalRequest;
import com.example.trading.dto.SignalResponse;
import com.example.trading.entity.TradingSignal;
import org.springframework.stereotype.Component;

@Component
public class SignalMapper {

    public TradingSignal toEntity(CreateSignalRequest request) {
        return TradingSignal.builder()
                .symbol(request.getSymbol().toUpperCase())
                .direction(request.getDirection())
                .entryPrice(request.getEntryPrice())
                .stopLoss(request.getStopLoss())
                .targetPrice(request.getTargetPrice())
                .entryTime(request.getEntryTime())
                .expiryTime(request.getExpiryTime())
                .build();
    }

    public SignalResponse toResponse(TradingSignal entity) {
        return SignalResponse.builder()
                .id(entity.getId())
                .symbol(entity.getSymbol())
                .direction(entity.getDirection())
                .entryPrice(entity.getEntryPrice())
                .stopLoss(entity.getStopLoss())
                .targetPrice(entity.getTargetPrice())
                .entryTime(entity.getEntryTime())
                .expiryTime(entity.getExpiryTime())
                .createdAt(entity.getCreatedAt())
                .status(entity.getStatus())
                .realizedRoi(entity.getRealizedRoi())
                .build();
    }
}
