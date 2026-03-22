package ru.netology.moneytransfer.service;

/**
 * Абстракция записи результатов перевода (удобно подменять в юнит-тестах).
 */
public interface TransferOperationLogger {

    void appendSuccess(
            String cardFrom,
            String cardTo,
            long amountMinor,
            String currency,
            long feeMinor,
            String operationId
    );

    void appendRejected(
            String cardFrom,
            String cardTo,
            long amountMinor,
            String currency,
            long feeMinor,
            String reason
    );
}
