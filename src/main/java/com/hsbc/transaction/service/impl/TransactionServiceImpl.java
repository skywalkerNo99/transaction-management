package com.hsbc.transaction.service.impl;

import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.repository.TransactionRepository;
import com.hsbc.transaction.service.TransactionService;
import com.hsbc.transaction.exception.TransactionNotFoundException;
import com.hsbc.transaction.enums.TransactionStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of the TransactionService interface.
 * Delegates business logic to the domain service and handles persistence operations.
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @CacheEvict(value = "allTransactions", allEntries = true)
    public Transaction createTransaction(Transaction transactionRequest) {
        logger.info("Creating new transaction with description: {}", transactionRequest.getDescription());

        // Check for duplicate transaction
        if (transactionRequest.getId() != null && transactionRepository.existsById(transactionRequest.getId())) {
            logger.error("Transaction with ID {} already exists", transactionRequest.getId());
            throw new IllegalArgumentException("Transaction with ID " + transactionRequest.getId() + " already exists");
        }

        // Validate amount
        if (transactionRequest.getMoney().getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        
        // Save and return
        Transaction savedTransaction = transactionRepository.save(transactionRequest);
        logger.info("Transaction created successfully with ID: {}", savedTransaction.getId());
        
        return savedTransaction;
    }

    @Override
    @Cacheable(value = "transactions", key = "#id")
    public Transaction getTransaction(Long id) {
        logger.debug("Fetching transaction with ID: {}", id);
        return transactionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Transaction with ID {} not found", id);
                    return new TransactionNotFoundException("Transaction with ID " + id + " not found");
                });
    }

    @Override
    @Cacheable(value = "allTransactions")
    public List<Transaction> getAllTransactions(int page, int size) {
        logger.debug("Fetching all transactions with page: {} and size: {}", page, size);
        List<Transaction> transactions = transactionRepository.findAll(page, size);
        logger.info("Found {} transactions", transactions.size());
        return transactions;
    }

    @Override
    public int getTotalTransactions() {
        return transactionRepository.count();
    }

    @Override
    @CacheEvict(value = {"transactions", "allTransactions"}, allEntries = true)
    public Transaction updateTransaction(Long id, Transaction transactionRequest) {
        logger.debug("Updating transaction with ID: {}", id);
        
        // Get existing transaction
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Transaction with ID {} not found for update", id);
                    return new TransactionNotFoundException("Transaction with ID " + id + " not found");
                });
        
        // Update fields
        existingTransaction.setDescription(transactionRequest.getDescription());
        existingTransaction.setMoney(transactionRequest.getMoney());
        existingTransaction.setType(transactionRequest.getType());
        // Set status to completed
        existingTransaction.setStatus(TransactionStatus.COMPLETED);
        
        // Save and return
        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        logger.info("Transaction updated successfully: {}", updatedTransaction);
        
        return updatedTransaction;
    }

    @Override
    @CacheEvict(value = {"transactions", "allTransactions"}, allEntries = true)
    public void deleteTransaction(Long id) {
        logger.info("Attempting to delete transaction with ID: {}", id);
        
        if (!transactionRepository.existsById(id)) {
            logger.error("Transaction with ID {} not found for deletion", id);
            throw new TransactionNotFoundException("Transaction with ID " + id + " not found");
        }
        
        boolean deleted = transactionRepository.deleteById(id);
        if (deleted) {
            logger.info("Transaction with ID {} deleted successfully", id);
        } else {
            logger.error("Failed to delete transaction with ID {}", id);
            throw new TransactionNotFoundException("Failed to delete transaction with ID " + id);
        }
    }
} 