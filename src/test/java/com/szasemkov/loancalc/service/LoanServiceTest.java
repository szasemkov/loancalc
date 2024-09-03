package com.szasemkov.loancalc.service;

import com.szasemkov.loancalc.config.TestConfig;
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
import com.szasemkov.loancalc.model.StatusLoan;
import com.szasemkov.loancalc.repository.LoanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.szasemkov.loancalc.generator.LoanDtoGenerator.createLoanRequestBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.createLoanResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.loan1ResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.loan2ResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.loanResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.updateLoanRequestBuilder;
import static com.szasemkov.loancalc.generator.LoanGenerator.generateCalculatedLoanBuilder;
import static com.szasemkov.loancalc.generator.LoanGenerator.generateLoan1Builder;
import static com.szasemkov.loancalc.generator.LoanGenerator.generateLoan2Builder;
import static com.szasemkov.loancalc.generator.LoanGenerator.generateLoanAfterCreateBuilder;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        LoanService.class,
        LoanMapper.class
})
@SpringBootTest(classes = {TestConfig.class})
public class LoanServiceTest {
    public static final Long WRONG_LOAN_ID = 500L;
    private static final String LOAN_IS_NOT_FOUND = "Loan with id = %s is not found";
    private static final String LOAN_IS_CALCULATED = "Loan with id = %s is calculated";
    private static final String WRONG_AMOUNT = "Amount is less or equal to zero";
    private static final String WRONG_TERM = "Term is less or equal to zero";

    @Autowired
    private LoanService loanService;

    @MockBean
    private LoanRepository loanRepository;


    @Test
    void createLoan() {
        //given
        CreateLoanRequest request = createLoanRequestBuilder().build();

        when(loanRepository.save(any())).thenReturn(any());

        //when
        CreateLoanResponse actualResponse = loanService.createLoan(request);

        //then
        CreateLoanResponse expectedResponse = createLoanResponseBuilder().build();

        assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResponse);

