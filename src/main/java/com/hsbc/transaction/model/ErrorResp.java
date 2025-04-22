package com.hsbc.transaction.model;

import com.hsbc.transaction.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResp {

    int code;

    String message;

    public ErrorResp(ErrorCode errorCode, String message) {
        this.code = errorCode.getCode();
        this.message = message;
    }
}
