package com.cryptotradingsim.backend.controllers;

import com.cryptotradingsim.backend.models.Transaction;
import com.cryptotradingsim.backend.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Get all transactions by user
    @GetMapping("/{userId}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable int userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }
    @GetMapping("{userId}/holdings")
    public ResponseEntity<Map<String, Double>> getUserHoldings(@PathVariable int userId) {
        return ResponseEntity.ok(transactionService.getUserHoldings(userId));
    }


}
