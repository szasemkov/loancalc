package com.szasemkov.loancalc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "loans")
@Builder(setterPrefix = "set")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq")
    @SequenceGenerator(name = "loan_seq", sequenceName = "loan_seq", allocationSize = 1)
    private Long id;

    private Double amount;

    private Integer term;

    private Double interestRate;

    @Temporal(TemporalType.DATE)
    private Date loanDate;

    @Enumerated(value = EnumType.STRING)
    private TypePayment typePayment;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private StatusLoan status = StatusLoan.NEW;

    public Loan(Double amount, Integer term, Double interestRate, Date loanDate, TypePayment typePayment, StatusLoan status) {
        this.amount = amount;
        this.term = term;
        this.interestRate = interestRate;
        this.loanDate = loanDate;
        this.typePayment = typePayment;
        this.status = status;
    }
}
