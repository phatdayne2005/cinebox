package com.phat.cinebox.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)  // ← tường minh
    public ResponseEntity<?> handleMismatchException(MethodArgumentTypeMismatchException e){
        log.warn("Type mismatch: {}", e.getMessage());
        return ResponseEntity.badRequest().body("Request argument mismatched");  // 400 ← OK
    }

    @ExceptionHandler(Exception.class)  // ← tường minh
    public ResponseEntity<?> handleException(Exception e){
        log.error("Unexpected error: ", e);
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }
}
