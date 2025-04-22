package com.hsbc.transaction.exception;

import com.hsbc.transaction.enums.ErrorCode;
import com.hsbc.transaction.model.ErrorResp;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling across all @RequestMapping methods.
 * Translates exceptions into appropriate HTTP responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles TransactionNotFoundException.
     * Returns HTTP 404 (Not Found) with error message.
     * 
     * @param ex the exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResp> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        ErrorResp errorResp = new ErrorResp(ErrorCode.TRANSACTION_ID_NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(errorResp, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DuplicateTransactionException.
     * Returns HTTP 409 (Conflict) with error message.
     * 
     * @param ex the exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<ErrorResp> handleDuplicateTransactionException(DuplicateTransactionException ex) {
        ErrorResp errorResp = new ErrorResp(ErrorCode.DUPLICATE_TRANSACTION, ex.getMessage());
        return new ResponseEntity<>(errorResp, HttpStatus.CONFLICT);
    }

    /**
     * Handles validation errors.
     * Returns HTTP 400 (Bad Request) with validation error details.
     * 
     * @param ex the exception containing validation errors
     * @return ResponseEntity with field-level error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResp> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String msg = "Validation exception";
        if (ex.getBindingResult().hasErrors()) {
            msg = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        }
        ErrorResp errorResp = new ErrorResp(ErrorCode.INVALID_ARGUMENT, msg);
        return new ResponseEntity<>(errorResp, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ConstraintViolationException.
     * @param ex the exception containing constraint violations
     * @return ResponseEntity with field-level error details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResp> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElse("Constraint violation");
        ErrorResp errorResp = new ErrorResp(ErrorCode.INVALID_ARGUMENT, msg);
        return new ResponseEntity<>(errorResp, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other unhandled exceptions.
     * Returns HTTP 500 (Internal Server Error) with generic error message.
     * 
     * @param ex the exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResp> handleAllExceptions(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorResp errorResp = new ErrorResp(ErrorCode.SYSTEM_INNER_ERROR, "An unexpected error occurred");
        return new ResponseEntity<>(errorResp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 