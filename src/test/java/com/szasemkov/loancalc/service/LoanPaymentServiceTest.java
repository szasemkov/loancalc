package com.szasemkov.loancalc.service;

import com.szasemkov.loancalc.config.TestConfig;
import com.szasemkov.loancalc.dto.CreateLoanPaymentRequest;
import com.szasemkov.loancalc.dto.LoanPaymentPageResponse;
import com.szasemkov.loancalc.exception.LoanCalcException;
import com.szasemkov.loancalc.exception.LoanNotFoundException;
import com.szasemkov.loancalc.exception.LoanPaymentNotFoundException;
import com.szasemkov.loancalc.generator.LoanPaymentGenerator;
import com.szasemkov.loancalc.mapper.LoanPaymentMapper;
import com.szasemkov.loancalc.model.Loan;
import com.szasemkov.loancalc.model.LoanPayment;
import com.szasemkov.loancalc.repository.LoanPaymentRepository;
import com.szasemkov.loancalc.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.szasemkov.loancalc.generator.LoanGenerator.generateCalculatedLoanBuilder;
import static com.szasemkov.loancalc.generator.LoanGenerator.generateLoanAfterCreateBuilder;
import static com.szasemkov.loancalc.generator.LoanPaymentDtoGenerator.loanPaymentPageResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanPaymentGenerator.generateLoanPayment1Builder;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        LoanPaymentService.class,
        LoanPaymentMapper.class
})
@SpringBootTest(classes = {TestConfig.class})
class LoanPaymentServiceTest {
    public static final Long WRONG_LOAN_ID = 500L;
    private static final String LOANPAYMENT_IS_NOT_FOUND = "LoanPayment with Loan_id = %s is not found";
    private static final String LOAN_IS_NOT_FOUND = "Loan with id = %s is not found";
    private static final String LOAN_ALREADY_CALC = "Loan with id = %s is calculated";

    @Autowired
    private LoanPaymentService loanPaymentService;

    @MockBean
    private LoanPaymentRepository loanPaymentRepository;


    @MockBean
    private LoanRepository loanRepository;

    @MockBean
    private LoanPaymentCalculation loanPaymentCalculation;


    @Test
    void createLoanPayment() {
        //given
        Loan loan = generateLoanAfterCreateBuilder().build();

        Long loanId = loan.getId();
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        LoanPayment loanPayment1 = generateLoanPayment1Builder()
                .build();
        LoanPayment loanPayment2 = LoanPaymentGenerator.generateLoanPayment2Builder()
                .build();
        List<LoanPayment> loanPayments = List.of(loanPayment1, loanPayment2);

        when(loanPaymentCalculation.createLoanPayments(loan)).thenReturn(loanPayments);
        when(loanPaymentRepository.saveAll(anyList())).thenReturn(anyList());

        CreateLoanPaymentRequest request = CreateLoanPaymentRequest.builder()
                .setLoanId(loanId)
                .build();

        //when
        LoanPaymentPageResponse actualResponse = loanPaymentService.createLoanPayment(request);

        //then
        LoanPaymentPageResponse expectedResponse = loanPaymentPageResponseBuilder().build();

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(loanRepository).findById(loanId);
        verify(loanRepository).save(any());
        verifyNoMoreInteractions(loanRepository);
        verify(loanPaymentCalculation).createLoanPayments(loan);
        verifyNoMoreInteractions(loanPaymentCalculation);
        verify(loanPaymentRepository).saveAll(loanPayments);
        verifyNoMoreInteractions(loanPaymentRepository);

    }


    @Test
    void createLoanPayment_loanNotFound() {
        //given
        when(loanRepository.findById(WRONG_LOAN_ID)).thenReturn(Optional.empty());

        CreateLoanPaymentRequest request = CreateLoanPaymentRequest.builder()
                .setLoanId(WRONG_LOAN_ID)
                .build();

        //when & then
        Exception exception = assertThrows(LoanNotFoundException.class, () -> loanPaymentService.createLoanPayment(request));
        assertEquals(String.format(LOAN_IS_NOT_FOUND, WRONG_LOAN_ID), exception.getMessage());

        verify(loanRepository).findById(WRONG_LOAN_ID);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(loanPaymentCalculation);
        verifyNoMoreInteractions(loanPaymentRepository);

    }


