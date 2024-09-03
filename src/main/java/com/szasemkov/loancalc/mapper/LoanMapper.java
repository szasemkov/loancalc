package com.szasemkov.loancalc.mapper;

import com.szasemkov.loancalc.dto.CreateLoanResponse;
import com.szasemkov.loancalc.dto.LoanPageResponse;
import com.szasemkov.loancalc.dto.LoanResponse;
import com.szasemkov.loancalc.dto.UpdateLoanRequest;
import com.szasemkov.loancalc.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LoanMapper {

    CreateLoanResponse loanToCreatedLoanResponse(Loan loan);

    LoanResponse loanToLoanResponse(Loan loan);

    List<LoanResponse> loansToLoanResponse(List<Loan> loans);

    @Mapping(target = "status", ignore = true)
    Loan updateLoanRequestToLoan(UpdateLoanRequest request);

    default LoanPageResponse loanToLoanPageResponse(List<Loan> loans) {
        List<LoanResponse> loanResponses = loansToLoanResponse(loans);
        return new LoanPageResponse(loanResponses);
    }

}
