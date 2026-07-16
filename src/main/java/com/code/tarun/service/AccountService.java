package com.code.tarun.service;

import com.code.tarun.dto.*;
import com.code.tarun.entity.*;
import com.code.tarun.repository.AccountRepository;
import com.code.tarun.repository.TransactionRepository;
import com.code.tarun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

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

        String accountNumber;

        do {
            accountNumber = String.valueOf(
                    1000000000L + (long) (Math.random() * 9000000000L)
            );
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        return accountNumber;
    }

    public DepositResponse deposit(DepositRequest request) {
        if (request.getAmount() <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero.");
        }

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Acoount not found"));
        account.setBalance(
                account.getBalance().add(BigDecimal.valueOf(request.getAmount()))
        );

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setAccount(account);

        transactionRepository.save(transaction);

        return new DepositResponse(
                account.getAccountNumber(),
                request.getAmount(),
                account.getBalance().doubleValue(),
                "Amount deposited Successfully"
        );

    }

    public WithdrawResponse withdraw(WithdrawRequest request) {
        if(request.getAmount() <= 0) {
            throw new RuntimeException("Withdraw amount must be greater than zero.");
        }

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if(account.getBalance().compareTo(BigDecimal.valueOf(request.getAmount())) < 0) {
            throw new RuntimeException("Insufficient balance.");
        }

        account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(request.getAmount())));

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setAccount(account);

        transactionRepository.save(transaction);

        return new WithdrawResponse(
                account.getAccountNumber(),
                request.getAmount(),
                account.getBalance().doubleValue(),
                "Amount withdrawn successfully."
        );
    }
}
