package com.szasemkov.loancalc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szasemkov.loancalc.dto.CreateLoanPaymentRequest;
import com.szasemkov.loancalc.dto.ErrorResponse;
import com.szasemkov.loancalc.dto.LoanPaymentPageResponse;
import com.szasemkov.loancalc.dto.LoanPaymentResponse;
import com.szasemkov.loancalc.exception.LoanCalcException;
import com.szasemkov.loancalc.exception.LoanNotFoundException;
import com.szasemkov.loancalc.exception.LoanPaymentNotFoundException;
import com.szasemkov.loancalc.service.LoanPaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.szasemkov.loancalc.generator.LoanPaymentDtoGenerator.loanPayment1ResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanPaymentDtoGenerator.loanPayment2ResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanPaymentDtoGenerator.loanPaymentPageResponseBuilder;
import static com.szasemkov.loancalc.model.InternalErrorStatus.LOANPAYMENT_DOES_NOT_EXIST;
import static com.szasemkov.loancalc.model.InternalErrorStatus.LOAN_ALREADY_CALCULATED;
import static com.szasemkov.loancalc.model.InternalErrorStatus.LOAN_DOES_NOT_EXIST;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LoanPaymentController.class)
class LoanPaymentControllerTest {
    private static final String LOANPAYMENT_IS_NOT_FOUND = "LoanPayment with Loan_id = %s is not found";
    private static final String LOAN_IS_NOT_FOUND = "Loan with id = %s is not found";
    private static final String LOAN_ALREADY_CALC = "Loan with id = %s is calculated";

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private LoanPaymentController loanPaymentController;

