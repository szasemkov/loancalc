package com.szasemkov.loancalc.controller;


import com.szasemkov.loancalc.dto.ErrorResponse;
import com.szasemkov.loancalc.exception.BadRequestException;
import com.szasemkov.loancalc.exception.LoanCalcException;
import com.szasemkov.loancalc.exception.LoanDeleteException;
import com.szasemkov.loancalc.exception.LoanNotFoundException;
import com.szasemkov.loancalc.exception.LoanPaymentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.szasemkov.loancalc.model.InternalErrorStatus.BAD_REQUEST;
import static com.szasemkov.loancalc.model.InternalErrorStatus.LOANPAYMENT_DOES_NOT_EXIST;
import static com.szasemkov.loancalc.model.InternalErrorStatus.LOAN_ALREADY_CALCULATED;
import static com.szasemkov.loancalc.model.InternalErrorStatus.LOAN_CALCULATED;
import static com.szasemkov.loancalc.model.InternalErrorStatus.LOAN_DOES_NOT_EXIST;


@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoanNotFoundException(LoanNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(LOAN_DOES_NOT_EXIST, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LoanDeleteException.class)
    public ResponseEntity<ErrorResponse> handleLoanDeleteException(LoanDeleteException e) {
        ErrorResponse errorResponse = new ErrorResponse(LOAN_CALCULATED, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoanPaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoanPaymentNotFoundException(LoanPaymentNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(LOANPAYMENT_DOES_NOT_EXIST, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LoanCalcException.class)
    public ResponseEntity<ErrorResponse> handleLoanCalcException(LoanCalcException e) {
        ErrorResponse errorResponse = new ErrorResponse(LOAN_ALREADY_CALCULATED, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        ErrorResponse errorResponse = new ErrorResponse(BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
