package com.szasemkov.loancalc.generator;

import com.szasemkov.loancalc.dto.CreateLoanRequest;
import com.szasemkov.loancalc.dto.CreateLoanResponse;
import com.szasemkov.loancalc.dto.LoanPageResponse;
import com.szasemkov.loancalc.dto.LoanResponse;
import com.szasemkov.loancalc.dto.UpdateLoanRequest;
import com.szasemkov.loancalc.model.Loan;

import java.util.List;

public class LoanDtoGenerator {

    public static CreateLoanRequest.CreateLoanRequestBuilder createLoanRequestBuilder() {
        Loan loan = LoanGenerator.generateLoanAfterCreateBuilder()
                .build();

        return CreateLoanRequest.builder()
                .setAmount(loan.getAmount())
                .setTerm(loan.getTerm())
                .setInterestRate(loan.getInterestRate())
                .setLoanDate(loan.getLoanDate())
                .setTypePayment(loan.getTypePayment());
    }


    public static UpdateLoanRequest.UpdateLoanRequestBuilder updateLoanRequestBuilder() {
        Loan loan = LoanGenerator.generateLoanAfterCreateBuilder()
                .build();

        return UpdateLoanRequest.builder()
                .setId(loan.getId())
                .setAmount(loan.getAmount())
                .setTerm(loan.getTerm())
                .setInterestRate(loan.getInterestRate())
                .setLoanDate(loan.getLoanDate())
                .setTypePayment(loan.getTypePayment());
    }

    public static CreateLoanResponse.CreateLoanResponseBuilder createLoanResponseBuilder() {
        Loan loan = LoanGenerator.generateLoanAfterCreateBuilder()
                .build();

        return  CreateLoanResponse.builder()
                .setId(loan.getId())
                .setAmount(loan.getAmount())
                .setTerm(loan.getTerm())
                .setInterestRate(loan.getInterestRate())
                .setLoanDate(loan.getLoanDate())
                .setTypePayment(loan.getTypePayment());
    }

    public static LoanResponse.LoanResponseBuilder loanResponseBuilder() {
        Loan loan = LoanGenerator.generateLoanAfterCreateBuilder()
                .build();

        return  LoanResponse.builder()
                .setId(loan.getId())
                .setAmount(loan.getAmount())
                .setTerm(loan.getTerm())
                .setInterestRate(loan.getInterestRate())
                .setLoanDate(loan.getLoanDate())
                .setTypePayment(loan.getTypePayment());
    }


    public static LoanResponse.LoanResponseBuilder loan1ResponseBuilder() {
        Loan loan1 = LoanGenerator.generateLoan1Builder()
                .build();

        return  LoanResponse.builder()
                .setId(loan1.getId())
                .setAmount(loan1.getAmount())
                .setTerm(loan1.getTerm())
                .setInterestRate(loan1.getInterestRate())
                .setLoanDate(loan1.getLoanDate())
                .setTypePayment(loan1.getTypePayment());
    }



    public static LoanResponse.LoanResponseBuilder loan2ResponseBuilder() {
        Loan loan2 = LoanGenerator.generateLoan2Builder()
                .build();

        return  LoanResponse.builder()
                .setId(loan2.getId())
                .setAmount(loan2.getAmount())
                .setTerm(loan2.getTerm())
                .setInterestRate(loan2.getInterestRate())
                .setLoanDate(loan2.getLoanDate())
                .setTypePayment(loan2.getTypePayment());
    }


    public static LoanPageResponse.LoanPageResponseBuilder loanPageResponseBuilder(){
        LoanResponse loan1 = loan1ResponseBuilder().build();
        LoanResponse loan2 = loan2ResponseBuilder().build();

        return LoanPageResponse.builder()
                .setLoans(List.of(loan1,loan2));
    }

}
