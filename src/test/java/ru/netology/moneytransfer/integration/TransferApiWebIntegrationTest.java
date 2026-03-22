package ru.netology.moneytransfer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционный тест полного REST-слоя на встроенной H2 (без Docker).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local-h2")
class TransferApiWebIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /transfer и POST /confirmOperation — полный сценарий (H2)")
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
