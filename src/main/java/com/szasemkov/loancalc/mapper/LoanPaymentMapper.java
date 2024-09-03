package com.szasemkov.loancalc.mapper;

import com.szasemkov.loancalc.dto.LoanPaymentPageResponse;
import com.szasemkov.loancalc.dto.LoanPaymentResponse;
import com.szasemkov.loancalc.model.LoanPayment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LoanPaymentMapper {

    List<LoanPaymentResponse> loanPaymentsToLoanPaymentResponse(List<LoanPayment> loanPayments);

    default LoanPaymentPageResponse loanPaymentsToLoanPaymentPageResponse(List<LoanPayment> loanPayments) {
        List<LoanPaymentResponse> loanPaymentResponses = loanPaymentsToLoanPaymentResponse(loanPayments);
        return new LoanPaymentPageResponse(loanPaymentResponses);
    }


}
