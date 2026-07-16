package com.code.tarun.repository;

import com.code.tarun.entity.Account;
import com.code.tarun.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository  extends JpaRepository<Transaction,Long> {
    List<Transaction> findByAccountOrderByTransactionTimeDesc(Account account);
}
