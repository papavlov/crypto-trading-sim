package com.cryptotradingsim.backend.services;

import com.cryptotradingsim.backend.models.Transaction;
import com.cryptotradingsim.backend.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void clearTransactionsForUser(int userId) {
        transactionRepository.clearAllTransactionsForUser(userId);
    }

    public void addTransaction(int userId, String cryptoName, String symbol, double quantity, String type) {
        BigDecimal livePrice = new BigDecimal(krakenWebSocketClient.getLatestPrice(symbol.toUpperCase()));

        if (livePrice == null) {
            throw new RuntimeException("Live price not available for: " + symbol);
        }

        BigDecimal total = livePrice.multiply(new BigDecimal(quantity));
        BigDecimal profitLoss = null; //when buying profitloss null

        if (type.equalsIgnoreCase("SELL")) {
            List<Transaction> buys = transactionRepository.getBuyTransactionsByUserAndSymbol(userId, symbol.toUpperCase());
            List<Transaction> sells = transactionRepository.getSellTransactionsByUserAndSymbol(userId, symbol.toUpperCase());

            buys.sort(Comparator.comparing(Transaction::getTimestamp));
            sells.sort(Comparator.comparing(Transaction::getTimestamp));

            BigDecimal remainingToSell = new BigDecimal(quantity);
            BigDecimal totalCost = BigDecimal.ZERO;

            for (Transaction buy : buys) {
                if (remainingToSell.compareTo(BigDecimal.ZERO) <= 0) break;

                BigDecimal soldQtyAgainstBuy = BigDecimal.ZERO;
                BigDecimal buyQty = new BigDecimal(buy.getQuantity());

                for (Transaction sell : sells) {
                    if (!sell.getTimestamp().after(buy.getTimestamp())) continue;

                    BigDecimal alreadyMatched = BigDecimal.valueOf(Math.min(sell.getQuantity(), buy.getQuantity() - soldQtyAgainstBuy.doubleValue()));
                    soldQtyAgainstBuy = soldQtyAgainstBuy.add(alreadyMatched);

                    if (soldQtyAgainstBuy.compareTo(buyQty) >= 0) break;
                }

                BigDecimal availableQty = buyQty.subtract(soldQtyAgainstBuy);
                if (availableQty.compareTo(BigDecimal.ZERO) <= 0) continue;

                BigDecimal usedQty = availableQty.min(remainingToSell);
                totalCost = totalCost.add(usedQty.multiply(new BigDecimal(buy.getPrice())));
                remainingToSell = remainingToSell.subtract(usedQty);
            }

            if (remainingToSell.compareTo(BigDecimal.ZERO) > 0) {
                throw new RuntimeException("Not enough holdings to sell " + quantity + " of " + symbol);
            }

            profitLoss = total.subtract(totalCost); //calc profit or loss
        }

        //add the transaction with profitLoss or null (for BUY operation)
        transactionRepository.addTransaction(
                userId,
                cryptoName,
                symbol.toUpperCase(),
                livePrice.doubleValue(),
                quantity,
                total.doubleValue(),
                type.toUpperCase(),
                profitLoss != null ? profitLoss.setScale(2, RoundingMode.HALF_UP).doubleValue() : null
        );
    }

    public Map<String, Double> getUserHoldings(int userId) {
        List<Transaction> transactions = transactionRepository.getTransactionsByUserId(userId);
        Map<String, Double> holdings = new HashMap<>();

        for (Transaction transaction : transactions) {
            String cryptoSymbol = transaction.getSymbol();
            double quantity = transaction.getQuantity();

            if ("BUY".equals(transaction.getType())) {
                holdings.put(cryptoSymbol, holdings.getOrDefault(cryptoSymbol, 0.0) + quantity);
            } else if ("SELL".equals(transaction.getType())) {
                holdings.put(cryptoSymbol, holdings.getOrDefault(cryptoSymbol, 0.0) - quantity);
            }
        }

        return holdings;
    }

}
