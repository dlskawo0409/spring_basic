package com.dlskawo0409.demo.common.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 필드 유효성 검사 실패 처리기 (이메일 등)
    private Map<String, String> generateErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> handleEnumTypeMismatchException(Exception ex) {
        String message = "올바르지 않은 값입니다.";
        Throwable cause = ex.getCause();
        if (cause instanceof IllegalArgumentException) {
            message = extractEnumValidationMessage(cause.getMessage());
        }
        return new ResponseEntity<>(generateErrorResponse(message), HttpStatus.BAD_REQUEST);
    }

    private String extractEnumValidationMessage(String exceptionMessage) {
        String message = "올바르지 않은 값입니다.";
        String enumClassName = exceptionMessage.substring(exceptionMessage.indexOf("'") + 1, exceptionMessage.lastIndexOf("'"));
        try {
            Class<?> enumClass = Class.forName(enumClassName);
            if (enumClass.isEnum()) {
                String allowedValues = Stream.of(enumClass.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                message = "올바르지 않은 값입니다. 허용되는 값은 [" + allowedValues + "] 입니다.";
            }
        } catch (ClassNotFoundException ignored) {
        }
        return message;
    }

//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<?> handleException(Exception e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//    }


}