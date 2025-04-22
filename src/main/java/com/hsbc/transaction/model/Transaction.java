package com.hsbc.transaction.model;

import java.time.LocalDateTime;

import com.hsbc.transaction.enums.TransactionStatus;
import com.hsbc.transaction.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Long id;
    private String description;
    private Money money;
    private TransactionType type;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    @Builder.Default
    private TransactionStatus status = TransactionStatus.COMPLETED;

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", money=" + money +
                ", type='" + type.name()     + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
} 