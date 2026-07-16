package com.code.tarun.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<?> handleAccount(AccountNotFoundException ex){

        Map<String,Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status",404);
        response.put("error","Not Found");
        response.put("message",ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUser(UserNotFoundException ex){

        Map<String,Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status",404);
        response.put("error","Not Found");
        response.put("message",ex.getMessage());

        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<?> handleBalance(InsufficientBalanceException ex){

        Map<String,Object> response = new LinkedHashMap<>();

        response.put("timestamp",LocalDateTime.now());
        response.put("status",400);
        response.put("error","Bad Request");
        response.put("message",ex.getMessage());

        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<?> handleAmount(InvalidAmountException ex){

        Map<String,Object> response = new LinkedHashMap<>();

        response.put("timestamp",LocalDateTime.now());
        response.put("status",400);
        response.put("error","Bad Request");
        response.put("message",ex.getMessage());

        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}