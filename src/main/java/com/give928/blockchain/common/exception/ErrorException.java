package com.give928.blockchain.common.exception;

import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException {
    private final int code;
    private final String message;
    private final String data;

    public ErrorException(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
