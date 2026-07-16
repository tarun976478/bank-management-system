package com.code.tarun.repository;

import com.code.tarun.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository  extends JpaRepository<Transaction,Long> {
}
