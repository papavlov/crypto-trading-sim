package com.cryptotradingsim.backend.repositories;

import com.cryptotradingsim.backend.models.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //RowMapper to map DB result to Transaction object
    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) -> new Transaction(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("crypto_name"),
            rs.getString("symbol"),
            rs.getDouble("price"),
            rs.getDouble("quantity"),
            rs.getDouble("total"),
            rs.getString("type"),
            rs.getTimestamp("timestamp"),
            rs.getObject("profit_loss") != null ? rs.getBigDecimal("profit_loss") : null
    );

    //Get all transactions for a user
    public List<Transaction> getTransactionsByUserId(int userId) {
        return jdbcTemplate.query(
                "SELECT * FROM transactions WHERE user_id = ? ORDER BY timestamp ASC, id ASC",
                transactionRowMapper,
                userId
        );

    }

    //Add a new transaction with profitLoss
    public void addTransaction(int userId, String cryptoName, String symbol, double price, double quantity, double total, String type, Double profitLoss) {
        jdbcTemplate.update(
                "INSERT INTO transactions (user_id, crypto_name, symbol, price, quantity, total, type, timestamp, profit_loss) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?)",
                userId, cryptoName, symbol, price, quantity, total, type, profitLoss
        );
    }

    //Get all BUY transactions by user and symbol
    public List<Transaction> getBuyTransactionsByUserAndSymbol(int userId, String symbol) {
        return jdbcTemplate.query(
                "SELECT * FROM transactions WHERE user_id = ? AND symbol = ? AND type = 'BUY'",
                transactionRowMapper,
                userId, symbol
        );
    }

    //Return all SELL transactions for a specific user and symbol
    public List<Transaction> getSellTransactionsByUserAndSymbol(int userId, String symbol) {
        return jdbcTemplate.query(
                "SELECT * FROM transactions WHERE user_id = ? AND symbol = ? AND type = 'SELL' ORDER BY timestamp ASC",
                transactionRowMapper,
                userId,
                symbol
        );
    }

    public void clearAllTransactionsForUser(int userId) {
        jdbcTemplate.update("DELETE FROM transactions WHERE user_id = ?", userId);
    }

}