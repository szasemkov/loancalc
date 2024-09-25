package com.szasemkov.loancalc.dto;

import com.szasemkov.loancalc.model.TypePayment;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder(setterPrefix = "set")
public class CreateLoanRequest {

    private Double amount;

    private Integer term;

    private Double interestRate;

    private Date loanDate;

    private TypePayment typePayment;

}
