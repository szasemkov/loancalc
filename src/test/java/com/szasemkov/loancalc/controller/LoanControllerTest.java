package com.szasemkov.loancalc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szasemkov.loancalc.dto.CreateLoanRequest;
import com.szasemkov.loancalc.dto.CreateLoanResponse;
import com.szasemkov.loancalc.dto.ErrorResponse;
import com.szasemkov.loancalc.dto.LoanPageResponse;
import com.szasemkov.loancalc.dto.LoanResponse;
import com.szasemkov.loancalc.dto.UpdateLoanRequest;
import com.szasemkov.loancalc.exception.LoanDeleteException;
import com.szasemkov.loancalc.exception.LoanNotFoundException;
import com.szasemkov.loancalc.service.LoanService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.szasemkov.loancalc.generator.LoanDtoGenerator.createLoanRequestBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.createLoanResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.loan1ResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.loan2ResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.loanPageResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.loanResponseBuilder;
import static com.szasemkov.loancalc.generator.LoanDtoGenerator.updateLoanRequestBuilder;
import static com.szasemkov.loancalc.model.InternalErrorStatus.LOAN_CALCULATED;
import static com.szasemkov.loancalc.model.InternalErrorStatus.LOAN_DOES_NOT_EXIST;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
class LoanControllerTest {
    private static final String LOAN_IS_NOT_FOUND = "Loan with id = %s is not found";
    private static final String LOAN_IS_CALCULATED = "Loan with id = %s is calculated";

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private LoanController loanController;

    @MockBean
    private LoanService loanService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createLoan_success() throws Exception {
        //given
        CreateLoanRequest request = createLoanRequestBuilder()
                .build();

        String stringReq = objectMapper.writeValueAsString(request);
        CreateLoanResponse response = createLoanResponseBuilder()
                .build();

        when(loanService.createLoan(request)).thenReturn(response);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/loan/create") //send
                        .content(stringReq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(response.getAmount()))
                .andExpect(jsonPath("$.term").value(response.getTerm()))
                .andExpect(jsonPath("$.interestRate").value(response.getInterestRate()))
                .andExpect(jsonPath("$.loanDate").value(response.getLoanDate().toString()))
                .andExpect(jsonPath("$.typePayment").value(response.getTypePayment().name()));

        verify(loanService).createLoan(request);
        verifyNoMoreInteractions(loanService);
    }


    @Test
    void getById_success() throws Exception {
        //given
        LoanResponse response = loanResponseBuilder().build();
        Long loanId = response.getId();

        when(loanService.getById(loanId)).thenReturn(response);

        //when & then
        LoanResponse expectedLoanResponse = loanResponseBuilder().build();
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/loan/" + loanId) //send
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(expectedLoanResponse.getAmount()))
                .andExpect(jsonPath("$.term").value(expectedLoanResponse.getTerm()))
                .andExpect(jsonPath("$.interestRate").value(expectedLoanResponse.getInterestRate()))
                .andExpect(jsonPath("$.loanDate").value(expectedLoanResponse.getLoanDate().toString()))
                .andExpect(jsonPath("$.typePayment").value(expectedLoanResponse.getTypePayment().name()));

