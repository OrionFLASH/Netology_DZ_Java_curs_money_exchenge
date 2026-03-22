package ru.netology.moneytransfer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Ожидающая подтверждения операция перевода (хранится до вызова /confirmOperation).
 */
@Entity
@Table(name = "pending_operations")
public class PendingOperation {

    @Id
    @Column(length = 64)
    private String operationId;

    @Column(nullable = false, length = 32)
    private String cardFromNumber;

    @Column(nullable = false, length = 32)
    private String cardToNumber;

    @Column(nullable = false)
    private long amountValueMinor;

    @Column(nullable = false, length = 8)
    private String currency;

    @Column(nullable = false)
    private long feeMinor;

    public PendingOperation() {
    }

    public PendingOperation(
            String operationId,
            String cardFromNumber,
            String cardToNumber,
            long amountValueMinor,
            String currency,
            long feeMinor
    ) {
        this.operationId = operationId;
        this.cardFromNumber = cardFromNumber;
        this.cardToNumber = cardToNumber;
        this.amountValueMinor = amountValueMinor;
        this.currency = currency;
        this.feeMinor = feeMinor;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getCardFromNumber() {
        return cardFromNumber;
    }

    public void setCardFromNumber(String cardFromNumber) {
        this.cardFromNumber = cardFromNumber;
    }

    public String getCardToNumber() {
        return cardToNumber;
    }

    public void setCardToNumber(String cardToNumber) {
        this.cardToNumber = cardToNumber;
    }

    public long getAmountValueMinor() {
        return amountValueMinor;
    }

    public void setAmountValueMinor(long amountValueMinor) {
        this.amountValueMinor = amountValueMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getFeeMinor() {
        return feeMinor;
    }

    public void setFeeMinor(long feeMinor) {
        this.feeMinor = feeMinor;
    }
}
