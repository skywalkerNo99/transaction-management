package com.hsbc.transaction.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing error messages for the transaction service.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * Error code for duplicate transaction.
     */
    DUPLICATE_TRANSACTION(1001),

    /**
     * Error code for transaction not found.
     */
    TRANSACTION_ID_NOT_FOUND(1002),

    /**
     * Error code for invalid argument.
     */
    INVALID_ARGUMENT(1003),

    /**
     * Error code for system inner error.
     */
    SYSTEM_INNER_ERROR(9999);

    final int code;
}
