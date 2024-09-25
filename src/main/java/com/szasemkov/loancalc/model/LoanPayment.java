package com.szasemkov.loancalc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Table(name = "loans_payment")
@Builder(setterPrefix = "set")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class LoanPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loanpayment_seq")
    @SequenceGenerator(name = "loanpayment_seq", sequenceName = "loanpayment_seq", allocationSize = 1)
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
