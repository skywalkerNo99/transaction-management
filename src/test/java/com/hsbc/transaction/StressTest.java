package com.hsbc.transaction;

import com.hsbc.transaction.enums.TransactionType;
import com.hsbc.transaction.model.Money;
import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.service.TransactionService;
import com.hsbc.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Transaction System Stress Tests")
class StressTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    private static final int CONCURRENT_USERS = 50;
    private static final int TRANSACTIONS_PER_USER = 20;
    private static final int TOTAL_TRANSACTIONS = CONCURRENT_USERS * TRANSACTIONS_PER_USER;

    @BeforeEach
    void setUp() {
        // Clear all transactions before each test
        transactionRepository.deleteAll();
    }

    @Test
    @DisplayName("Should handle concurrent transaction creation")
    void shouldHandleConcurrentTransactionCreation() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Create transactions concurrently
        for (int user = 0; user < CONCURRENT_USERS; user++) {
            final int userId = user;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (int i = 0; i < TRANSACTIONS_PER_USER; i++) {
                    try {
                        Transaction transaction = Transaction.builder()
                                .description("Stress Test Transaction - User " + userId + " - " + i)
                                .money(Money.of(new BigDecimal("100.00"), Currency.getInstance("USD")))
                                .type(TransactionType.PAYMENT)
                                .build();

                        Transaction created = transactionService.createTransaction(transaction);
                        assertNotNull(created.getId());
                    } catch (Exception e) {
                        fail("Transaction creation failed: " + e.getMessage());
                    }
                }
            }, executorService);
            futures.add(future);
        }

        // Wait for all transactions to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);

        // Verify all transactions were created
        List<Transaction> allTransactions = transactionService.getAllTransactions(0, TOTAL_TRANSACTIONS);
        assertEquals(TOTAL_TRANSACTIONS, allTransactions.size(),
                "Expected " + TOTAL_TRANSACTIONS + " transactions to be created");

        executorService.shutdown();
    }

    @Test
    @DisplayName("Should handle concurrent reads and writes")
    void shouldHandleConcurrentReadsAndWrites() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<Long> transactionIds = new ArrayList<>();

        // Create some initial transactions
        for (int i = 0; i < 10; i++) {
            Transaction transaction = Transaction.builder()
                    .description("Initial Transaction " + i)
                    .money(Money.of(new BigDecimal("100.00"), Currency.getInstance("USD")))
                    .type(TransactionType.PAYMENT)
                    .build();
            transactionIds.add(transactionService.createTransaction(transaction).getId());
        }

        // Perform concurrent reads and writes
        for (int user = 0; user < CONCURRENT_USERS; user++) {
            final int userId = user;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // Read operations
                    for (Long id : transactionIds) {
                        Transaction transaction = transactionService.getTransaction(id);
                        assertNotNull(transaction);
                    }

                    // Write operations
                    Transaction transaction = Transaction.builder()
                            .description("Concurrent Transaction - User " + userId)
                            .money(Money.of(new BigDecimal("100.00"), Currency.getInstance("USD")))
                            .type(TransactionType.PAYMENT)
                            .build();
                    Transaction created = transactionService.createTransaction(transaction);
                    assertNotNull(created.getId());

                    // Read all with pagination
                    List<Transaction> transactions = transactionService.getAllTransactions(0, 10);
                    assertFalse(transactions.isEmpty());
                } catch (Exception e) {
                    fail("Concurrent operation failed: " + e.getMessage());
                }
            }, executorService);
            futures.add(future);
        }

        // Wait for all operations to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);

        executorService.shutdown();
    }

    @Test
    @DisplayName("Should handle concurrent updates")
    void shouldHandleConcurrentUpdates() throws Exception {
        // Create a transaction to update
        Transaction initial = transactionService.createTransaction(
                Transaction.builder()
                        .description("Transaction to Update")
                        .money(Money.of(new BigDecimal("100.00"), Currency.getInstance("USD")))
                        .type(TransactionType.PAYMENT)
                        .build()
        );

        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Perform concurrent updates
        for (int user = 0; user < CONCURRENT_USERS; user++) {
            final int userId = user;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Transaction updateRequest = Transaction.builder()
                            .description("Updated by User " + userId)
                            .money(Money.of(new BigDecimal("150.00"), Currency.getInstance("USD")))
                            .type(TransactionType.PAYMENT)
                            .build();

                    Transaction updated = transactionService.updateTransaction(initial.getId(), updateRequest);
                    assertNotNull(updated);
                    assertEquals(initial.getId(), updated.getId());
                } catch (Exception e) {
                    // Some updates may fail due to concurrent modifications, which is expected
                    assertTrue(e.getMessage().contains("not found") || e.getMessage().contains("concurrent"));
                }
            }, executorService);
            futures.add(future);
        }

        // Wait for all updates to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);

        executorService.shutdown();
    }
} 