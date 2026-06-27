package com.example.trading.validation;

import com.example.trading.dto.CreateSignalRequest;
import com.example.trading.enums.Direction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SignalValidator {

    public void validate(CreateSignalRequest request) {
        validateTime(request.getEntryTime(), request.getExpiryTime());
        validatePrices(request);
    }

    private void validateTime(LocalDateTime entryTime, LocalDateTime expiryTime) {
        if (!expiryTime.isAfter(entryTime)) {
            throw new IllegalArgumentException("Expiry time must be after Entry time");
        }
        
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        if (entryTime.isBefore(twentyFourHoursAgo)) {
            throw new IllegalArgumentException("Entry time may be at most 24 hours in the past");
        }
    }

    private void validatePrices(CreateSignalRequest request) {
        if (request.getDirection() == Direction.BUY) {
            if (request.getStopLoss().compareTo(request.getEntryPrice()) >= 0) {
                throw new IllegalArgumentException("For BUY signals, Stop Loss must be less than Entry Price");
            }
            if (request.getTargetPrice().compareTo(request.getEntryPrice()) <= 0) {
                throw new IllegalArgumentException("For BUY signals, Target Price must be greater than Entry Price");
            }
        } else if (request.getDirection() == Direction.SELL) {
            if (request.getStopLoss().compareTo(request.getEntryPrice()) <= 0) {
                throw new IllegalArgumentException("For SELL signals, Stop Loss must be greater than Entry Price");
            }
            if (request.getTargetPrice().compareTo(request.getEntryPrice()) >= 0) {
                throw new IllegalArgumentException("For SELL signals, Target Price must be less than Entry Price");
            }
        }
    }
}
