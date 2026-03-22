package ru.netology.moneytransfer.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Подключение типобезопасных свойств приложения.
 */
@Configuration
@EnableConfigurationProperties(MoneyTransferProperties.class)
public class AppConfig {
}
