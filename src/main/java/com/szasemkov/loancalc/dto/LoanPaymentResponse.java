package com.szasemkov.loancalc.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder(setterPrefix = "set")
public class LoanPaymentResponse {

    private Long loanId;

    private Integer numberPayment;

    private Date datePayment;

    private Double amountPayment;

    private Double principalDebt;

    private Double interestDebt;

    private Double remainingDebt;

    private String descriptionPayment;
}
