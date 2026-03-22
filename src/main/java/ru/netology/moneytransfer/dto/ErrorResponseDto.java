package ru.netology.moneytransfer.dto;

/**
 * Тело ошибки клиентского API (поля message и id по спецификации).
 */
public record ErrorResponseDto(String message, int id) {
}
