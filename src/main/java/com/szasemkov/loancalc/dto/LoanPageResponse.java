package com.szasemkov.loancalc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(setterPrefix = "set")
@AllArgsConstructor
public class LoanPageResponse {

    private List<LoanResponse> loans;
}
