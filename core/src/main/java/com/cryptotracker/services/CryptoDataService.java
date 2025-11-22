package com.cryptotracker.services;

import com.cryptotracker.models.Cryptocurrency;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service for managing cryptocurrency data
 */
public class CryptoDataService {
    private List<Cryptocurrency> cryptocurrencies;
    private Random random;
    
    public CryptoDataService() {
        this.cryptocurrencies = new ArrayList<Cryptocurrency>();
        this.random = new Random();
        initializeCryptocurrencies();
    }
    
    private void initializeCryptocurrencies() {
        // Add popular cryptocurrencies with initial data
        Cryptocurrency btc = new Cryptocurrency("BTC", "Bitcoin");
        btc.setPrice(45000.0);
        btc.setMarketCap(880000000000.0);
        btc.setVolume24h(28000000000.0);
        btc.setChange24h(2.5);
        cryptocurrencies.add(btc);
        
        Cryptocurrency eth = new Cryptocurrency("ETH", "Ethereum");
        eth.setPrice(2500.0);
        eth.setMarketCap(300000000000.0);
        eth.setVolume24h(15000000000.0);
        eth.setChange24h(-1.2);
        cryptocurrencies.add(eth);
        
        Cryptocurrency usdt = new Cryptocurrency("USDT", "Tether");
        usdt.setPrice(1.0);
        usdt.setMarketCap(95000000000.0);
        usdt.setVolume24h(50000000000.0);
        usdt.setChange24h(0.01);
        cryptocurrencies.add(usdt);
        
        Cryptocurrency bnb = new Cryptocurrency("BNB", "Binance Coin");
        bnb.setPrice(320.0);
        bnb.setMarketCap(50000000000.0);
        bnb.setVolume24h(1200000000.0);
        bnb.setChange24h(3.8);
        cryptocurrencies.add(bnb);
        
        Cryptocurrency sol = new Cryptocurrency("SOL", "Solana");
        sol.setPrice(110.0);
        sol.setMarketCap(45000000000.0);
        sol.setVolume24h(2500000000.0);
        sol.setChange24h(5.2);
        cryptocurrencies.add(sol);
        
        Cryptocurrency xrp = new Cryptocurrency("XRP", "Ripple");
        xrp.setPrice(0.62);
        xrp.setMarketCap(33000000000.0);
        xrp.setVolume24h(1800000000.0);
        xrp.setChange24h(-0.8);
        cryptocurrencies.add(xrp);
        
        Cryptocurrency ada = new Cryptocurrency("ADA", "Cardano");
        ada.setPrice(0.58);
        ada.setMarketCap(20000000000.0);
        ada.setVolume24h(800000000.0);
        ada.setChange24h(1.5);
        cryptocurrencies.add(ada);
        
        Cryptocurrency doge = new Cryptocurrency("DOGE", "Dogecoin");
        doge.setPrice(0.12);
        doge.setMarketCap(17000000000.0);
        doge.setVolume24h(1500000000.0);
        doge.setChange24h(7.3);
        cryptocurrencies.add(doge);
    }
    
    public List<Cryptocurrency> getAllCryptocurrencies() {
        return cryptocurrencies;
    }
    
    public Cryptocurrency getCryptocurrency(String symbol) {
        for (Cryptocurrency crypto : cryptocurrencies) {
            if (crypto.getSymbol().equals(symbol)) {
                return crypto;
            }
        }
        return null;
    }
    
    /**
     * Update cryptocurrency prices (simulate real-time updates)
     */
    public void updatePrices() {
        for (Cryptocurrency crypto : cryptocurrencies) {
            // Simulate price changes
            double changePercent = (random.nextDouble() - 0.5) * 4; // -2% to +2%
            double newPrice = crypto.getPrice() * (1 + changePercent / 100);
            crypto.setPrice(newPrice);
            crypto.setChange24h(crypto.getChange24h() + changePercent);
        }
    }
    
    public List<Cryptocurrency> getTopCryptocurrencies(int count) {
        int size = Math.min(count, cryptocurrencies.size());
        return cryptocurrencies.subList(0, size);
    }
}
