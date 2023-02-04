package com.give928.blockchain.common.response;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String exception;
    private final Integer code;
    private final String message;
    private final String data;

    public ErrorResponse(String exception, String message) {
        this(exception, null, message, null);
    }

    public ErrorResponse(String exception, Integer code, String message, String data) {
        this.exception = exception;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
