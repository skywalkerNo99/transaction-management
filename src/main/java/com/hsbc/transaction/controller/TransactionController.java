package com.hsbc.transaction.controller;

import com.hsbc.transaction.dto.TransactionDTO;
import com.hsbc.transaction.exception.TransactionNotFoundException;
import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing transactions.
 * Provides endpoints for CRUD operations on transactions.
 * All endpoints are under the base path '/api/transactions'.
 */
@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {
    
    private final TransactionService transactionService;

    /**
     * Constructor injection of TransactionService.
     * 
     * @param transactionService the service for handling transaction operations
     */
    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Creates a new transaction.
     * 
     * @param transactionDTO the transaction to create (validated)
     * @return ResponseEntity containing the created transaction and CREATED status
     */
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = transactionService.createTransaction(transactionDTO.toEntity());
        return new ResponseEntity<>(TransactionDTO.fromEntity(transaction), HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific transaction by ID.
     * 
     * @param id the ID of the transaction to retrieve
     * @return ResponseEntity containing the found transaction
     * @throws TransactionNotFoundException if transaction is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable @Digits(integer = 19, fraction = 0, message = "id should be a valid number") String id) {
        Transaction transaction = transactionService.getTransaction(Long.parseLong(id));
        return ResponseEntity.ok(TransactionDTO.fromEntity(transaction));
    }

    /**
     * Retrieves all transactions with pagination support.
     *
     * @param page the page number to retrieve (0-indexed)
     * @param size the number of transactions per page
     * @return ResponseEntity containing the page of transactions
     */
    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") @Range(min = 0, message = "Page must be greater than or equal to 0") int page,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 100, message = "Size must be between 1 and 100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getAllTransactions(pageable);
        Page<TransactionDTO> dtoPage = transactions.map(TransactionDTO::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Updates an existing transaction.
     * 
     * @param id the ID of the transaction to update
     * @param transactionDTO the new transaction data (validated)
     * @return ResponseEntity containing the updated transaction
     * @throws TransactionNotFoundException if transaction is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable String id,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = transactionService.updateTransaction(
            Long.parseLong(id), 
            transactionDTO.toEntity()
        );
        return ResponseEntity.ok(TransactionDTO.fromEntity(transaction));
    }

    /**
     * Deletes a transaction.
     * 
     * @param id the ID of the transaction to delete
     * @return ResponseEntity with no content
     * @throws TransactionNotFoundException if transaction is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable @Digits(integer = 19, fraction = 0, message = "id should be a valid number") String id) {
        transactionService.deleteTransaction(Long.parseLong(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalTransactions() {
        int count = transactionService.getTotalTransactions();
        return ResponseEntity.ok(count);
    }
} 