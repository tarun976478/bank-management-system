package com.code.tarun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountResponse {

    private String accountNumber;

    private String accountHolderName;

    private String accountType;

    private String status;

    private double balance;
}
