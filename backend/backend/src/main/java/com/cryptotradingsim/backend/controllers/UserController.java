package com.cryptotradingsim.backend.controllers;

import com.cryptotradingsim.backend.models.User;
import com.cryptotradingsim.backend.services.KrakenWebSocketClient;
import com.cryptotradingsim.backend.services.TransactionService;
import com.cryptotradingsim.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final KrakenWebSocketClient krakenWebSocketClient;

    public UserController(UserService userService, TransactionService transactionService, KrakenWebSocketClient krakenWebSocketClient) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.krakenWebSocketClient = krakenWebSocketClient;
    }

    // Create a new user
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestParam String username) {
        userService.createUser(username);
        return ResponseEntity.ok("User created with initial balance.");
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Reset account balance
    @PostMapping("/{id}/reset")
    public ResponseEntity<String> resetBalance(@PathVariable int id) {
        userService.resetBalance(id);
        return ResponseEntity.ok("Balance reset to initial value.");
    }

    // Buy crypto using live Kraken price
    @PostMapping("/{id}/buy")
    public ResponseEntity<String> buyCrypto(
            @PathVariable int id,
            @RequestParam String cryptoName,
            @RequestParam String symbol,
            @RequestParam double quantity
    ) {
        Double livePrice = krakenWebSocketClient.getLatestPrice(symbol.toUpperCase());

        if (livePrice == null) {
            return ResponseEntity.badRequest().body("Live price not available for: " + symbol.toUpperCase());
        }

        double totalCost = livePrice * quantity;

        boolean success = userService.updateBalanceForBuy(id, totalCost);
        if (!success) {
            return ResponseEntity.badRequest().body("Insufficient funds.");
        }

        transactionService.addTransaction(id, cryptoName, symbol, quantity, "BUY");
        return ResponseEntity.ok("Purchase successful at price: $" + livePrice);
    }

    // Sell crypto using live Kraken price
    @PostMapping("/{id}/sell")
    public ResponseEntity<String> sellCrypto(
            @PathVariable int id,
            @RequestParam String cryptoName,
            @RequestParam String symbol,
            @RequestParam double quantity
    ) {
        Double livePrice = krakenWebSocketClient.getLatestPrice(symbol.toUpperCase());

        if (livePrice == null) {
            return ResponseEntity.badRequest().body("Live price not available for: " + symbol.toUpperCase());
        }

        double totalGain = livePrice * quantity;

        userService.updateBalanceForSell(id, totalGain);
        transactionService.addTransaction(id, cryptoName, symbol, quantity, "SELL");
        return ResponseEntity.ok("Sell successful at price: $" + livePrice);
    }
}
