package com.hsbc.transaction.service.impl;

import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.repository.TransactionRepository;
import com.hsbc.transaction.service.TransactionService;
import com.hsbc.transaction.exception.TransactionNotFoundException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.hsbc.transaction.enums.TransactionStatus;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileWriter;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Implementation of the TransactionService interface.
 * Delegates business logic to the domain service and handles persistence
 * operations.
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

        // write to pdf
        logger.info("Writing transaction to JSON: {}", savedTransaction);
        writeToJSON(savedTransaction);

        return savedTransaction;
    }

    private void writeToJSON(Transaction transaction) {
        try {
            // Convert transaction to JSON
            String jsonContent = JSON.toJSONString(transaction, JSONWriter.Feature.PrettyFormat); // true for pretty printing
            logger.info("JSON content: {}", jsonContent);
            
            // Write to file
            try (FileWriter writer = new FileWriter("transaction.json")) {
                writer.write(jsonContent);
            }
            
            logger.info("JSON file created successfully for transaction ID: {}", transaction.getId());
        } catch (IOException e) {
            logger.error("Error creating JSON file for transaction ID: " + transaction.getId(), e);
        }
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
    public Page<Transaction> getAllTransactions(Pageable pageable) {
        logger.debug("Fetching all transactions with pageable: {}", pageable);
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        logger.info("Found {} transactions in page {} of size {}",
                transactions.getNumberOfElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
        return transactions;
    }

    @Override
    public int getTotalTransactions() {
        return transactionRepository.count();
    }

    @Override
    @CacheEvict(value = { "transactions", "allTransactions" }, allEntries = true)
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
        existingTransaction.setTimestamp(LocalDateTime.now());

        // Save and return
        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        logger.info("Transaction updated successfully: {}", updatedTransaction);

        return updatedTransaction;
    }

    @Override
    @CacheEvict(value = { "transactions", "allTransactions" }, allEntries = true)
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