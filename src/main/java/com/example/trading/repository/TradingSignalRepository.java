package com.example.trading.repository;

import com.example.trading.entity.TradingSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradingSignalRepository extends JpaRepository<TradingSignal, Long> {
}
