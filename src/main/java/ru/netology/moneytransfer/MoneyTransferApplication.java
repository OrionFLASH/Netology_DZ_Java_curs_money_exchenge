package ru.netology.moneytransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа Spring Boot-приложения «Сервис перевода денег».
 * <p>
 * Реализует REST API по спецификации OpenAPI для интеграции с FRONT-приложением
 * (перевод с карты на карту, подтверждение операции кодом).
 */
@SpringBootApplication
public class MoneyTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyTransferApplication.class, args);
    }
}
