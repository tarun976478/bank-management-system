package com.code.tarun.exception;

public class InvalidAccountException  extends RuntimeException {
    public InvalidAccountException(String message) {
        super(message);
    }
}
