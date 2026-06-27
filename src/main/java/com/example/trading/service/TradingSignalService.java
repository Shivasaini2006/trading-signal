package com.example.trading.service;

import com.example.trading.dto.CreateSignalRequest;
import com.example.trading.dto.SignalResponse;
import com.example.trading.entity.TradingSignal;
import com.example.trading.enums.Direction;
import com.example.trading.enums.Status;
import com.example.trading.exception.EntityNotFoundException;
import com.example.trading.mapper.SignalMapper;
import com.example.trading.repository.TradingSignalRepository;
import com.example.trading.validation.SignalValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradingSignalService {

    private final TradingSignalRepository repository;
    private final SignalMapper mapper;
    private final SignalValidator validator;
    private final BinanceService binanceService;

    public TradingSignalService(TradingSignalRepository repository, SignalMapper mapper, 
                                SignalValidator validator, BinanceService binanceService) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.binanceService = binanceService;
    }

    @Transactional
    public SignalResponse createSignal(CreateSignalRequest request) {
        validator.validate(request);
        TradingSignal entity = mapper.toEntity(request);
        entity.setStatus(Status.OPEN);
        entity.setCreatedAt(LocalDateTime.now());
        
        TradingSignal savedEntity = repository.save(entity);
        return mapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<SignalResponse> getAllSignals() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SignalResponse getSignalById(Long id) {
        TradingSignal signal = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Signal not found with id: " + id));
        return mapper.toResponse(signal);
    }

    @Transactional
    public void deleteSignal(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Signal not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public SignalResponse evaluateSignal(Long id) {
        TradingSignal signal = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Signal not found with id: " + id));

        // If it's already in a final state, no transition is allowed
        if (signal.getStatus() != Status.OPEN) {
            return mapper.toResponse(signal);
        }

        BigDecimal currentPrice = binanceService.getCurrentPrice(signal.getSymbol());
        boolean statusChanged = false;

        if (signal.getDirection() == Direction.BUY) {
            if (currentPrice.compareTo(signal.getTargetPrice()) >= 0) {
                signal.setStatus(Status.TARGET_HIT);
                statusChanged = true;
            } else if (currentPrice.compareTo(signal.getStopLoss()) <= 0) {
                signal.setStatus(Status.STOPLOSS_HIT);
                statusChanged = true;
            }
        } else if (signal.getDirection() == Direction.SELL) {
            if (currentPrice.compareTo(signal.getTargetPrice()) <= 0) {
                signal.setStatus(Status.TARGET_HIT);
                statusChanged = true;
            } else if (currentPrice.compareTo(signal.getStopLoss()) >= 0) {
                signal.setStatus(Status.STOPLOSS_HIT);
                statusChanged = true;
            }
        }

        // Check expiry if not hit
        if (!statusChanged && LocalDateTime.now().isAfter(signal.getExpiryTime())) {
            signal.setStatus(Status.EXPIRED);
            statusChanged = true;
        }

        if (statusChanged) {
            signal.setRealizedRoi(calculateROI(signal, currentPrice));
            repository.save(signal);
        }

        return mapper.toResponse(signal);
    }

    private BigDecimal calculateROI(TradingSignal signal, BigDecimal currentPrice) {
        BigDecimal entryPrice = signal.getEntryPrice();
        BigDecimal roi;
        if (signal.getDirection() == Direction.BUY) {
            // (CurrentPrice - EntryPrice) / EntryPrice * 100
            roi = currentPrice.subtract(entryPrice)
                    .divide(entryPrice, 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        } else {
            // (EntryPrice - CurrentPrice) / EntryPrice * 100
            roi = entryPrice.subtract(currentPrice)
                    .divide(entryPrice, 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return roi.setScale(2, RoundingMode.HALF_UP);
    }
}
