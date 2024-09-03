package com.szasemkov.loancalc.exception;

public class LoanNotFoundException extends RuntimeException{

    public LoanNotFoundException(String message) {
        super(message);
    }
}
