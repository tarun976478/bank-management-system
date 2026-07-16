package com.code.tarun.dto;

import com.code.tarun.entity.AccountType;
import lombok.Data;

@Data
public class CreateAccountRequest {

    private String username;

    private AccountType accountType;

    private String accountHolderName;

    private double initialBalance;
}
