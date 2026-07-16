package com.code.tarun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WithdrawResponse {

    private String accountNumber;

    private double withdrawnAmount;

    private double currentBalance;

    private String message;
}