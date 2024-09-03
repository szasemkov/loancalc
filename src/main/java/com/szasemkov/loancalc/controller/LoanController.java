package com.szasemkov.loancalc.controller;

import com.szasemkov.loancalc.dto.CreateLoanRequest;
import com.szasemkov.loancalc.dto.CreateLoanResponse;
import com.szasemkov.loancalc.dto.LoanPageResponse;
import com.szasemkov.loancalc.dto.LoanResponse;
import com.szasemkov.loancalc.dto.UpdateLoanRequest;
import com.szasemkov.loancalc.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("create")
    public CreateLoanResponse createLoan(@RequestBody CreateLoanRequest request) {
        return loanService.createLoan(request);
    }

    @GetMapping("/{id}")
    public LoanResponse getById(@PathVariable("id") Long id) {
        return loanService.getById(id);
    }

    @DeleteMapping("/{id}")
    public LoanResponse delete(@PathVariable("id") Long id) {
        return loanService.delete(id);
    }

    @PutMapping("update")
    public LoanResponse update(@RequestBody UpdateLoanRequest request) {
        return loanService.update(request);
    }

    @GetMapping
    public LoanPageResponse getAll() {
        return loanService.getAll();
    }

}
