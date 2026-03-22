package ru.netology.moneytransfer.service;

import org.springframework.stereotype.Service;

/**
 * Расчёт комиссии за перевод в минорных единицах валюты.
 * <p>
 * Правило: 1% от суммы, не менее 100 минорных единиц (например, 100 копеек для RUB).
 */
@Service
public class CommissionService {

    private static final double PERCENT = 0.01;
    private static final long MIN_FEE_MINOR = 100L;

    /**
     * Возвращает комиссию в минорных единицах.
     */
    public long calculateFeeMinor(long amountMinor) {
        long byPercent = Math.round(amountMinor * PERCENT);
        return Math.max(byPercent, MIN_FEE_MINOR);
    }
}
