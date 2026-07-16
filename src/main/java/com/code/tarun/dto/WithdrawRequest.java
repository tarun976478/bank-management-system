package com.code.tarun.dto;

import lombok.Data;

@Data
public class WithdrawRequest {

    private String accountNumber;

    private double amount;
}