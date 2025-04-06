package com.cryptotradingsim.backend.services;

import com.cryptotradingsim.backend.models.CryptoPrice;
import com.cryptotradingsim.backend.utils.KrakenPairs;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KrakenWebSocketClient extends WebSocketClient {

    private static final String KRAKEN_WS_URL = "wss://ws.kraken.com/v2";
    private final ObjectMapper objectMapper = new ObjectMapper();

    //key = "BTC", "ETH"...
    private static final Map<String, CryptoPrice> prices = new ConcurrentHashMap<>();

    public KrakenWebSocketClient() throws Exception {
        super(new URI(KRAKEN_WS_URL));
    }

    @PostConstruct
    public void start() {
        this.connect();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to Kraken WebSocket");

        try {
            String[] pairs = KrakenPairs.PAIRS.keySet().toArray(new String[0]);

            String subscribeMessage = objectMapper.writeValueAsString(Map.of(
                    "method", "subscribe",
                    "params", Map.of(
                            "channel", "ticker",
                            "symbol", pairs
                    )
            ));

            System.out.println("Subscribing to pairs: " + Arrays.toString(pairs));
            System.out.println("Subscription message: " + subscribeMessage);

            this.send(subscribeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            if ("ticker".equals(jsonNode.get("channel").asText()) && jsonNode.has("data")) {
                for (JsonNode dataNode : jsonNode.get("data")) {
                    String krakenSymbol = dataNode.get("symbol").asText(); //like "XBT/USD"
                    double lastPrice = dataNode.get("last").asDouble();

                    //Convert Kraken pair like "XBT/USD" to "BTC"
                    String displaySymbol = krakenSymbol.replace("/USD", "").replace("XBT", "BTC");
                    String name = KrakenPairs.PAIRS.getOrDefault(krakenSymbol, krakenSymbol);

                    prices.put(displaySymbol, new CryptoPrice(displaySymbol, name, lastPrice));

                    System.out.println("Updated price for " + displaySymbol + ": " + lastPrice);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //test print
        System.out.println("Incoming message from Kraken: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public Map<String, CryptoPrice> getPrices() {
        return prices;
    }

    //method to get the latest price by symbol (BTC, ETH, etc.)
    public Double getLatestPrice(String symbol) {
        CryptoPrice price = prices.get(symbol.toUpperCase());
        return price != null ? price.getPrice() : null;
    }
}