        verify(loanRepository).save(any());
        verifyNoMoreInteractions(loanRepository);

    }


    @ParameterizedTest
    @MethodSource("creationBadRequestToErrorMessages")
    void createLoan_badRequest(CreateLoanRequest request, String expectedMessage) {
        //when & then
        Exception exception = assertThrows(BadRequestException.class, () -> loanService.createLoan(request));
        assertEquals(expectedMessage, exception.getMessage());

        verifyNoMoreInteractions(loanRepository);

    }


    private static Stream<Arguments> creationBadRequestToErrorMessages() {
        return Stream.of(
                Arguments.of(createLoanRequestBuilder()
                                .setAmount(0.0)
                                .build(),
                        WRONG_AMOUNT),
                Arguments.of(createLoanRequestBuilder()
                                .setAmount(-5.0)
                                .build(),
                        WRONG_AMOUNT),
                Arguments.of(createLoanRequestBuilder()
                                .setTerm(0)
                                .build(),
                        WRONG_TERM),
                Arguments.of(createLoanRequestBuilder()
                                .setTerm(-5)
                                .build(),
                        WRONG_TERM)
        );
    }


    @Test
    void getAll() {
        //given
        Loan loan1 = generateLoan1Builder().build();
        Loan loan2 = generateLoan2Builder().build();

        when(loanRepository.findAll()).thenReturn(List.of(loan1, loan2));

        //when
        LoanPageResponse actualResponse = loanService.getAll();

        //then
        LoanResponse loanResponse1 = loan1ResponseBuilder().build();
        LoanResponse loanResponse2 = loan2ResponseBuilder().build();

        List<LoanResponse> expectedLoans = new ArrayList<>();
        expectedLoans.add(loanResponse1);
        expectedLoans.add(loanResponse2);
        LoanPageResponse expectedResponse = new LoanPageResponse(expectedLoans);

        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(loanRepository).findAll();
        verifyNoMoreInteractions(loanRepository);
    }


    @Test
    void getById() {
        //given
        Loan loan = generateLoanAfterCreateBuilder().build();

        Long loanId = loan.getId();
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        //when
        LoanResponse actualResponse = loanService.getById(loanId);

        //then
        LoanResponse expectedLoanResponse = loanResponseBuilder().build();

        Assertions.assertThat(actualResponse).isEqualTo(expectedLoanResponse);

        verify(loanRepository).findById(loanId);
        verifyNoMoreInteractions(loanRepository);
    }


    @Test
    void getById_loanNotFound() {
        //given
        when(loanRepository.findById(WRONG_LOAN_ID)).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(LoanNotFoundException.class, () -> loanService.getById(WRONG_LOAN_ID));
        assertEquals(String.format(LOAN_IS_NOT_FOUND, WRONG_LOAN_ID), exception.getMessage());

        verify(loanRepository).findById(WRONG_LOAN_ID);
        verifyNoMoreInteractions(loanRepository);
    }


    @Test
    void update() {
        //given
        UpdateLoanRequest request = updateLoanRequestBuilder().build();

        Long requestLoanId = request.getId();

        Loan loan = generateLoanAfterCreateBuilder().build();

        when(loanRepository.findById(requestLoanId)).thenReturn(Optional.of(loan));

        //when
        LoanResponse actualResponse = loanService.update(request);

        //then
        LoanResponse expectedLoanResponse = loanResponseBuilder().build();
        Assertions.assertThat(actualResponse).isEqualTo(expectedLoanResponse);

        verify(loanRepository).findById(requestLoanId);
        verify(loanRepository).save(loan);
        verifyNoMoreInteractions(loanRepository);
    }


    @Test
    void update_loanNotFound() {
        //given
        UpdateLoanRequest request = updateLoanRequestBuilder().build();

        Long requestLoanId = request.getId();

        when(loanRepository.findById(requestLoanId)).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(LoanNotFoundException.class, () -> loanService.update(request));
        String expectedMessage = String.format("Loan with id = %s is not found", requestLoanId);
        assertEquals(expectedMessage, exception.getMessage());

        verify(loanRepository).findById(requestLoanId);
        verifyNoMoreInteractions(loanRepository);
    }


    @ParameterizedTest
    @MethodSource("updateBadRequestToErrorMessages")
    void updateLoan_badRequest(UpdateLoanRequest request, String expectedMessage) {
        //given
        Long requestLoanId = request.getId();

        Loan loan = generateLoanAfterCreateBuilder().build();

        when(loanRepository.findById(requestLoanId)).thenReturn(Optional.of(loan));

        //when & then
        Exception exception = assertThrows(BadRequestException.class, () -> loanService.update(request));
        assertEquals(expectedMessage, exception.getMessage());

        verify(loanRepository).findById(requestLoanId);
        verifyNoMoreInteractions(loanRepository);

    }


    private static Stream<Arguments> updateBadRequestToErrorMessages() {
        return Stream.of(
                Arguments.of(updateLoanRequestBuilder()
                                .setAmount(0.0)
                                .build(),
                        WRONG_AMOUNT),
                Arguments.of(updateLoanRequestBuilder()
                                .setAmount(-5.0)
                                .build(),
                        WRONG_AMOUNT),
                Arguments.of(updateLoanRequestBuilder()
                                .setTerm(0)
                                .build(),
                        WRONG_TERM),
                Arguments.of(updateLoanRequestBuilder()
                                .setTerm(-5)
                                .build(),
                        WRONG_TERM)
        );
    }


    @Test
    void updateLoan_LoanAlreadyCalculated() {
        //given
        UpdateLoanRequest request = updateLoanRequestBuilder().build();
        Long requestLoanId = request.getId();

        Loan loan = generateLoanAfterCreateBuilder()
                .setStatus(StatusLoan.CALCULATED)
                .build();

        when(loanRepository.findById(requestLoanId)).thenReturn(Optional.of(loan));

        //when & then
        Exception exception = assertThrows(BadRequestException.class, () -> loanService.update(request));
        assertEquals(String.format(LOAN_IS_CALCULATED, requestLoanId), exception.getMessage());

        verify(loanRepository).findById(requestLoanId);
        verifyNoMoreInteractions(loanRepository);

    }


    @Test
    void delete() {
        //given
        Loan loan = generateLoanAfterCreateBuilder().build();

        Long loanId = loan.getId();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        //when
        LoanResponse actualResponse = loanService.delete(loanId);

        //then
        LoanResponse expectedLoanResponse = loanResponseBuilder().build();
        Assertions.assertThat(actualResponse).isEqualTo(expectedLoanResponse);

        verify(loanRepository).findById(loanId);
        verify(loanRepository).deleteById(loanId);
        verifyNoMoreInteractions(loanRepository);
    }


    @Test
    void delete_loanNotFound() {
        //given
        when(loanRepository.findById(WRONG_LOAN_ID)).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(LoanNotFoundException.class, () -> loanService.delete(WRONG_LOAN_ID));
        assertEquals(String.format(LOAN_IS_NOT_FOUND, WRONG_LOAN_ID), exception.getMessage());

        verify(loanRepository).findById(WRONG_LOAN_ID);
        verifyNoMoreInteractions(loanRepository);
    }


    @Test
    void delete_loanAlreadyCalculated() {
        //given
        Loan loan = generateCalculatedLoanBuilder().build();

        Long loanId = loan.getId();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        //when & then
        Exception exception = assertThrows(LoanDeleteException.class, () -> loanService.delete(loanId));
        assertEquals(String.format(LOAN_IS_CALCULATED, loanId), exception.getMessage());

        verify(loanRepository).findById(loanId);
        verifyNoMoreInteractions(loanRepository);
    }
}
