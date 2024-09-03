package com.szasemkov.loancalc.controller;

import com.szasemkov.loancalc.dto.CreateLoanPaymentRequest;
import com.szasemkov.loancalc.dto.LoanPaymentPageResponse;
import com.szasemkov.loancalc.service.LoanPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("loan-payment")
@RequiredArgsConstructor
public class LoanPaymentController {

    private final LoanPaymentService loanPaymentService;

    @PostMapping("create")
    public LoanPaymentPageResponse createLoanPayment(@RequestBody CreateLoanPaymentRequest request) {
        return loanPaymentService.createLoanPayment(request);
    }

    @GetMapping("/{loan_id}")
    public LoanPaymentPageResponse getLoanPaymentByLoanId(@PathVariable("loan_id") Long loanId) {
        return loanPaymentService.getLoanPaymentByLoanId(loanId);
    }

    @DeleteMapping("/{loan_id}")
    public LoanPaymentPageResponse deletePaymentByLoanId(@PathVariable("loan_id") Long id) {
        return loanPaymentService.deletePaymentByLoanId(id);
    }
}
