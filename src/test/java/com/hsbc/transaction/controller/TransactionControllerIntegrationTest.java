package com.hsbc.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsbc.transaction.dto.TransactionDTO;
import com.hsbc.transaction.enums.TransactionType;
import com.hsbc.transaction.model.Money;
import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Currency;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Transaction Controller Integration Tests")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionService transactionService;

    @Test
    @DisplayName("Should perform full lifecycle: Create -> Update -> Delete transaction")
    void shouldPerformFullTransactionLifecycle() throws Exception {
        // Create a new transaction
        TransactionDTO newTransaction = TransactionDTO.builder()
                .description("Test Transaction")
                .amount("100.00")
                .currency("USD")
                .type("PAYMENT")
                .build();

        // Step 1: Create transaction
        MvcResult createResult = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTransaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Test Transaction"))
                .andExpect(jsonPath("$.amount").value("100.00"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.type").value("PAYMENT"))
                .andReturn();

        // Extract created transaction for later use
        String responseContent = createResult.getResponse().getContentAsString();
        String transactionId = objectMapper.readTree(responseContent).get("id").asText();

        // Step 2: Verify the transaction was created
        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.description").value("Test Transaction"));

        // Step 3: Update the transaction
        TransactionDTO updateRequest = TransactionDTO.builder()
                .description("Updated Test Transaction")
                .amount("150.00")
                .currency("USD")
                .type("PAYMENT")
                .build();

        mockMvc.perform(put("/api/transactions/{id}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.description").value("Updated Test Transaction"))
                .andExpect(jsonPath("$.amount").value("150.00"));

        // Step 4: Verify the update
        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Test Transaction"))
                .andExpect(jsonPath("$.amount").value("150.00"));

        // Step 5: Delete the transaction
        mockMvc.perform(delete("/api/transactions/{id}", transactionId))
                .andExpect(status().isNoContent());

        // Step 6: Verify the deletion
        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle invalid transaction ID format")
    void shouldHandleInvalidTransactionId() throws Exception {
        String invalidId = "invalid-id";

        mockMvc.perform(get("/api/transactions/{id}", invalidId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists());
    }

    @Test
    @DisplayName("Should handle non-existent transaction")
    void shouldHandleNonExistentTransaction() throws Exception {
        String nonExistentId = "999999999999999999";

        mockMvc.perform(get("/api/transactions/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").exists());
    }

    @Test
    @DisplayName("Should get all transactions with pagination")
    void shouldGetAllTransactionsWithPagination() throws Exception {
        // Create some test transactions
        createTestTransaction("Transaction 1", "100.00");
        createTestTransaction("Transaction 2", "200.00");

        mockMvc.perform(get("/api/transactions")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").isNumber())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    @DisplayName("Should update transaction successfully")
    void shouldUpdateTransaction() throws Exception {
        // Create a transaction first
        Transaction transaction = transactionService.createTransaction(
                Transaction.builder()
                        .description("Original Transaction")
                        .money(Money.of(new BigDecimal("100.00"), Currency.getInstance("USD")))
                        .type(TransactionType.PAYMENT)
                        .build()
        );

        // Create update request
        TransactionDTO updateRequest = TransactionDTO.builder()
                .description("Updated Transaction")
                .amount("150.00")
                .currency("USD")
                .type("PAYMENT")
                .build();

        mockMvc.perform(put("/api/transactions/{id}", transaction.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transaction.getId().toString()))
                .andExpect(jsonPath("$.description").value("Updated Transaction"))
                .andExpect(jsonPath("$.amount").value("150.00"));
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void shouldDeleteTransaction() throws Exception {
        Transaction transaction = transactionService.createTransaction(
                Transaction.builder()
                        .description("To Delete")
                        .money(Money.of(new BigDecimal("100.00"), Currency.getInstance("USD")))
                        .type(TransactionType.PAYMENT)
                        .build()
        );

        mockMvc.perform(delete("/api/transactions/{id}", transaction.getId().toString()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/transactions/{id}", transaction.getId().toString()))
                .andExpect(status().isNotFound());
    }

    private void createTestTransaction(String description, String amount) {
        transactionService.createTransaction(
                Transaction.builder()
                        .description(description)
                        .money(Money.of(new BigDecimal(amount), Currency.getInstance("USD")))
                        .type(TransactionType.PAYMENT)
                        .build()
        );
    }

    @Test
    @DisplayName("Should handle boundary values for pagination parameters")
    void shouldHandleBoundaryValuesForPaginationParameters() throws Exception {
        // minimum values
        mockMvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk());
        // maximum values
        mockMvc.perform(get("/api/transactions")
                        .param("page", "1024")
                        .param("size", "100"))
                .andExpect(status().isOk());

        // out of range values
        mockMvc.perform(get("/api/transactions")
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/transactions")
                        .param("page", "1025")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }
} 