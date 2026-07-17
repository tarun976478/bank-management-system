package com.code.tarun.dto;

import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferResponse {
    private String fromAccountNumber;
    private String toAccountNumber;
    private double transferredAmount;
    private String message;
}
