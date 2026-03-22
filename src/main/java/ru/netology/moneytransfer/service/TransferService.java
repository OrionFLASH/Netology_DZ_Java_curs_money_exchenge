package ru.netology.moneytransfer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.moneytransfer.config.MoneyTransferProperties;
import ru.netology.moneytransfer.domain.PendingOperation;
import ru.netology.moneytransfer.dto.ConfirmRequestDto;
import ru.netology.moneytransfer.dto.OperationIdResponseDto;
import ru.netology.moneytransfer.dto.TransferRequestDto;
import ru.netology.moneytransfer.exception.BusinessException;
import ru.netology.moneytransfer.repository.PendingOperationRepository;

import java.util.UUID;

/**
 * Сценарии перевода: создание ожидающей операции и подтверждение кодом.
 */
@Service
public class TransferService {

    private final PendingOperationRepository pendingOperationRepository;
    private final CardValidationService cardValidationService;
    private final CommissionService commissionService;
    private final TransferOperationLogger transferOperationLogger;
    private final MoneyTransferProperties properties;

    public TransferService(
            PendingOperationRepository pendingOperationRepository,
            CardValidationService cardValidationService,
            CommissionService commissionService,
            TransferOperationLogger transferOperationLogger,
            MoneyTransferProperties properties
    ) {
        this.pendingOperationRepository = pendingOperationRepository;
        this.cardValidationService = cardValidationService;
        this.commissionService = commissionService;
        this.transferOperationLogger = transferOperationLogger;
        this.properties = properties;
    }

    /**
     * Принимает реквизиты перевода, валидирует их и сохраняет операцию до подтверждения.
     */
    @Transactional
    public OperationIdResponseDto initiateTransfer(TransferRequestDto request) {
        String from = cardValidationService.normalizeCardNumber(request.cardFromNumber());
        String to = cardValidationService.normalizeCardNumber(request.cardToNumber());
        cardValidationService.requireValidCardNumber(from, "Карта списания");
        cardValidationService.requireValidCardNumber(to, "Карта зачисления");
        cardValidationService.requireValidCvv(request.cardFromCVV());
        cardValidationService.requireValidExpiry(request.cardFromValidTill());

        long amountMinor = request.amount().value();
        long feeMinor = commissionService.calculateFeeMinor(amountMinor);

        String operationId = UUID.randomUUID().toString();
        PendingOperation pending = new PendingOperation(
                operationId,
                from,
                to,
                amountMinor,
                request.amount().currency(),
                feeMinor
        );
        pendingOperationRepository.save(pending);
        return new OperationIdResponseDto(operationId);
    }

    /**
     * Подтверждает операцию кодом; при успехе удаляет ожидающую запись и пишет лог.
     */
    @Transactional
    public OperationIdResponseDto confirmOperation(ConfirmRequestDto request) {
        PendingOperation op = pendingOperationRepository.findById(request.operationId())
                .orElseThrow(() -> BusinessException.badRequest("Операция не найдена или уже завершена", 10));

        String expected = properties.getConfirmation().getDemoCode();
        if (!expected.equals(request.code())) {
            transferOperationLogger.appendRejected(
                    op.getCardFromNumber(),
                    op.getCardToNumber(),
                    op.getAmountValueMinor(),
                    op.getCurrency(),
                    op.getFeeMinor(),
                    "неверный код подтверждения"
            );
            throw BusinessException.badRequest("Неверный код подтверждения", 11);
        }

        transferOperationLogger.appendSuccess(
                op.getCardFromNumber(),
                op.getCardToNumber(),
                op.getAmountValueMinor(),
                op.getCurrency(),
                op.getFeeMinor(),
                op.getOperationId()
        );

        pendingOperationRepository.delete(op);
        return new OperationIdResponseDto(op.getOperationId());
    }
}
