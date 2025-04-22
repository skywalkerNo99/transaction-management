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
    @Length(min = 1, max = 32, message = "id length must be between 0 and 32")
    private String id;
    
    @NotBlank(message = "Description is required")
    @Length(max = 1024, message = "description length must be between 0 and 1024")
    private String description;
    
    @NotBlank(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
    @DecimalMax(value = "1000000", inclusive = false, message = "Amount must be less than 1000000")
    @Digits(integer = 7, fraction = 2, message = "Amount must be a valid decimal number with up to 7 digits and 2 decimal")
    private String amount;
    
    @NotNull(message = "Currency is required")
    @Length(min = 1, max = 32, message = "currency length must be between 1 and 32")
    private String currency;

    @ValidTransactionType
    @NotBlank(message = "Type is required")
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