package com.szasemkov.loancalc.generator;

import com.szasemkov.loancalc.model.LoanPayment;

import java.sql.Date;
import java.time.LocalDate;

public class LoanPaymentGenerator {
    private static final Long LOAN_PAYMENT_1_ID = 1L;
    private static final Integer LOAN_PAYMENT_1_NUMBER_PAYMENT = 1;
    private static final Date LOAN_PAYMENT_1_DATE = Date.valueOf(LocalDate.now());
    private static final Double LOAN_PAYMENT_1_AMOUNT_PAYMENT = 1000.0;
    private static final Double LOAN_PAYMENT_1_PRINCIPAL_PAYMENT = 100.0;
    private static final Double LOAN_PAYMENT_1_INTEREST_PAYMENT = 100.0;
    private static final Double LOAN_PAYMENT_1_REMAINING_PAYMENT = 900.0;
    private static final String LOAN_PAYMENT_1_DESCRIPTION_PAYMENT = "DESCRIPTION_PAYMENT_1";

    private static final Long LOAN_PAYMENT_2_ID = 1L;
    private static final Integer LOAN_PAYMENT_2_NUMBER_PAYMENT = 2;
    private static final Date LOAN_PAYMENT_2_DATE = Date.valueOf(LocalDate.now());
    private static final Double LOAN_PAYMENT_2_AMOUNT_PAYMENT = 1000.0;
    private static final Double LOAN_PAYMENT_2_PRINCIPAL_PAYMENT = 100.0;
    private static final Double LOAN_PAYMENT_2_INTEREST_PAYMENT = 100.0;
    private static final Double LOAN_PAYMENT_2_REMAINING_PAYMENT = 800.0;
    private static final String LOAN_PAYMENT_2_DESCRIPTION_PAYMENT = "DESCRIPTION_PAYMENT_2";

    public static LoanPayment.LoanPaymentBuilder generateLoanPayment1Builder() {
        return LoanPayment.builder()
                .setLoanId(LOAN_PAYMENT_1_ID)
                .setNumberPayment(LOAN_PAYMENT_1_NUMBER_PAYMENT)
                .setDatePayment(LOAN_PAYMENT_1_DATE)
                .setAmountPayment(LOAN_PAYMENT_1_AMOUNT_PAYMENT)
                .setPrincipalDebt(LOAN_PAYMENT_1_PRINCIPAL_PAYMENT)
                .setInterestDebt(LOAN_PAYMENT_1_INTEREST_PAYMENT)
                .setRemainingDebt(LOAN_PAYMENT_1_REMAINING_PAYMENT)
                .setDescriptionPayment(LOAN_PAYMENT_1_DESCRIPTION_PAYMENT);
    }


    public static LoanPayment.LoanPaymentBuilder generateLoanPayment2Builder() {
        return LoanPayment.builder()
                .setLoanId(LOAN_PAYMENT_2_ID)
                .setNumberPayment(LOAN_PAYMENT_2_NUMBER_PAYMENT)
                .setDatePayment(LOAN_PAYMENT_2_DATE)
                .setAmountPayment(LOAN_PAYMENT_2_AMOUNT_PAYMENT)
                .setPrincipalDebt(LOAN_PAYMENT_2_PRINCIPAL_PAYMENT)
                .setInterestDebt(LOAN_PAYMENT_2_INTEREST_PAYMENT)
                .setRemainingDebt(LOAN_PAYMENT_2_REMAINING_PAYMENT)
                .setDescriptionPayment(LOAN_PAYMENT_2_DESCRIPTION_PAYMENT);
    }
}
