package com.code.tarun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepositResponse {
    private String accountNumber;

    private double depositedAmount;

    private double currentBalance;

    private String message;
}
