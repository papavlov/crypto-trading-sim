package com.cryptotradingsim.backend;

import com.cryptotradingsim.backend.models.Transaction;
import com.cryptotradingsim.backend.repositories.TransactionRepository;
import com.cryptotradingsim.backend.repositories.UserRepository;
import com.cryptotradingsim.backend.services.KrakenWebSocketClient;
import com.cryptotradingsim.backend.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private KrakenWebSocketClient krakenWebSocketClient;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransactionsByUserId() {
        // Arrange
        int userId = 1;
        List<Transaction> mockTransactions = Collections.singletonList(new Transaction(1, userId, "Bitcoin", "BTC", 50000, 0.5, 25000.0, "BUY", null, null));
        when(transactionRepository.getTransactionsByUserId(userId)).thenReturn(mockTransactions);

        // Act
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);

        // Assert
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals("Bitcoin", transactions.get(0).getCryptoName());
        verify(transactionRepository, times(1)).getTransactionsByUserId(userId);
    }

    @Test
    void testClearTransactionsForUser() {
        // Arrange
        int userId = 1;

        // Act
        transactionService.clearTransactionsForUser(userId);

        // Assert
        verify(transactionRepository, times(1)).clearAllTransactionsForUser(userId);
    }

    @Test
    void testAddTransactionBuy() {
        // Arrange
        int userId = 1;
        String cryptoName = "Bitcoin";
        String symbol = "BTC";
        double quantity = 0.5;
        String type = "BUY";

        // Mock the response for the live price from Kraken
        double livePrice = 50000.0;  // Mocked live price (double)
        when(krakenWebSocketClient.getLatestPrice(symbol.toUpperCase())).thenReturn(livePrice);

        // Act
        transactionService.addTransaction(userId, cryptoName, symbol, quantity, type);

        // Assert - verify that addTransaction was called with expected arguments
        verify(transactionRepository, times(1)).addTransaction(
                eq(userId),
                eq(cryptoName),
                eq(symbol.toUpperCase()),
                eq(livePrice),  // Use livePrice directly (no need to parse string)
                eq(quantity),
                eq(livePrice * quantity),  // Calculating the total
                eq(type.toUpperCase()),
                eq(null)  // Profit/Loss is null for BUY transaction
        );
    }

    @Test
    void testAddTransactionSell() {
        // Arrange
        int userId = 1;
        String cryptoName = "Bitcoin";
        String symbol = "BTC";
        double quantity = 0.5;
        String type = "SELL";

        // Mock the response for the live price from Kraken
        double livePrice = 50000.0;  // Mocked live price (double)
        when(krakenWebSocketClient.getLatestPrice(symbol.toUpperCase())).thenReturn(livePrice);

        // Mock buy transactions
        List<Transaction> mockBuyTransactions = Collections.singletonList(new Transaction(1, userId, "Bitcoin", "BTC", 50000, 0.5, 25000.0, "BUY", null, null));
        when(transactionRepository.getBuyTransactionsByUserAndSymbol(userId, symbol.toUpperCase())).thenReturn(mockBuyTransactions);

        // Act
        transactionService.addTransaction(userId, cryptoName, symbol, quantity, type);

        // Assert
        verify(transactionRepository, times(1)).addTransaction(
                eq(userId), eq(cryptoName), eq(symbol.toUpperCase()),
                eq(livePrice), eq(quantity),
                eq(livePrice * quantity),  // Calculating the total
                eq(type.toUpperCase()), anyDouble()  // Profit/Loss is calculated
        );
    }

    @Test
    void testAddTransactionSellNotEnoughHoldings() {
        // Arrange
        int userId = 1;
        String cryptoName = "Bitcoin";
        String symbol = "BTC";
        double quantity = 1.0; // Trying to sell 1 BTC, but the user has 0.5 BTC
        String type = "SELL";

        // Mock the response for the live price from Kraken
        double livePrice = 50000.0;  // Mocked live price (double)
        when(krakenWebSocketClient.getLatestPrice(symbol.toUpperCase())).thenReturn(livePrice);

        // Mock buy transactions
        List<Transaction> mockBuyTransactions = Collections.singletonList(new Transaction(1, userId, "Bitcoin", "BTC", 50000, 0.5, 25000.0, "BUY", null, null));
        when(transactionRepository.getBuyTransactionsByUserAndSymbol(userId, symbol.toUpperCase())).thenReturn(mockBuyTransactions);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                transactionService.addTransaction(userId, cryptoName, symbol, quantity, type)
        );
        assertEquals("Not enough holdings to sell 1.0 of BTC", exception.getMessage());
    }
}
