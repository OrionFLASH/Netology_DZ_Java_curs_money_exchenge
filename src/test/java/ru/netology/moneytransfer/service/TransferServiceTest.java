package ru.netology.moneytransfer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.moneytransfer.config.MoneyTransferProperties;
import ru.netology.moneytransfer.domain.PendingOperation;
import ru.netology.moneytransfer.dto.AmountDto;
import ru.netology.moneytransfer.dto.ConfirmRequestDto;
import ru.netology.moneytransfer.dto.TransferRequestDto;
import ru.netology.moneytransfer.exception.BusinessException;
import ru.netology.moneytransfer.repository.PendingOperationRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Юнит-тесты сценария перевода с подменой репозитория и файлового логгера (Mockito).
 */
@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private PendingOperationRepository pendingOperationRepository;

    @Mock
    private TransferOperationLogger transferOperationLogger;

    private final CardValidationService cardValidationService = new CardValidationService();
    private final CommissionService commissionService = new CommissionService();
    private final MoneyTransferProperties properties = new MoneyTransferProperties();

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        properties.getConfirmation().setDemoCode("0000");
        transferService = new TransferService(
                pendingOperationRepository,
                cardValidationService,
                commissionService,
                transferOperationLogger,
                properties
        );
    }

    @Test
    @DisplayName("Инициация перевода сохраняет ожидающую операцию")
    void initiatePersistsPending() {
        TransferRequestDto req = new TransferRequestDto(
                "4111111111111111",
                "12/30",
                "123",
                "5500005555555556",
                new AmountDto(10_000, "RUB")
        );
        transferService.initiateTransfer(req);

        ArgumentCaptor<PendingOperation> captor = ArgumentCaptor.forClass(PendingOperation.class);
        verify(pendingOperationRepository).save(captor.capture());
        assertThat(captor.getValue().getCardFromNumber()).isEqualTo("4111111111111111");
        assertThat(captor.getValue().getAmountValueMinor()).isEqualTo(10_000L);
    }

    @Test
    @DisplayName("Неверный код подтверждения: лог отказа и исключение")
    void wrongCodeLogsAndThrows() {
        PendingOperation op = new PendingOperation(
                "op-1",
                "4111111111111111",
                "5500005555555556",
                5000L,
                "RUB",
                100L
        );
        when(pendingOperationRepository.findById("op-1")).thenReturn(Optional.of(op));

        assertThatThrownBy(() -> transferService.confirmOperation(new ConfirmRequestDto("op-1", "9999")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Неверный");

        verify(transferOperationLogger).appendRejected(
                "4111111111111111",
                "5500005555555556",
                5000L,
                "RUB",
                100L,
                "неверный код подтверждения"
        );
        verify(pendingOperationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Верный код: лог успеха и удаление операции")
    void successConfirms() {
        PendingOperation op = new PendingOperation(
                "op-2",
                "4111111111111111",
                "5500005555555556",
                5000L,
                "RUB",
                100L
        );
        when(pendingOperationRepository.findById("op-2")).thenReturn(Optional.of(op));

        transferService.confirmOperation(new ConfirmRequestDto("op-2", "0000"));

        verify(transferOperationLogger).appendSuccess(
                "4111111111111111",
                "5500005555555556",
                5000L,
                "RUB",
                100L,
                "op-2"
        );
        verify(pendingOperationRepository).delete(op);
    }
}
