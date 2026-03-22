package ru.netology.moneytransfer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Юнит-тесты расчёта комиссии (без Spring-контекста).
 */
class CommissionServiceTest {

    private final CommissionService commissionService = new CommissionService();

    @Test
    @DisplayName("Комиссия не ниже минимума 100 минорных единиц")
    void minFeeApplied() {
        assertThat(commissionService.calculateFeeMinor(1000L)).isEqualTo(100L);
    }

    @Test
    @DisplayName("Комиссия 1% если она больше минимума")
    void percentApplied() {
        assertThat(commissionService.calculateFeeMinor(50_000L)).isEqualTo(500L);
    }
}