    @Test
    void createLoanPayment_loanAlreadyCalculated() {
        //given
        Loan loan = generateCalculatedLoanBuilder().build();

        Long loanId = loan.getId();
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        CreateLoanPaymentRequest request = CreateLoanPaymentRequest.builder()
                .setLoanId(loanId)
                .build();

        //when & then
        Exception exception = assertThrows(LoanCalcException.class, () -> loanPaymentService.createLoanPayment(request));
        assertEquals(String.format(LOAN_ALREADY_CALC, loanId), exception.getMessage());

        verify(loanRepository).findById(loanId);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(loanPaymentCalculation);
        verifyNoMoreInteractions(loanPaymentRepository);

    }


    @Test
    void getLoanPaymentByLoanId() {
        //given
        LoanPayment loanPayment1 = generateLoanPayment1Builder()
                .build();
        LoanPayment loanPayment2 = LoanPaymentGenerator.generateLoanPayment2Builder()
                .build();
        List<LoanPayment> loanPayments = List.of(loanPayment1, loanPayment2);

        Long loanId = loanPayment1.getLoanId();

        when(loanPaymentRepository.findAllByLoanId(loanId)).thenReturn(loanPayments);

        //when
        LoanPaymentPageResponse actualResponse = loanPaymentService.getLoanPaymentByLoanId(loanId);

        //then
        LoanPaymentPageResponse expectedResponse = loanPaymentPageResponseBuilder().build();

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verifyNoMoreInteractions(loanRepository);
        verify(loanPaymentRepository).findAllByLoanId(loanId);
        verifyNoMoreInteractions(loanPaymentRepository);
    }


    @Test
    void getLoanPaymentByLoanId_loanPaymentsNotFound() {
        //given
        Long loanId = 1L;

        when(loanPaymentRepository.findAllByLoanId(loanId)).thenReturn(Collections.EMPTY_LIST);

        //when & then
        Exception exception = assertThrows(LoanPaymentNotFoundException.class, () -> loanPaymentService.getLoanPaymentByLoanId(loanId));
        assertEquals(String.format(LOANPAYMENT_IS_NOT_FOUND, loanId), exception.getMessage());

        verify(loanPaymentRepository).findAllByLoanId(loanId);
        verifyNoMoreInteractions(loanPaymentRepository);
    }


    @Test
    void deletePaymentByLoanId() {
        //given
        Loan loan = generateCalculatedLoanBuilder().build();
        Long loanId = loan.getId();
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        LoanPayment loanPayment1 = generateLoanPayment1Builder()
                .build();
        LoanPayment loanPayment2 = LoanPaymentGenerator.generateLoanPayment2Builder()
                .build();
        List<LoanPayment> loanPayments = List.of(loanPayment1, loanPayment2);

        when(loanPaymentRepository.findAllByLoanId(loanId)).thenReturn(loanPayments);

        //when
        LoanPaymentPageResponse actualResponse = loanPaymentService.deletePaymentByLoanId(loanId);

        //then
        LoanPaymentPageResponse expectedResponse = loanPaymentPageResponseBuilder().build();

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(loanRepository).findById(loanId);
        verify(loanRepository).save(loan);
        verifyNoMoreInteractions(loanRepository);
        verify(loanPaymentRepository).findAllByLoanId(loanId);
        verify(loanPaymentRepository).deleteAll(loanPayments);
        verifyNoMoreInteractions(loanPaymentRepository);
    }


    @Test
    void delete_loanNotFound() {
        //given
        Long loanId = 1L;

        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(LoanNotFoundException.class, () -> loanPaymentService.deletePaymentByLoanId(loanId));
        assertEquals(String.format(LOAN_IS_NOT_FOUND, loanId), exception.getMessage());

        verify(loanRepository).findById(loanId);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(loanPaymentRepository);
    }


    @Test
    void delete_loanPaymentNotFound() {
        //given
        Loan loan = generateCalculatedLoanBuilder().build();
        Long loanId = loan.getId();
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        when(loanPaymentRepository.findAllByLoanId(loanId)).thenReturn(Collections.EMPTY_LIST);

        //when & then
        Exception exception = assertThrows(LoanPaymentNotFoundException.class, () -> loanPaymentService.deletePaymentByLoanId(loanId));
        assertEquals(String.format(LOANPAYMENT_IS_NOT_FOUND, loanId), exception.getMessage());

        verify(loanPaymentRepository).findAllByLoanId(loanId);
        verifyNoMoreInteractions(loanPaymentRepository);
    }

}