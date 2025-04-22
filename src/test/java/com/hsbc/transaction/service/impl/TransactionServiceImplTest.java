package com.hsbc.transaction.service.impl;

import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.model.Money;
import com.hsbc.transaction.enums.TransactionType;
import com.hsbc.transaction.repository.TransactionRepository;
import com.hsbc.transaction.exception.TransactionNotFoundException;
import com.hsbc.transaction.enums.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("TransactionServiceImpl Unit Tests")
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionServiceImpl transactionService;
    private static final Currency USD = Currency.getInstance("USD");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionServiceImpl(transactionRepository);
    }

    @Test
    @DisplayName("Should create transaction successfully")
    void createTransaction_Success() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .description("Test Transaction")
                .money(Money.of(new BigDecimal("100.00"), USD))
                .type(TransactionType.PAYMENT)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = transactionService.createTransaction(transaction);

        // Assert
        assertNotNull(result);
        assertEquals("Test Transaction", result.getDescription());
        assertEquals(new BigDecimal("100.00"), result.getMoney().getAmount());
        assertEquals("USD", result.getMoney().getCurrency().getCurrencyCode());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when creating transaction with duplicate ID")
    void createTransaction_DuplicateId() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(1L)
                .description("Test Transaction")
                .money(Money.of(new BigDecimal("100.00"), USD))
                .type(TransactionType.PAYMENT)
                .build();

        when(transactionRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransaction(transaction),
            "Should throw IllegalArgumentException for duplicate ID"
        );
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when creating transaction with non-positive amount")
    void createTransaction_NonPositiveAmount() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .description("Test Transaction")
                .money(Money.of(new BigDecimal("0.00"), USD))
                .type(TransactionType.PAYMENT)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransaction(transaction),
            "Should throw IllegalArgumentException for non-positive amount"
        );
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should retrieve transaction successfully")
    void getTransaction_Success() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(1L)
                .description("Test Transaction")
                .money(Money.of(new BigDecimal("100.00"), USD))
                .type(TransactionType.PAYMENT)
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // Act
        Transaction result = transactionService.getTransaction(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Transaction", result.getDescription());
        verify(transactionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when transaction not found")
    void getTransaction_NotFound() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> 
            transactionService.getTransaction(1L),
            "Should throw TransactionNotFoundException when transaction not found"
        );
        verify(transactionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should retrieve all transactions successfully")
    void getAllTransactions_Success() {
        // Arrange
        Transaction t1 = Transaction.builder()
                .id(1L)
                .description("Transaction 1")
                .money(Money.of(new BigDecimal("100.00"), USD))
                .build();
        Transaction t2 = Transaction.builder()
                .id(2L)
                .description("Transaction 2")
                .money(Money.of(new BigDecimal("200.00"), USD))
                .build();

        when(transactionRepository.findAll(0, 10)).thenReturn(Arrays.asList(t1, t2));

        // Act
        var result = transactionService.getAllTransactions(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Transaction 1", result.get(0).getDescription());
        assertEquals("Transaction 2", result.get(1).getDescription());
        verify(transactionRepository).findAll(0, 10);
    }

    @Test
    @DisplayName("Should update transaction successfully")
    void updateTransaction_Success() {
        // Arrange
        Transaction existingTransaction = Transaction.builder()
                .id(1L)
                .description("Old Description")
                .money(Money.of(new BigDecimal("100.00"), USD))
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.COMPLETED)
                .build();

        Transaction updateRequest = Transaction.builder()
                .description("New Description")
                .money(Money.of(new BigDecimal("200.00"), USD))
                .type(TransactionType.PAYMENT)
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Transaction result = transactionService.updateTransaction(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("New Description", result.getDescription());
        assertEquals(new BigDecimal("200.00"), result.getMoney().getAmount());
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent transaction")
    void updateTransaction_NotFound() {
        // Arrange
        Transaction updateRequest = Transaction.builder()
                .description("New Description")
                .money(Money.of(new BigDecimal("200.00"), USD))
                .type(TransactionType.PAYMENT)
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> 
            transactionService.updateTransaction(1L, updateRequest),
            "Should throw TransactionNotFoundException when transaction not found"
        );
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void deleteTransaction_Success() {
        // Arrange
        when(transactionRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.deleteById(1L)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> transactionService.deleteTransaction(1L));
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent transaction")
    void deleteTransaction_NotFound() {
        // Arrange
        when(transactionRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> 
            transactionService.deleteTransaction(1L),
            "Should throw TransactionNotFoundException when transaction not found"
        );
        verify(transactionRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should get total number of transactions")
    void getTotalTransactions_Success() {
        // Arrange
        when(transactionRepository.count()).thenReturn(5);

        // Act
        int result = transactionService.getTotalTransactions();

        // Assert
        assertEquals(5, result);
        verify(transactionRepository).count();
    }

    @Test
    @DisplayName("Should throw exception when updating transaction with non-existent ID")
    void updateTransaction_NonExistentId() {
        // Arrange
        Transaction updateRequest = Transaction.builder()
                .description("Updated Description")
                .money(Money.of(new BigDecimal("150.00"), USD))
                .type(TransactionType.TRANSFER)
                .build();

        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () ->
                        transactionService.updateTransaction(999L, updateRequest),
                "Should throw TransactionNotFoundException when updating a transaction with non-existent ID"
        );
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
} 