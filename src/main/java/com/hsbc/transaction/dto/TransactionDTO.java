package com.hsbc.transaction.dto;

import com.hsbc.transaction.enums.TransactionStatus;
import com.hsbc.transaction.model.Money;
import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.enums.TransactionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private String id;
    
    private String description;
    
    private String amount;
    
    private String currency;

    private String type;

    private String timestamp;

    public static TransactionDTO fromEntity(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId() != null ? transaction.getId().toString() : null)
                .description(transaction.getDescription())
                .amount(transaction.getMoney().getAmount().toString())
                .currency(transaction.getMoney().getCurrency().toString())
                .type(transaction.getType().getDisplayName())
                .timestamp(transaction.getTimestamp().toString())
                .build();
    }

    public Transaction toEntity() {
        Transaction transaction = Transaction.builder()
                .description(this.description)
                .money(Money.of(new BigDecimal(this.amount), Currency.getInstance(this.currency)))
                .type(TransactionType.byValue(this.getType()))
                .timestamp(LocalDateTime.now())
                .build();
        if (this.id != null && !this.id.isEmpty()) {
            transaction.setId(Long.parseLong(this.id));
        }
        return transaction;
    }
} 