package com.szasemkov.loancalc.service;

import com.szasemkov.loancalc.dto.CreateLoanPaymentRequest;
import com.szasemkov.loancalc.dto.LoanPaymentPageResponse;
import com.szasemkov.loancalc.exception.LoanCalcException;
import com.szasemkov.loancalc.exception.LoanNotFoundException;
import com.szasemkov.loancalc.exception.LoanPaymentNotFoundException;
import com.szasemkov.loancalc.mapper.LoanPaymentMapper;
import com.szasemkov.loancalc.model.Loan;
import com.szasemkov.loancalc.model.LoanPayment;
import com.szasemkov.loancalc.repository.LoanPaymentRepository;
import com.szasemkov.loancalc.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.szasemkov.loancalc.model.StatusLoan.CALCULATED;
import static com.szasemkov.loancalc.model.StatusLoan.NEW;

@Service
@RequiredArgsConstructor
public class LoanPaymentService {

    private static final String LOANPAYMENT_IS_NOT_FOUND = "LoanPayment with Loan_id = %s is not found";
    private static final String LOAN_IS_NOT_FOUND = "Loan with id = %s is not found";
    private static final String LOAN_ALREADY_CALC = "Loan with id = %s is calculated";

    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final LoanPaymentMapper loanPaymentMapper;
    private final LoanPaymentCalculation loanPaymentCalculation;

    public LoanPaymentPageResponse createLoanPayment(CreateLoanPaymentRequest request) {

        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new LoanNotFoundException(String.format(LOAN_IS_NOT_FOUND, request.getLoanId())));

        if (loan.getStatus() != NEW){
            throw new LoanCalcException(String.format(LOAN_ALREADY_CALC,loan.getId()));
        }

        List<LoanPayment> loanPayments = loanPaymentCalculation.createLoanPayments(loan);

        loanPaymentRepository.saveAll(loanPayments);

        loan.setStatus(CALCULATED);
        loanRepository.save(loan);

        return loanPaymentMapper.loanPaymentsToLoanPaymentPageResponse(loanPayments);
    }

    public LoanPaymentPageResponse getLoanPaymentByLoanId(Long loanId) {

        List<LoanPayment> loanPayments = loanPaymentRepository.findAllByLoanId(loanId);
        if (loanPayments.isEmpty()) {
            throw new LoanPaymentNotFoundException(String.format(LOANPAYMENT_IS_NOT_FOUND, loanId));
        }
        return loanPaymentMapper.loanPaymentsToLoanPaymentPageResponse(loanPayments);

    }

    public LoanPaymentPageResponse deletePaymentByLoanId(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(String.format(LOAN_IS_NOT_FOUND, loanId)));

        List<LoanPayment> loanPayments = loanPaymentRepository.findAllByLoanId(loanId);
        if (loanPayments.isEmpty()) {
            throw new LoanPaymentNotFoundException(String.format(LOANPAYMENT_IS_NOT_FOUND, loanId));
        }
        loanPaymentRepository.deleteAll(loanPayments);

        loan.setStatus(NEW);
        loanRepository.save(loan);

        return loanPaymentMapper.loanPaymentsToLoanPaymentPageResponse(loanPayments);

    }

}
