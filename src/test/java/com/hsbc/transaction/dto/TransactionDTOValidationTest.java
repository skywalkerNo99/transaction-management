package com.hsbc.transaction.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should pass validation for valid TransactionDTO")
    void validate_ValidTransactionDTO() {
        // Arrange
        TransactionDTO dto = TransactionDTO.builder()
                .id("1")
                .description("Valid description")
                .amount("100.00")
                .currency("USD")
                .type("PAYMENT")
                .build();

        // Act
        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "No validation errors should occur for a valid DTO");
    }

    @Test
    @DisplayName("Should fail validation when description is blank")
    void validate_BlankDescription() {
        // Arrange
        TransactionDTO dto = TransactionDTO.builder()
                .id("1")
                .description("")
                .amount("100.00")
                .currency("USD")
                .type("PAYMENT")
                .build();

        // Act
        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Validation errors should occur for blank description");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Description is required")));
    }

    @Test
    @DisplayName("Should fail validation when amount is null")
    void validate_NullAmount() {
        // Arrange
        TransactionDTO dto = TransactionDTO.builder()
                .id("1")
                .description("Valid description")
                .amount(null)
                .currency("USD")
                .type("PAYMENT")
                .build();

        // Act
        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Validation errors should occur for null amount");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Amount is required")));
    }

    @Test
    @DisplayName("Should fail validation when amount exceeds maximum")
    void validate_AmountExceedsMaximum() {
        // Arrange
        TransactionDTO dto = TransactionDTO.builder()
                .id("1")
                .description("Valid description")
                .amount("1000001")
                .currency("USD")
                .type("PAYMENT")
                .build();

        // Act
        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Validation errors should occur for amount exceeding maximum");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Amount must be less than 1000000")));
    }

    @Test
    @DisplayName("Should fail validation when description exceeds maximum length")
    void validate_DescriptionExceedsMaxLength() {
        // Arrange
        String longDescription = "a".repeat(1025); // 超过1024字符
        TransactionDTO dto = TransactionDTO.builder()
                .id("1")
                .description(longDescription)
                .amount("100.00")
                .currency("USD")
                .type("PAYMENT")
                .build();

        // Act
        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Validation errors should occur for description exceeding maximum length");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("length must be between 0 and 1024")));
    }
}