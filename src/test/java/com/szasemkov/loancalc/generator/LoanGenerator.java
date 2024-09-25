package com.szasemkov.loancalc.generator;

import com.szasemkov.loancalc.model.Loan;

import java.sql.Date;
import java.time.LocalDate;

import static com.szasemkov.loancalc.model.StatusLoan.CALCULATED;
import static com.szasemkov.loancalc.model.StatusLoan.NEW;
import static com.szasemkov.loancalc.model.TypePayment.ANNUIT;
import static com.szasemkov.loancalc.model.TypePayment.DIFFER;

public class LoanGenerator {

    private static final Long LOAN_ID = 1L;
    private static final Double LOAN_AMOUNT = 1000.0;
    private static final Integer LOAN_TERM = 12;
    private static final Double LOAN_INTEREST_RATE = 12.0;
    private static final Date LOAN_DATE = Date.valueOf(LocalDate.now());

    private static final Long LOAN_1_ID = 2L;
    private static final Double LOAN_1_AMOUNT = 2000.0;
    private static final Integer LOAN_1_TERM = 12;
    private static final Double LOAN_1_INTEREST_RATE = 5.0;
    private static final Date LOAN_1_DATE = Date.valueOf(LocalDate.now());

    private static final Long LOAN_2_ID = 3L;
    private static final Double LOAN_2_AMOUNT = 3000.0;
    private static final Integer LOAN_2_TERM = 24;
    private static final Double LOAN_2_INTEREST_RATE = 20.0;
    private static final Date LOAN_2_DATE = Date.valueOf(LocalDate.now());


    public static Loan.LoanBuilder generateLoanAfterCreateBuilder() {
        return Loan.builder()
                .setId(LOAN_ID)
                .setAmount(LOAN_AMOUNT)
                .setTerm(LOAN_TERM)
                .setInterestRate(LOAN_INTEREST_RATE)
                .setLoanDate(LOAN_DATE)
                .setTypePayment(ANNUIT)
                .setStatus(NEW);
    }


    public static Loan.LoanBuilder generateLoan1Builder() {
        return Loan.builder()
                .setId(LOAN_1_ID)
                .setAmount(LOAN_1_AMOUNT)
                .setTerm(LOAN_1_TERM)
                .setInterestRate(LOAN_1_INTEREST_RATE)
                .setLoanDate(LOAN_1_DATE)
                .setTypePayment(ANNUIT)
                .setStatus(NEW);
    }


    public static Loan.LoanBuilder generateLoan2Builder() {
        return Loan.builder()
                .setId(LOAN_2_ID)
                .setAmount(LOAN_2_AMOUNT)
                .setTerm(LOAN_2_TERM)
                .setInterestRate(LOAN_2_INTEREST_RATE)
                .setLoanDate(LOAN_2_DATE)
                .setTypePayment(DIFFER)
                .setStatus(CALCULATED);
    }


    public static Loan.LoanBuilder generateCalculatedLoanBuilder() {
        return Loan.builder()
                .setId(LOAN_ID)
                .setAmount(LOAN_AMOUNT)
                .setTerm(LOAN_TERM)
                .setInterestRate(LOAN_INTEREST_RATE)
                .setLoanDate(LOAN_DATE)
                .setTypePayment(ANNUIT)
                .setStatus(CALCULATED);
    }

}
