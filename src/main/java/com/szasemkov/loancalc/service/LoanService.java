package com.szasemkov.loancalc.service;

import com.szasemkov.loancalc.dto.CreateLoanRequest;
import com.szasemkov.loancalc.dto.CreateLoanResponse;
import com.szasemkov.loancalc.dto.LoanPageResponse;
import com.szasemkov.loancalc.dto.LoanResponse;
import com.szasemkov.loancalc.dto.UpdateLoanRequest;
import com.szasemkov.loancalc.exception.BadRequestException;
import com.szasemkov.loancalc.exception.LoanDeleteException;
import com.szasemkov.loancalc.exception.LoanNotFoundException;
import com.szasemkov.loancalc.mapper.LoanMapper;
import com.szasemkov.loancalc.model.Loan;
import com.szasemkov.loancalc.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static com.szasemkov.loancalc.model.StatusLoan.CALCULATED;
import static com.szasemkov.loancalc.model.StatusLoan.NEW;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final String LOAN_IS_NOT_FOUND = "Loan with id = %s is not found";
    private static final String LOAN_IS_CALCULATED = "Loan with id = %s is calculated";
    private static final String WRONG_AMOUNT = "Amount is less or equal to zero";
    private static final String WRONG_TERM = "Term is less or equal to zero";
    private static final String WRONG_INTEREST_RATE = "InterestRate is less or equal to zero";
    private static final String WRONG_LOAN_DATE = "LoanDate is less then LocalDate";

    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;

    public CreateLoanResponse createLoan(CreateLoanRequest request) {

        Loan loan =
                new Loan(request.getAmount(), request.getTerm(), request.getInterestRate(),
                        request.getLoanDate(), request.getTypePayment(), NEW);

        if (request.getAmount() <= 0){
            throw new BadRequestException(WRONG_AMOUNT);
        }

        if (request.getTerm() <= 0){
            throw new BadRequestException(WRONG_TERM);
        }

        if (request.getInterestRate() <= 0){
            throw new BadRequestException(WRONG_INTEREST_RATE);
        }

        if (request.getLoanDate().before(Date.valueOf(LocalDate.now()))) {
            throw new BadRequestException(WRONG_LOAN_DATE);
        }

        loanRepository.save(loan);

        return loanMapper.loanToCreatedLoanResponse(loan);
    }

    public LoanResponse getById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(String.format(LOAN_IS_NOT_FOUND, id)));
        return loanMapper.loanToLoanResponse(loan);
    }

    public LoanResponse delete(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(String.format(LOAN_IS_NOT_FOUND, id)));
        if (loan.getStatus().equals(CALCULATED)) {
            throw new LoanDeleteException(String.format(LOAN_IS_CALCULATED, id));
        }

        loanRepository.deleteById(id);

        return loanMapper.loanToLoanResponse(loan);
    }

    public LoanResponse update(UpdateLoanRequest request) {
        Long loanId = request.getId();
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(String.format(LOAN_IS_NOT_FOUND, loanId)));

        if (request.getAmount() <= 0){
            throw new BadRequestException(WRONG_AMOUNT);
        }

        if (request.getTerm() <= 0){
            throw new BadRequestException(WRONG_TERM);
        }

        if (request.getInterestRate() <= 0){
            throw new BadRequestException(WRONG_INTEREST_RATE);
        }

        if (request.getLoanDate().before(Date.valueOf(LocalDate.now()))) {
            throw new BadRequestException(WRONG_LOAN_DATE);
        }


        if (loan.getStatus() != NEW){
            throw new BadRequestException(String.format(LOAN_IS_CALCULATED, loanId));
        }


        Loan updatedLoan = loanMapper.updateLoanRequestToLoan(request);

        loanRepository.save(updatedLoan);

        return loanMapper.loanToLoanResponse(updatedLoan);
    }

    public LoanPageResponse getAll() {
        List<Loan> allLoans = loanRepository.findAll();
        return loanMapper.loanToLoanPageResponse(allLoans);
    }

}
