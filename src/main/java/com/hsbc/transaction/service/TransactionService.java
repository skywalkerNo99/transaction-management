package com.hsbc.transaction.service;

import com.hsbc.transaction.exception.DuplicateTransactionException;
import com.hsbc.transaction.exception.TransactionNotFoundException;
import com.hsbc.transaction.model.Transaction;
import java.util.List;

/**
 * Service interface for managing transactions.
 * Defines the core business operations for transaction management.
 */
public interface TransactionService {
    /**
     * Creates a new transaction in the system.
     * 
     * @param transaction The transaction to create
     * @return The created transaction with generated ID and timestamp
     * @throws DuplicateTransactionException if a transaction with the same ID already exists
     */
    Transaction createTransaction(Transaction transaction);

    /**
     * Retrieves a specific transaction by ID.
     * 
     * @param id The ID of the transaction to retrieve
     * @return The found transaction
     * @throws TransactionNotFoundException if no transaction is found with the given ID
     */
    Transaction getTransaction(Long id);

    /**
     * Retrieves all transactions with pagination support.
     * 
     * @param page The page number
     * @param size The number of transactions per page
     * @return List of transactions
     */
    List<Transaction> getAllTransactions(int page, int size);

    /**
     * Updates an existing transaction.
     * 
     * @param id The ID of the transaction to update
     * @param transaction The new transaction data
     * @return The updated transaction
     * @throws TransactionNotFoundException if no transaction is found with the given ID
     */
    Transaction updateTransaction(Long id, Transaction transaction);

    /**
     * Deletes a transaction from the system.
     * 
     * @param id The ID of the transaction to delete
     * @throws TransactionNotFoundException if no transaction is found with the given ID
     */
    void deleteTransaction(Long id);

    /**
     * Retrieves the total number of transactions.
     * 
     * @return The total number of transactions
     */
    int getTotalTransactions();
} 