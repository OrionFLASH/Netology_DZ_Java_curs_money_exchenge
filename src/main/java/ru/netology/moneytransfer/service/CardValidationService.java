package ru.netology.moneytransfer.service;

import org.springframework.stereotype.Service;
import ru.netology.moneytransfer.exception.BusinessException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Проверка реквизитов банковских карт для учебного сценария (без обращения к платёжным системам).
 */
@Service
public class CardValidationService {

    private static final Pattern DIGITS_ONLY = Pattern.compile("^\\d+$");
    private static final DateTimeFormatter MMYY_SLASH = DateTimeFormatter.ofPattern("MM/uu");

    /**
     * Нормализует номер карты: оставляет только цифры.
     */
    public String normalizeCardNumber(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replaceAll("\\D", "");
    }

    /**
     * Проверяет, что в номере не меньше 16 цифр (как на FRONT).
     */
    public void requireValidCardNumber(String normalized, String fieldLabel) {
        if (normalized.length() < 16) {
            throw BusinessException.badRequest(
                    fieldLabel + ": требуется не менее 16 цифр",
                    1
            );
        }
    }

    /**
     * Проверяет CVV (3 цифры).
     */
    public void requireValidCvv(String cvv) {
        if (cvv == null || !DIGITS_ONLY.matcher(cvv).matches() || cvv.length() < 3) {
            throw BusinessException.badRequest("CVV: требуется не менее 3 цифр", 2);
        }
    }

    /**
     * Проверяет срок действия в формате MM/YY и что карта не просрочена.
     */
    public void requireValidExpiry(String validTill) {
        if (validTill == null || validTill.length() < 4) {
            throw BusinessException.badRequest("Срок действия карты указан некорректно", 3);
        }
        String compact = validTill.replace("/", "").replaceAll("\\D", "");
        if (compact.length() < 4) {
            throw BusinessException.badRequest("Срок действия карты указан некорректно", 3);
        }
        String mm = compact.substring(0, 2);
        String yy = compact.substring(2, 4);
        if (!DIGITS_ONLY.matcher(mm + yy).matches()) {
            throw BusinessException.badRequest("Срок действия карты указан некорректно", 3);
        }
        int month = Integer.parseInt(mm);
        if (month < 1 || month > 12) {
            throw BusinessException.badRequest("Месяц срока действия должен быть от 01 до 12", 4);
        }
        try {
            YearMonth ym = YearMonth.parse(mm + "/" + yy, MMYY_SLASH);
            YearMonth current = YearMonth.from(LocalDate.now());
            if (ym.isBefore(current)) {
                throw BusinessException.badRequest("Срок действия карты истёк", 5);
            }
        } catch (DateTimeParseException e) {
            throw BusinessException.badRequest("Срок действия карты указан некорректно", 3);
        }
    }
}
