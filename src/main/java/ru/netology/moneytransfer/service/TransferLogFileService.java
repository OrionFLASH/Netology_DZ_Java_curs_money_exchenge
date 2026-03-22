package ru.netology.moneytransfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.netology.moneytransfer.config.MoneyTransferProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Запись журнала переводов в файл в произвольном читаемом формате.
 * Фиксируются дата, время, карты, сумма, комиссия и результат операции.
 */
@Service
public class TransferLogFileService implements TransferOperationLogger {

    private static final Logger log = LoggerFactory.getLogger(TransferLogFileService.class);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MoneyTransferProperties properties;

    public TransferLogFileService(MoneyTransferProperties properties) {
        this.properties = properties;
    }

    /**
     * Добавляет строку об успешно подтверждённом переводе.
     */
    public void appendSuccess(
            String cardFrom,
            String cardTo,
            long amountMinor,
            String currency,
            long feeMinor,
            String operationId
    ) {
        String line = String.format(
                "%s | УСПЕХ | operationId=%s | списание=%s | зачисление=%s | сумма=%d %s | комиссия=%d | результат=выполнено%n",
                LocalDateTime.now().format(TS),
                operationId,
                cardFrom,
                cardTo,
                amountMinor,
                currency,
                feeMinor
        );
        writeLine(line);
    }

    /**
     * Запись об отклонённой операции (например, неверный код).
     */
    public void appendRejected(
            String cardFrom,
            String cardTo,
            long amountMinor,
            String currency,
            long feeMinor,
            String reason
    ) {
        String line = String.format(
                "%s | ОТКАЗ | списание=%s | зачисление=%s | сумма=%d %s | комиссия=%d | результат=%s%n",
                LocalDateTime.now().format(TS),
                cardFrom,
                cardTo,
                amountMinor,
                currency,
                feeMinor,
                reason
        );
        writeLine(line);
    }

    private void writeLine(String line) {
        try {
            Path dir = Path.of(properties.getLog().getDirectory());
            Files.createDirectories(dir);
            Path file = dir.resolve(properties.getLog().getFileName());
            Files.writeString(
                    file,
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            log.error("Не удалось записать лог перевода: {}", e.getMessage());
            throw new IllegalStateException("Ошибка записи лога переводов", e);
        }
    }

}
