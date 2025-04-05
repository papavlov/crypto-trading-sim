package com.cryptotradingsim.backend.services;

import org.springframework.stereotype.Service;
import com.cryptotradingsim.backend.models.Transaction;
import com.cryptotradingsim.backend.repositories.TransactionRepository;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactionsByUserId(int userId) {
        return transactionRepository.getTransactionsByUserId(userId);
    }

    public void addTransaction(int userId, String cryptoName, String symbol, double price, double quantity, String type) {
        double total = price * quantity;
        transactionRepository.addTransaction(userId, cryptoName, symbol, price, quantity, total, type);
    }
}
