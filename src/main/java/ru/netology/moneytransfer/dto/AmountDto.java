package ru.netology.moneytransfer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Сумма перевода в минорных единицах валюты (например, копейки для RUB).
 */
public record AmountDto(
        @NotNull(message = "Сумма обязательна")
        @Min(value = 1, message = "Сумма должна быть больше нуля")
        Integer value,

        @NotBlank(message = "Валюта обязательна")
        String currency
) {
}
