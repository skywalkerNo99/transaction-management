package com.hsbc.transaction.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enumeration of transaction types supported by the system.
 */
@Getter
public enum TransactionType {
    PAYMENT("PAYMENT"),
    TRANSFER("TRANSFER"),
    DEPOSIT("DEPOSIT");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    private static final Map<String, TransactionType> VALUE_TO_TYPE_MAP =
            Arrays.stream(TransactionType.values())
                    .collect(Collectors.toMap(TransactionType::getDisplayName, Function.identity()));

    public static TransactionType byValue(String value) {
        return VALUE_TO_TYPE_MAP.get(value);
    }
}