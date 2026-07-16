package com.code.tarun.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private String accountNumber;

    private double amount;
}
