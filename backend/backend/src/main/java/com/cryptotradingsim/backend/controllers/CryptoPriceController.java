package com.cryptotradingsim.backend.controllers;

import com.cryptotradingsim.backend.models.CryptoPrice;
import com.cryptotradingsim.backend.services.KrakenWebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin(origins = "*") //Allow all frontend origins to access this endpoint
@RestController
@RequestMapping("/api/prices")
public class CryptoPriceController {

    @Autowired
    private KrakenWebSocketClient krakenClient;

    @GetMapping("/top20")
    public Collection<CryptoPrice> getTop20Prices() {
        return krakenClient.getPrices().values();
    }
}
