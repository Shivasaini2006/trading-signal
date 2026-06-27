package com.example.trading.service;

import com.example.trading.dto.CreateSignalRequest;
import com.example.trading.dto.SignalResponse;
import com.example.trading.entity.TradingSignal;
import com.example.trading.enums.Direction;
import com.example.trading.enums.Status;
import com.example.trading.mapper.SignalMapper;
import com.example.trading.repository.TradingSignalRepository;
import com.example.trading.validation.SignalValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingSignalServiceTest {

    @Mock
    private TradingSignalRepository repository;

    @Mock
    private SignalMapper mapper;

    @Mock
    private SignalValidator validator;

    @Mock
    private BinanceService binanceService;

    @InjectMocks
    private TradingSignalService service;

    private TradingSignal buySignal;
    private TradingSignal sellSignal;

    @BeforeEach
    void setUp() {
        buySignal = TradingSignal.builder()
                .id(1L)
                .symbol("BTCUSDT")
                .direction(Direction.BUY)
                .entryPrice(new BigDecimal("50000"))
                .targetPrice(new BigDecimal("55000"))
                .stopLoss(new BigDecimal("48000"))
                .entryTime(LocalDateTime.now().minusHours(1))
                .expiryTime(LocalDateTime.now().plusHours(1))
                .status(Status.OPEN)
                .build();
                
        sellSignal = TradingSignal.builder()
                .id(2L)
                .symbol("ETHUSDT")
                .direction(Direction.SELL)
                .entryPrice(new BigDecimal("3000"))
                .targetPrice(new BigDecimal("2500"))
                .stopLoss(new BigDecimal("3200"))
                .entryTime(LocalDateTime.now().minusHours(1))
                .expiryTime(LocalDateTime.now().plusHours(1))
                .status(Status.OPEN)
                .build();
    }

    @Test
    void testEvaluateBuySignal_TargetHit() {
        when(repository.findById(1L)).thenReturn(Optional.of(buySignal));
        when(binanceService.getCurrentPrice("BTCUSDT")).thenReturn(new BigDecimal("56000"));
        
        SignalResponse response = new SignalResponse();
        response.setStatus(Status.TARGET_HIT);
        when(mapper.toResponse(any())).thenReturn(response);

        SignalResponse result = service.evaluateSignal(1L);

        assertEquals(Status.TARGET_HIT, buySignal.getStatus());
        assertNotNull(buySignal.getRealizedRoi());
        assertEquals(Status.TARGET_HIT, result.getStatus());
        verify(repository, times(1)).save(buySignal);
    }

    @Test
    void testEvaluateBuySignal_StopLossHit() {
        when(repository.findById(1L)).thenReturn(Optional.of(buySignal));
        when(binanceService.getCurrentPrice("BTCUSDT")).thenReturn(new BigDecimal("47000"));
        
        SignalResponse response = new SignalResponse();
        response.setStatus(Status.STOPLOSS_HIT);
        when(mapper.toResponse(any())).thenReturn(response);

        SignalResponse result = service.evaluateSignal(1L);

        assertEquals(Status.STOPLOSS_HIT, buySignal.getStatus());
        assertNotNull(buySignal.getRealizedRoi());
        verify(repository, times(1)).save(buySignal);
    }

    @Test
    void testEvaluateSellSignal_TargetHit() {
        when(repository.findById(2L)).thenReturn(Optional.of(sellSignal));
        when(binanceService.getCurrentPrice("ETHUSDT")).thenReturn(new BigDecimal("2400"));
        
        SignalResponse response = new SignalResponse();
        response.setStatus(Status.TARGET_HIT);
        when(mapper.toResponse(any())).thenReturn(response);

        SignalResponse result = service.evaluateSignal(2L);

        assertEquals(Status.TARGET_HIT, sellSignal.getStatus());
        assertNotNull(sellSignal.getRealizedRoi());
        verify(repository, times(1)).save(sellSignal);
    }

    @Test
    void testEvaluateSignal_Expired() {
        buySignal.setExpiryTime(LocalDateTime.now().minusMinutes(10));
        when(repository.findById(1L)).thenReturn(Optional.of(buySignal));
        when(binanceService.getCurrentPrice("BTCUSDT")).thenReturn(new BigDecimal("51000")); // Between entry and target
        
        SignalResponse response = new SignalResponse();
        response.setStatus(Status.EXPIRED);
        when(mapper.toResponse(any())).thenReturn(response);

        SignalResponse result = service.evaluateSignal(1L);

        assertEquals(Status.EXPIRED, buySignal.getStatus());
        verify(repository, times(1)).save(buySignal);
    }

    @Test
    void testCreateSignal_CallsValidator() {
        CreateSignalRequest request = new CreateSignalRequest();
        when(mapper.toEntity(any())).thenReturn(buySignal);
        when(repository.save(any())).thenReturn(buySignal);
        when(mapper.toResponse(any())).thenReturn(new SignalResponse());

        service.createSignal(request);

        verify(validator, times(1)).validate(request);
    }
}