    @MockBean
    private LoanPaymentService loanPaymentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createLoanPayment_success() throws Exception {
        //given
        CreateLoanPaymentRequest request = CreateLoanPaymentRequest.builder()
                .setLoanId(1L)
                .build();

        LoanPaymentResponse loanPayment1 = loanPayment1ResponseBuilder().build();
        LoanPaymentResponse loanPayment2 = loanPayment2ResponseBuilder().build();

        String stringReq = objectMapper.writeValueAsString(request);
        LoanPaymentPageResponse loanPaymentPageResponse = loanPaymentPageResponseBuilder().build();

        when(loanPaymentService.createLoanPayment(request)).thenReturn(loanPaymentPageResponse);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/loan-payment/create")
                        .content(stringReq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanPayments.[0].loanId").value(loanPayment1.getLoanId()))
                .andExpect(jsonPath("$.loanPayments.[0].numberPayment").value(loanPayment1.getNumberPayment()))
                .andExpect(jsonPath("$.loanPayments.[0].datePayment").value(loanPayment1.getDatePayment().toString()))
                .andExpect(jsonPath("$.loanPayments.[0].amountPayment").value(loanPayment1.getAmountPayment()))
                .andExpect(jsonPath("$.loanPayments.[0].principalDebt").value(loanPayment1.getPrincipalDebt()))
                .andExpect(jsonPath("$.loanPayments.[0].interestDebt").value(loanPayment1.getInterestDebt()))
                .andExpect(jsonPath("$.loanPayments.[0].remainingDebt").value(loanPayment1.getRemainingDebt()))
                .andExpect(jsonPath("$.loanPayments.[0].descriptionPayment").value(loanPayment1.getDescriptionPayment()))
                .andExpect(jsonPath("$.loanPayments.[1].loanId").value(loanPayment2.getLoanId()))
                .andExpect(jsonPath("$.loanPayments.[1].numberPayment").value(loanPayment2.getNumberPayment()))
                .andExpect(jsonPath("$.loanPayments.[1].datePayment").value(loanPayment2.getDatePayment().toString()))
                .andExpect(jsonPath("$.loanPayments.[1].amountPayment").value(loanPayment2.getAmountPayment()))
                .andExpect(jsonPath("$.loanPayments.[1].principalDebt").value(loanPayment2.getPrincipalDebt()))
                .andExpect(jsonPath("$.loanPayments.[1].interestDebt").value(loanPayment2.getInterestDebt()))
                .andExpect(jsonPath("$.loanPayments.[1].remainingDebt").value(loanPayment2.getRemainingDebt()))
                .andExpect(jsonPath("$.loanPayments.[1].descriptionPayment").value(loanPayment2.getDescriptionPayment()));


        verify(loanPaymentService).createLoanPayment(request);
        verifyNoMoreInteractions(loanPaymentService);
    }


    @Test
    void createLoanPayment_loanNotFound() throws Exception {
        //given
        Long wrongLoanId = 500L;
        CreateLoanPaymentRequest request = CreateLoanPaymentRequest.builder()
                .setLoanId(wrongLoanId)
                .build();

        String stringReq = objectMapper.writeValueAsString(request);

        when(loanPaymentService.createLoanPayment(request)).thenThrow(new LoanNotFoundException(String.format(LOAN_IS_NOT_FOUND, wrongLoanId)));

        //when
        ErrorResponse errorResponse = new ErrorResponse(LOAN_DOES_NOT_EXIST, String.format(LOAN_IS_NOT_FOUND, wrongLoanId));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/loan-payment/create")
                        .content(stringReq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(errorResponse.getStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorResponse.getMessage()));

        verify(loanPaymentService).createLoanPayment(request);
        verifyNoMoreInteractions(loanPaymentService);
    }

    @Test
    void createLoanPayment_loanAlreadyCalculated() throws Exception {
        //given
        Long calculatedLoanId = 500L;
        CreateLoanPaymentRequest request = CreateLoanPaymentRequest.builder()
                .setLoanId(calculatedLoanId)
                .build();

        String stringReq = objectMapper.writeValueAsString(request);

        when(loanPaymentService.createLoanPayment(request)).thenThrow(new LoanCalcException(String.format(LOAN_ALREADY_CALC, calculatedLoanId)));

        //when
        ErrorResponse errorResponse = new ErrorResponse(LOAN_ALREADY_CALCULATED, String.format(LOAN_ALREADY_CALC, calculatedLoanId));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/loan-payment/create")
                        .content(stringReq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(errorResponse.getStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorResponse.getMessage()));

        verify(loanPaymentService).createLoanPayment(request);
        verifyNoMoreInteractions(loanPaymentService);
    }


    @Test
    void getById_success() throws Exception {
        //given
        LoanPaymentResponse loanPayment1 = loanPayment1ResponseBuilder().build();
        LoanPaymentResponse loanPayment2 = loanPayment2ResponseBuilder().build();
        Long loanId = loanPayment1.getLoanId();

        LoanPaymentPageResponse loanPaymentPageResponse = loanPaymentPageResponseBuilder().build();

        when(loanPaymentService.getLoanPaymentByLoanId(loanId)).thenReturn(loanPaymentPageResponse);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/loan-payment/" + loanId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanPayments.[0].loanId").value(loanPayment1.getLoanId()))
                .andExpect(jsonPath("$.loanPayments.[0].numberPayment").value(loanPayment1.getNumberPayment()))
                .andExpect(jsonPath("$.loanPayments.[0].datePayment").value(loanPayment1.getDatePayment().toString()))
                .andExpect(jsonPath("$.loanPayments.[0].amountPayment").value(loanPayment1.getAmountPayment()))
                .andExpect(jsonPath("$.loanPayments.[0].principalDebt").value(loanPayment1.getPrincipalDebt()))
                .andExpect(jsonPath("$.loanPayments.[0].interestDebt").value(loanPayment1.getInterestDebt()))
                .andExpect(jsonPath("$.loanPayments.[0].remainingDebt").value(loanPayment1.getRemainingDebt()))
                .andExpect(jsonPath("$.loanPayments.[0].descriptionPayment").value(loanPayment1.getDescriptionPayment()))
                .andExpect(jsonPath("$.loanPayments.[1].loanId").value(loanPayment2.getLoanId()))
                .andExpect(jsonPath("$.loanPayments.[1].numberPayment").value(loanPayment2.getNumberPayment()))
                .andExpect(jsonPath("$.loanPayments.[1].datePayment").value(loanPayment2.getDatePayment().toString()))
                .andExpect(jsonPath("$.loanPayments.[1].amountPayment").value(loanPayment2.getAmountPayment()))
                .andExpect(jsonPath("$.loanPayments.[1].principalDebt").value(loanPayment2.getPrincipalDebt()))
                .andExpect(jsonPath("$.loanPayments.[1].interestDebt").value(loanPayment2.getInterestDebt()))
                .andExpect(jsonPath("$.loanPayments.[1].remainingDebt").value(loanPayment2.getRemainingDebt()))
                .andExpect(jsonPath("$.loanPayments.[1].descriptionPayment").value(loanPayment2.getDescriptionPayment()));


