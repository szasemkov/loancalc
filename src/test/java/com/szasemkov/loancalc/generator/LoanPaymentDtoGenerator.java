package com.szasemkov.loancalc.generator;

import com.szasemkov.loancalc.dto.LoanPaymentPageResponse;
import com.szasemkov.loancalc.dto.LoanPaymentResponse;
import com.szasemkov.loancalc.model.LoanPayment;

import java.util.List;

public class LoanPaymentDtoGenerator {
    public static LoanPaymentResponse.LoanPaymentResponseBuilder loanPayment1ResponseBuilder() {
        LoanPayment loanPayment1 = LoanPaymentGenerator.generateLoanPayment1Builder()
                .build();

        return LoanPaymentResponse.builder()
                .setLoanId(loanPayment1.getLoanId())
                .setNumberPayment(loanPayment1.getNumberPayment())
                .setDatePayment(loanPayment1.getDatePayment())
                .setAmountPayment(loanPayment1.getAmountPayment())
                .setPrincipalDebt(loanPayment1.getPrincipalDebt())
                .setInterestDebt(loanPayment1.getInterestDebt())
                .setRemainingDebt(loanPayment1.getRemainingDebt())
                .setDescriptionPayment(loanPayment1.getDescriptionPayment());

    }


    public static LoanPaymentResponse.LoanPaymentResponseBuilder loanPayment2ResponseBuilder() {
        LoanPayment loanPayment2 = LoanPaymentGenerator.generateLoanPayment2Builder()
                .build();

        return LoanPaymentResponse.builder()
                .setLoanId(loanPayment2.getLoanId())
                .setNumberPayment(loanPayment2.getNumberPayment())
                .setDatePayment(loanPayment2.getDatePayment())
                .setAmountPayment(loanPayment2.getAmountPayment())
                .setPrincipalDebt(loanPayment2.getPrincipalDebt())
                .setInterestDebt(loanPayment2.getInterestDebt())
                .setRemainingDebt(loanPayment2.getRemainingDebt())
                .setDescriptionPayment(loanPayment2.getDescriptionPayment());
    }

    public static LoanPaymentPageResponse.LoanPaymentPageResponseBuilder loanPaymentPageResponseBuilder(){
        LoanPaymentResponse loanPayment1 = loanPayment1ResponseBuilder().build();
        LoanPaymentResponse loanPayment2 = loanPayment2ResponseBuilder().build();

        return LoanPaymentPageResponse.builder()
                .setLoanPayments(List.of(loanPayment1,loanPayment2));
    }

}
