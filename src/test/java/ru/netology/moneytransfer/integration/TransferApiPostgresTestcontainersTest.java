package ru.netology.moneytransfer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционный тест с PostgreSQL в Testcontainers (как в реальной среде Docker).
 * Если Docker недоступен, класс тестов не выполняется (см. {@link Testcontainers}).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class TransferApiPostgresTestcontainersTest {

    @Container
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("moneytransfer_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("money-transfer.log.directory", () -> System.getProperty("java.io.tmpdir") + "/mt-tc-logs");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /transfer и /confirmOperation с PostgreSQL (Testcontainers)")
    void transferAndConfirm() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "cardFromNumber", "4111111111111111",
                "cardFromValidTill", "12/30",
                "cardFromCVV", "123",
                "cardToNumber", "5500005555555556",
                "amount", Map.of("value", 1000, "currency", "RUB")
        ));

        String json = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String operationId = objectMapper.readTree(json).get("operationId").asText();

        String confirm = objectMapper.writeValueAsString(Map.of(
                "operationId", operationId,
                "code", "0000"
        ));

        mockMvc.perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(confirm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").value(operationId));
    }
}
