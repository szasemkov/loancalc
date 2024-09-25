package com.szasemkov.loancalc.exception;

public class LoanPaymentNotFoundException extends RuntimeException{

    public LoanPaymentNotFoundException(String message) {
        super(message);
    }
}