        verify(loanPaymentService).getLoanPaymentByLoanId(loanId);
        verifyNoMoreInteractions(loanPaymentService);
    }


    @Test
    void getById_loanPaymentsNotFound() throws Exception {
        //given
        Long wrongLoanId = 500L;

        when(loanPaymentService.getLoanPaymentByLoanId(wrongLoanId)).thenThrow(new LoanPaymentNotFoundException(String.format(LOANPAYMENT_IS_NOT_FOUND, wrongLoanId)));

        //when
        ErrorResponse errorResponse = new ErrorResponse(LOANPAYMENT_DOES_NOT_EXIST, String.format(LOANPAYMENT_IS_NOT_FOUND, wrongLoanId));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/loan-payment/" + wrongLoanId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(errorResponse.getStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorResponse.getMessage()));

        verify(loanPaymentService).getLoanPaymentByLoanId(wrongLoanId);
        verifyNoMoreInteractions(loanPaymentService);
    }


    @Test
    void deleteById_success() throws Exception {
        //given
        LoanPaymentResponse loanPayment1 = loanPayment1ResponseBuilder().build();
        LoanPaymentResponse loanPayment2 = loanPayment2ResponseBuilder().build();
        Long loanId = loanPayment1.getLoanId();

        LoanPaymentPageResponse loanPaymentPageResponse = loanPaymentPageResponseBuilder().build();

        when(loanPaymentService.deletePaymentByLoanId(loanId)).thenReturn(loanPaymentPageResponse);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/loan-payment/" + loanId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanPayments.[0].loanId").value(loanPayment1.getLoanId()))
                .andExpect(jsonPath("$.loanPayments.[0].numberPayment").value(loanPayment1.getNumberPayment()))
                .andExpect(jsonPath("$.loanPayments.[0].datePayment").value(loanPayment1.getDatePayment().toString()))
                .andExpect(jsonPath("$.loanPayments.[0].amountPayment").value(loanPayment1.getAmountPayment()))
                .andExpect(jsonPath("$.loanPayments.[0].principalDebt").value(loanPayment1.getPrincipalDebt()))
                .andExpect(jsonPath("$.loanPayments.[0].interestDebt").value(loanPayment1.getInterestDebt()))
                .andExpect(jsonPath("$.loanPayments.[0].remainingDebt").value(loanPayment1.getRemainingDebt()))
                .andExpect(jsonPath("$.loanPayments.[0].descriptionPayment").value(loanPayment1.getDescriptionPayment()))
                .andExpect(jsonPath("$.loanPayments.[1].loanId").value(loanPayment2.getLoanId()))
                .andExpect(jsonPath("$.loanPayments.[1].numberPayment").value(loanPayment2.getNumberPayment()))
                .andExpect(jsonPath("$.loanPayments.[1].datePayment").value(loanPayment2.getDatePayment().toString()))
                .andExpect(jsonPath("$.loanPayments.[1].amountPayment").value(loanPayment2.getAmountPayment()))
                .andExpect(jsonPath("$.loanPayments.[1].principalDebt").value(loanPayment2.getPrincipalDebt()))
                .andExpect(jsonPath("$.loanPayments.[1].interestDebt").value(loanPayment2.getInterestDebt()))
                .andExpect(jsonPath("$.loanPayments.[1].remainingDebt").value(loanPayment2.getRemainingDebt()))
                .andExpect(jsonPath("$.loanPayments.[1].descriptionPayment").value(loanPayment2.getDescriptionPayment()));


        verify(loanPaymentService).deletePaymentByLoanId(loanId);
        verifyNoMoreInteractions(loanPaymentService);
    }


    @Test
    void deleteById_loanNotFound() throws Exception {
        //given
        Long wrongLoanId = 500L;

        when(loanPaymentService.deletePaymentByLoanId(wrongLoanId)).thenThrow(new LoanNotFoundException(String.format(LOAN_IS_NOT_FOUND, wrongLoanId)));

        //when
        ErrorResponse errorResponse = new ErrorResponse(LOAN_DOES_NOT_EXIST, String.format(LOAN_IS_NOT_FOUND, wrongLoanId));
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/loan-payment/" + wrongLoanId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(errorResponse.getStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorResponse.getMessage()));

        verify(loanPaymentService).deletePaymentByLoanId(wrongLoanId);
        verifyNoMoreInteractions(loanPaymentService);
    }


    @Test
    void deleteById_loanPaymentsNotFound() throws Exception {
        //given
        Long wrongLoanId = 500L;

        when(loanPaymentService.deletePaymentByLoanId(wrongLoanId)).thenThrow(new LoanPaymentNotFoundException(String.format(LOANPAYMENT_IS_NOT_FOUND, wrongLoanId)));

        //when
        ErrorResponse errorResponse = new ErrorResponse(LOANPAYMENT_DOES_NOT_EXIST, String.format(LOANPAYMENT_IS_NOT_FOUND, wrongLoanId));
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/loan-payment/" + wrongLoanId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(errorResponse.getStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorResponse.getMessage()));

        verify(loanPaymentService).deletePaymentByLoanId(wrongLoanId);
        verifyNoMoreInteractions(loanPaymentService);
    }
}