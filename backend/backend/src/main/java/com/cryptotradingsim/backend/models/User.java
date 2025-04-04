package com.cryptotradingsim.backend.models;

public class User {

    private int id;
    private String username;
    private double balance;

    //Constructors
    public User() {}

    public User(int id, String username, double balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

}
