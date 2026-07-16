package com.code.tarun.service;

import com.code.tarun.dto.AccountResponse;
import com.code.tarun.dto.CreateAccountRequest;
import com.code.tarun.entity.Account;
import com.code.tarun.entity.AccountStatus;
import com.code.tarun.entity.AccountType;
import com.code.tarun.entity.Users;
import com.code.tarun.repository.AccountRepository;
import com.code.tarun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountResponse createAccount(CreateAccountRequest request) {
        Users user = userRepository.findByName(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();

        account.setAccountHolderName(request.getAccountHolderName());

        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.valueOf(request.getInitialBalance()));
        account.setStatus(AccountStatus.ACTIVE);
        account.setAccountNumber(generateAccountNumber());
        account.setUser(user);
        accountRepository.save(account);

        return new AccountResponse(
                account.getAccountNumber(),
                account.getAccountHolderName(),
                account.getAccountType().name(),
                account.getStatus().name(),
                account.getBalance().doubleValue()
        );
    }

    private String generateAccountNumber() {
        return String.valueOf(1000000000L + new Random().nextInt(900000000));
    }
}
