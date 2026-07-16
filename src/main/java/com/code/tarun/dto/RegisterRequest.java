package com.code.tarun.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String password;
    private String role;
}
