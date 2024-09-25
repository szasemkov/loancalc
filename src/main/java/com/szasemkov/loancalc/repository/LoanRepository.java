package com.szasemkov.loancalc.repository;

import com.szasemkov.loancalc.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

}
