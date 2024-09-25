package com.szasemkov.loancalc.dto;

import com.szasemkov.loancalc.model.InternalErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private InternalErrorStatus status;

    private String message;
}
