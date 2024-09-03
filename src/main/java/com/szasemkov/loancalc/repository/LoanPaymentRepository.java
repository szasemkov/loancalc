package com.szasemkov.loancalc.repository;

import com.szasemkov.loancalc.model.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {

    List<LoanPayment> findAllByLoanId(Long loanId);

}
