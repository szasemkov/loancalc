package com.szasemkov.loancalc.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder(setterPrefix = "set")
public class CreateLoanPaymentResponse {

    private Long id;

    private Long loanId;

    private Integer numberPayment;

    @Temporal(TemporalType.DATE)
    private Date datePayment;

    private Double amountPayment;

    private Double principalDebt;

    private Double interestDebt;

    private Double remainingDebt;

    private String descriptionPayment;
}
