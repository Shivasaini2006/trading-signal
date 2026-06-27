package com.example.trading.controller;

import com.example.trading.dto.CreateSignalRequest;
import com.example.trading.dto.SignalResponse;
import com.example.trading.service.TradingSignalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signals")
public class TradingSignalController {

    private final TradingSignalService service;

    public TradingSignalController(TradingSignalService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SignalResponse> createSignal(@Valid @RequestBody CreateSignalRequest request) {
        SignalResponse response = service.createSignal(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SignalResponse>> getAllSignals() {
        return ResponseEntity.ok(service.getAllSignals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignalResponse> getSignalById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSignalById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSignal(@PathVariable Long id) {
        service.deleteSignal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<SignalResponse> evaluateSignalStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.evaluateSignal(id));
    }
}
