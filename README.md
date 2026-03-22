# Сервис перевода денег (курсовой проект Нетологии)

Учебный REST-сервис на **Spring Boot** для перевода денег с карты на карту по [спецификации OpenAPI](Docs/MoneyTransferServiceSpecification.yaml). Предназначен для работы с FRONT-приложением [card-transfer](https://github.com/serp-ya/card-transfer) без изменений на стороне клиента.

Исходные материалы задания лежат в каталоге [`Docs/`](Docs/).

## Технологии

- Java 17, Spring Boot 3.2, Spring Data JPA, валидация Bean Validation  
- Сборка: **Maven** (`pom.xml`)  
- База данных: PostgreSQL (в Docker Compose) или встроенная H2 (профиль `local-h2`)  
- Тесты: JUnit 5, Mockito, Spring Test, Testcontainers (PostgreSQL; при отсутствии Docker тест с Testcontainers пропускается)

## Структура проекта

| Путь | Назначение |
|------|------------|
| `src/main/java/ru/netology/moneytransfer/` | Основной код: контроллеры, сервисы, DTO, сущности, обработка ошибок |
| `src/main/resources/application.yml` | Порт, БД, настройки лог-файла и демо-кода подтверждения |
| `src/test/java/` | Юнит- и интеграционные тесты |
| `Docs/` | Текст задания и копия OpenAPI YAML |
| `Dockerfile`, `docker-compose.yml` | Сборка образа и запуск API с PostgreSQL |

## Запуск локально (без Docker)

Требования: JDK 17+, Maven 3.9+.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local-h2
```

Сервис слушает порт **5500** (как в методичке для демо FRONT: `http://localhost:5500/`).

Переменные БД для режима с PostgreSQL на машине см. [`.env.example`](.env.example).

## Запуск через Docker Compose

Требования: Docker, Docker Compose.

```bash
docker compose up --build
```

- API: `http://localhost:5500`  
- PostgreSQL: `localhost:5432` (учётные данные как в `docker-compose.yml`)  
- Журнал переводов монтируется в каталог `./log/transfers.log` на хосте.

## Интеграция с FRONT

1. Клонируйте [card-transfer](https://github.com/serp-ya/card-transfer).  
2. В `.env` задайте: `REACT_APP_API_URL=http://localhost:5500` (без хвоста `/transfer`).  
3. Запустите FRONT: `npm i` и `npm run start`.  
4. Убедитесь, что API запущен на порту 5500.

Для браузерных запросов с другого origin включён **CORS** (`CorsConfig`): `http://localhost:3000`, `http://127.0.0.1:3000`, `https://serp-ya.github.io` (демо на GitHub Pages обращается к вашему `localhost:5500`).

Демо-код подтверждения (учебный сценарий без SMS) по умолчанию: **`0000`** (настраивается в `application.yml`, ключ `money-transfer.confirmation.demo-code`).

## Эндпоинты и примеры запросов

### `POST /transfer`

```bash
curl -s -X POST http://localhost:5500/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "cardFromNumber": "4111111111111111",
    "cardFromValidTill": "12/30",
    "cardFromCVV": "123",
    "cardToNumber": "5500005555555556",
    "amount": { "value": 10000, "currency": "RUB" }
  }'
```

Ответ `200`: `{"operationId":"<uuid>"}`.

Поле `amount.value` — целое число в **минорных единицах** (например, копейки для RUB).

### `POST /confirmOperation`

```bash
curl -s -X POST http://localhost:5500/confirmOperation \
  -H "Content-Type: application/json" \
  -d '{"operationId":"<uuid из ответа /transfer>","code":"0000"}'
```

### Лог переводов

Файл по умолчанию: `log/transfers.log` (относительно рабочей директории процесса). В строке фиксируются дата и время, полные номера карт (учебный режим), сумма, комиссия, результат.

## Основные классы и компоненты

| Имя | Роль |
|-----|------|
| `MoneyTransferApplication` | Точка входа Spring Boot |
| `TransferController` | HTTP `POST /transfer`, `POST /confirmOperation` |
| `TransferService` | Создание ожидающей операции, подтверждение кодом, вызов логгера |
| `CardValidationService` | Проверка номера карты, срока MM/YY, CVV |
| `CommissionService` | Комиссия: 1% от суммы, не менее 100 минорных единиц |
| `TransferLogFileService` | Запись строк в файл журнала (`TransferOperationLogger`) |
| `PendingOperation` / `PendingOperationRepository` | Хранение неподтверждённых операций в БД |
| `GlobalExceptionHandler` | Ответы с телом `{ "message", "id" }` для 4xx/5xx |
| `MoneyTransferProperties` | Демо-код и путь к лог-файлу |
| `CorsConfig` | CORS для FRONT (React и демо на GitHub Pages) |

## Тесты

```bash
mvn test
```

- Юнит-тесты: `CommissionServiceTest`, `TransferServiceTest` (Mockito).  
- Интеграция на H2: `TransferApiWebIntegrationTest`.  
- Интеграция с PostgreSQL в Testcontainers: `TransferApiPostgresTestcontainersTest` (выполняется при доступном Docker; иначе класс помечается как пропущенный).

На JDK новее 17 для Mockito в Surefire задан флаг `-Dnet.bytebuddy.experimental=true`.

## История версий

| Версия | Изменения |
|--------|-----------|
| 1.0.0 | Первоначальная реализация: REST по OpenAPI, JPA, файл логов, Docker Compose, тесты Mockito и Testcontainers |
