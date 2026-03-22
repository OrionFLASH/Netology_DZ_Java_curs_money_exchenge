package ru.netology.moneytransfer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Тело запроса на перевод между картами (соответствует схеме OpenAPI).
 */
public record TransferRequestDto(
        @NotBlank(message = "Номер карты списания обязателен")
        String cardFromNumber,

        @NotBlank(message = "Срок действия карты списания обязателен")
        String cardFromValidTill,

        @NotBlank(message = "CVV карты списания обязателен")
        String cardFromCVV,

        @NotBlank(message = "Номер карты зачисления обязателен")
        String cardToNumber,

        @NotNull(message = "Сумма перевода обязательна")
        @Valid
        AmountDto amount
) {
}
