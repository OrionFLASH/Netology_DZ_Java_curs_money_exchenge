package ru.netology.moneytransfer.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Подтверждение операции перевода кодом верификации.
 */
public record ConfirmRequestDto(
        @NotBlank(message = "Идентификатор операции обязателен")
        String operationId,

        @NotBlank(message = "Код подтверждения обязателен")
        String code
) {
}
