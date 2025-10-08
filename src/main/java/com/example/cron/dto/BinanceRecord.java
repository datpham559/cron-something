package com.example.cron.dto;

import java.time.LocalDateTime;

public class BinanceRecord {
    private String symbol;
    private String price;
    private LocalDateTime fetchedAt;

    public BinanceRecord(String symbol, String price, LocalDateTime fetchedAt) {
        this.symbol = symbol;
        this.price = price;
        this.fetchedAt = fetchedAt;
    }

    public String getSymbol() { return symbol; }
    public String getPrice() { return price; }
    public LocalDateTime getFetchedAt() { return fetchedAt; }
}
