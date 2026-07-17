package com.code.tarun.service;

import com.code.tarun.dto.*;
import com.code.tarun.entity.*;
import com.code.tarun.exception.AccountNotFoundException;
import com.code.tarun.exception.InsufficientBalanceException;
import com.code.tarun.exception.InvalidAccountException;
import com.code.tarun.exception.InvalidAmountException;
import com.code.tarun.repository.AccountRepository;
import com.code.tarun.repository.TransactionRepository;
import com.code.tarun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AccountResponse createAccount(CreateAccountRequest request) {
        Users user = userRepository.findByName(request.getUsername())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

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
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
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
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if(account.getBalance().compareTo(BigDecimal.valueOf(request.getAmount())) < 0) {
           throw new InsufficientBalanceException("Insufficient balance");
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

    public List<TransactionResponse> getTransactionHistory(String accountNumber){

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        List<Transaction> transactions =
                transactionRepository.findByAccountOrderByTransactionTimeDesc(account);

        return transactions.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getTransactionType().name(),
                        transaction.getAmount().doubleValue(),
                        transaction.getStatus().name(),
                        transaction.getTransactionTime()
                ))
                .toList();
    }

    @Transactional
    public TransferResponse transferMoney(TransferRequest request) {
        if(request.getAmount() <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than zero.");
        }

        if(request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new InvalidAccountException("Cannot transfer to the sane account.");
        }

        Account fromAccount = accountRepository
                .findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() ->
                        new AccountNotFoundException("Source account not found."));

        Account toAccount = accountRepository
                .findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() ->
                        new AccountNotFoundException("Destination account not found."));

        BigDecimal amount = BigDecimal.valueOf(request.getAmount());

        if(fromAccount.getBalance().compareTo(amount) < 0 ) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        //Deduct form source account

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));

        //Credit destination account
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        //Transaction for source account
        Transaction debitTransaction = new Transaction();
        debitTransaction.setAccount(fromAccount);
        debitTransaction.setAmount(amount);
        debitTransaction.setTransactionType(TransactionType.WITHDRAW);
        debitTransaction.setStatus(TransactionStatus.SUCCESS);
        debitTransaction.setTransactionTime(LocalDateTime.now());

        //Transaction for destination account
        Transaction creditTransaction = new Transaction();
        creditTransaction.setAccount(toAccount);
        creditTransaction.setAmount(amount);
        creditTransaction.setTransactionType(TransactionType.DEPOSIT);
        creditTransaction.setStatus(TransactionStatus.SUCCESS);
        creditTransaction.setTransactionTime(LocalDateTime.now());

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        return new TransferResponse(
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                request.getAmount(),
                "Money transferred successfully"
        );
    }
}
