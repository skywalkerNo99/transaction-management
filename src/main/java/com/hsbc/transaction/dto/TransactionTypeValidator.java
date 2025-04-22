package com.hsbc.transaction.dto;

import com.hsbc.transaction.enums.TransactionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class TransactionTypeValidator implements ConstraintValidator<ValidTransactionType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return Arrays.stream(TransactionType.values())
                .anyMatch(type -> type.name().equals(value));
    }
}