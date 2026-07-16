package com.code.tarun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse {

    private String transactionType;

    private double amount;

    private String status;

    private LocalDateTime transactionTime;
}