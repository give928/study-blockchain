package com.give928.blockchain.common.exception;

import com.give928.blockchain.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalRestControllerExceptionHandler {
    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(ErrorException e) {
        log.error("handle ErrorException", e);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getClass().getName(), e.getCode(), e.getMessage(), e.getData()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("handle RuntimeException", e);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getClass().getName(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handle Exception", e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(e.getClass().getName(), e.getMessage()));
    }
}
