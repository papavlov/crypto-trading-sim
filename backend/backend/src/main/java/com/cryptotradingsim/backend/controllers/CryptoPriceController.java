package com.cryptotradingsim.backend.controllers;

import com.cryptotradingsim.backend.models.CryptoPrice;
import com.cryptotradingsim.backend.services.KrakenWebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

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
