package com.sid.digitalbankbackend.exceptions;

public class BalanceNotSufficientException extends Exception {

    public BalanceNotSufficientException(String message) {
        super(message);
    }
}
