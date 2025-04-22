package com.hsbc.transaction.repository;

import com.hsbc.transaction.config.IdGeneratorConfig;
import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.util.SnowflakeIdGenerator;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repository interface for Transaction entity.
 * Follows DDD principles for persistence operations.
 */
@Repository
public class TransactionRepository {
    private final Map<Long, Transaction> transactions = new ConcurrentHashMap<>();

    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public TransactionRepository(SnowflakeIdGenerator snowflakeIdGenerator) {
        this.snowflakeIdGenerator = snowflakeIdGenerator;
    }

    /**
     * Saves a transaction to the repository.
     *
     * @param transaction the transaction to save
     * @return the saved transaction
     */
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            // Generate a new ID if not provided
            transaction.setId(snowflakeIdGenerator.nextId());
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    /**
     * Finds a transaction by its ID.
     *
     * @param id the ID of the transaction
     * @return an Optional containing the found transaction, or empty if not found
     */
    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(transactions.get(id));
    }

    /**
     * Returns all transactions with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return a list of transactions
     */
    public List<Transaction> findAll(int page, int size) {
        List<Transaction> allTransactions = new ArrayList<>(transactions.values());
        int start = page * size;
        int end = Math.min(start + size, allTransactions.size());
        
        if (start >= allTransactions.size()) {
            return new ArrayList<>();
        }
        
        return allTransactions.subList(start, end);
    }

    /**
     * Counts the number of transactions in the repository.
     *
     * @return the number of transactions
     */
    public int count() {
        return transactions.size();
    }

    /**
     * Deletes a transaction by its ID.
     *
     * @param id the ID of the transaction to delete
     * @return true if the transaction was deleted, false if it didn't exist
     */
    public boolean deleteById(Long id) {
        return transactions.remove(id) != null;
    }

    /**
     * Checks if a transaction exists by its ID.
     *
     * @param id the ID to check
     * @return true if the transaction exists, false otherwise
     */
    public boolean existsById(Long id) {
        return transactions.containsKey(id);
    }

    /**
     * Deletes all transactions from the repository.
     */
    public void deleteAll() {
        transactions.clear();
    }
} 