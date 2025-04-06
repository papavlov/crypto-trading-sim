package com.cryptotradingsim.backend.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class KrakenPairs {
    public static final Map<String, String> PAIRS = new LinkedHashMap<>();

    static {
        PAIRS.put("BTC/USD", "Bitcoin");
        PAIRS.put("ETH/USD", "Ethereum");
        PAIRS.put("USDT/USD", "Tether");
        PAIRS.put("XRP/USD", "XRP");
        PAIRS.put("SOL/USD", "Solana");
        PAIRS.put("ADA/USD", "Cardano");
        PAIRS.put("DOGE/USD", "Dogecoin");
        PAIRS.put("AVAX/USD", "Avalanche");
        PAIRS.put("DOT/USD", "Polkadot");
        PAIRS.put("LINK/USD", "Chainlink");
        PAIRS.put("TRX/USD", "Tron");
        PAIRS.put("MATIC/USD", "Polygon");
        PAIRS.put("BCH/USD", "Bitcoin Cash");
        PAIRS.put("LTC/USD", "Litecoin");
        PAIRS.put("ATOM/USD", "Cosmos");
        PAIRS.put("UNI/USD", "Uniswap");
        PAIRS.put("XLM/USD", "Stellar");
        PAIRS.put("ICP/USD", "Internet Computer");
        PAIRS.put("NEAR/USD", "NEAR Protocol");
        PAIRS.put("ETC/USD", "Ethereum Classic");
    }
}