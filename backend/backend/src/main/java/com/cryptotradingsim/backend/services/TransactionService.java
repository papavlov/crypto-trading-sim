package com.cryptotradingsim.backend.services;

import org.springframework.stereotype.Service;
import com.cryptotradingsim.backend.models.Transaction;
import com.cryptotradingsim.backend.repositories.TransactionRepository;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final KrakenWebSocketClient krakenWebSocketClient;

    public TransactionService(TransactionRepository transactionRepository,
                              KrakenWebSocketClient krakenWebSocketClient) {
        this.transactionRepository = transactionRepository;
        this.krakenWebSocketClient = krakenWebSocketClient;
    }

    public List<Transaction> getTransactionsByUserId(int userId) {
        return transactionRepository.getTransactionsByUserId(userId);
    }

    public void addTransaction(int userId, String cryptoName, String symbol, double quantity, String type) {
        Double livePrice = krakenWebSocketClient.getLatestPrice(symbol.toUpperCase());

        if (livePrice == null) {
            throw new RuntimeException("Live price not available for: " + symbol);
        }

        double total = livePrice * quantity;

        transactionRepository.addTransaction(userId, cryptoName, symbol.toUpperCase(), livePrice, quantity, total, type);
    }
}
