package com.cryptotradingsim.backend.models;

import java.sql.Timestamp;

public class Transaction {

    private int id;
    private int userId;
    private String cryptoName;
    private String symbol;
    private double price;
    private double quantity;
    private double total;
    private String type;  // "BUY" or "SELL"
    private Timestamp timestamp;

    // Constructors
    public Transaction() {}

    public Transaction(int id, int userId, String cryptoName, String symbol, double price, double quantity, double total, String type, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.cryptoName = cryptoName;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
        this.type = type;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCryptoName() { return cryptoName; }
    public void setCryptoName(String cryptoName) { this.cryptoName = cryptoName; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}