        verify(loanService).getById(loanId);
        verifyNoMoreInteractions(loanService);
    }


    @Test
    void getById_loanNotFound() throws Exception {
        //given

        Long wrongLoanId = 500L;

        when(loanService.getById(wrongLoanId)).thenThrow(new LoanNotFoundException(String.format(LOAN_IS_NOT_FOUND, wrongLoanId)));

        //when & then
        ErrorResponse errorResponse = new ErrorResponse(LOAN_DOES_NOT_EXIST, String.format(LOAN_IS_NOT_FOUND, wrongLoanId));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/loan/" + wrongLoanId) //send
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(errorResponse.getStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorResponse.getMessage()));

        verify(loanService).getById(wrongLoanId);
        verifyNoMoreInteractions(loanService);

    }


    @Test
    void getAll() throws Exception {
        //given
        LoanResponse loan1 = loan1ResponseBuilder().build();
        LoanResponse loan2 = loan2ResponseBuilder().build();

        LoanPageResponse loanPageResponse = loanPageResponseBuilder().build();

        when(loanService.getAll()).thenReturn(loanPageResponse);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/loan") //send
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loans.[0].id").value(loan1.getId()))
                .andExpect(jsonPath("$.loans.[0].amount").value(loan1.getAmount()))
                .andExpect(jsonPath("$.loans.[0].term").value(loan1.getTerm()))
                .andExpect(jsonPath("$.loans.[0].interestRate").value(loan1.getInterestRate()))
                .andExpect(jsonPath("$.loans.[0].loanDate").value(loan1.getLoanDate().toString()))
                .andExpect(jsonPath("$.loans.[0].typePayment").value(loan1.getTypePayment().name()))
                .andExpect(jsonPath("$.loans.[1].id").value(loan2.getId()))
                .andExpect(jsonPath("$.loans.[1].amount").value(loan2.getAmount()))
                .andExpect(jsonPath("$.loans.[1].term").value(loan2.getTerm()))
                .andExpect(jsonPath("$.loans.[1].interestRate").value(loan2.getInterestRate()))
                .andExpect(jsonPath("$.loans.[1].loanDate").value(loan2.getLoanDate().toString()))
                .andExpect(jsonPath("$.loans.[1].typePayment").value(loan2.getTypePayment().name()));

        verify(loanService).getAll();
        verifyNoMoreInteractions(loanService);
    }

    @Test
    void delete_success() throws Exception {
        //given
        LoanResponse loan = loanResponseBuilder().build();

        Long loanId = loan.getId();

        when(loanService.delete(loanId)).thenReturn(loan);

        //when & then
        LoanResponse expectedLoanResponse = loanResponseBuilder().build();
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/loan/" + loanId) //send
                       .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedLoanResponse.getId()))
                .andExpect(jsonPath("$.amount").value(expectedLoanResponse.getAmount()))
                .andExpect(jsonPath("$.term").value(expectedLoanResponse.getTerm()))
                .andExpect(jsonPath("$.interestRate").value(expectedLoanResponse.getInterestRate()))
                .andExpect(jsonPath("$.loanDate").value(expectedLoanResponse.getLoanDate().toString()))
                .andExpect(jsonPath("$.typePayment").value(expectedLoanResponse.getTypePayment().name()));

        verify(loanService).delete(loanId);
        verifyNoMoreInteractions(loanService);

    }

    @Test
    void delete_loanNotFound() throws Exception {
        //given
        Long wrongLoanId = 500L;

        when(loanService.delete(wrongLoanId)).thenThrow(new LoanNotFoundException(String.format(LOAN_IS_NOT_FOUND, wrongLoanId)));

        //when & then
        ErrorResponse errorResponse = new ErrorResponse(LOAN_DOES_NOT_EXIST, String.format(LOAN_IS_NOT_FOUND, wrongLoanId));
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/loan/" + wrongLoanId) //send
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(errorResponse.getStatus().toString()))
                .andExpect(jsonPath("$.message").value(errorResponse.getMessage()));

        verify(loanService).delete(wrongLoanId);
        verifyNoMoreInteractions(loanService);

    }


    @Test
    void delete_loanCalculated() throws Exception {
        //given
        Long calculatedLoanId = 500L;

        when(loanService.delete(calculatedLoanId)).thenThrow(new LoanDeleteException(String.format(LOAN_IS_CALCULATED, calculatedLoanId)));

        //when & then
        ErrorResponse errorResponse = new ErrorResponse(LOAN_CALCULATED, String.format(LOAN_IS_CALCULATED, calculatedLoanId));
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/loan/" + calculatedLoanId) //send
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(errorResponse.getStatus().toString()))
                .andExpect(jsonPath("$.message").value(errorResponse.getMessage()));

        verify(loanService).delete(calculatedLoanId);
        verifyNoMoreInteractions(loanService);

    }


    @Test
    void update_success() throws Exception {
        //given
        UpdateLoanRequest request = updateLoanRequestBuilder()
                .build();

        String stringReq = objectMapper.writeValueAsString(request);
        LoanResponse response = loanResponseBuilder()
                .build();

        when(loanService.update(request)).thenReturn(response);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/loan/update") //send
                        .content(stringReq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.amount").value(response.getAmount()))
                .andExpect(jsonPath("$.term").value(response.getTerm()))
                .andExpect(jsonPath("$.interestRate").value(response.getInterestRate()))
                .andExpect(jsonPath("$.loanDate").value(response.getLoanDate().toString()))
                .andExpect(jsonPath("$.typePayment").value(response.getTypePayment().name()));

        verify(loanService).update(request);
        verifyNoMoreInteractions(loanService);
    }

    @Test
    void update_loanNotFound() throws Exception {
        //given
        UpdateLoanRequest request = updateLoanRequestBuilder()
                .build();

        Long requestId = request.getId();

        String stringReq = objectMapper.writeValueAsString(request);

        when(loanService.update(request)).thenThrow(new LoanNotFoundException(String.format(LOAN_IS_NOT_FOUND, requestId)));

        //when
        ErrorResponse errorResponse = new ErrorResponse(LOAN_DOES_NOT_EXIST, String.format(LOAN_IS_NOT_FOUND, requestId));
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/loan/update") //send
                        .content(stringReq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(errorResponse.getStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorResponse.getMessage()));

        verify(loanService).update(request);
        verifyNoMoreInteractions(loanService);
    }
}
