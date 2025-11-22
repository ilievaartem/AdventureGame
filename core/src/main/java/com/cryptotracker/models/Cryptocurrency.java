package com.cryptotracker.models;

/**
 * Cryptocurrency model
 */
public class Cryptocurrency {
    private String symbol;
    private String name;
    private double price;
    private double marketCap;
    private double volume24h;
    private double change24h;
    private String imageUrl;
    
    public Cryptocurrency(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
        this.price = 0.0;
        this.marketCap = 0.0;
        this.volume24h = 0.0;
        this.change24h = 0.0;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public double getMarketCap() {
        return marketCap;
    }
    
    public void setMarketCap(double marketCap) {
        this.marketCap = marketCap;
    }
    
    public double getVolume24h() {
        return volume24h;
    }
    
    public void setVolume24h(double volume24h) {
        this.volume24h = volume24h;
    }
    
    public double getChange24h() {
        return change24h;
    }
    
    public void setChange24h(double change24h) {
        this.change24h = change24h;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public boolean isPriceIncreasing() {
        return change24h > 0;
    }
    
    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }
    
    public String getFormattedChange() {
        return String.format("%.2f%%", change24h);
    }
}
