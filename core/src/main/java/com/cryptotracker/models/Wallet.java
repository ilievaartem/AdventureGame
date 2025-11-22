package com.cryptotracker.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Wallet model for cryptocurrency wallet
 */
public class Wallet {
    private String address;
    private String name;
    private List<WalletBalance> balances;
    private List<Transaction> transactions;
    
    public Wallet(String address, String name) {
        this.address = address;
        this.name = name;
        this.balances = new ArrayList<WalletBalance>();
        this.transactions = new ArrayList<Transaction>();
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<WalletBalance> getBalances() {
        return balances;
    }
    
    public void addBalance(WalletBalance balance) {
        balances.add(balance);
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
    
    public double getTotalBalanceUSD() {
        double total = 0.0;
        for (WalletBalance balance : balances) {
            total += balance.getValueUSD();
        }
        return total;
    }
    
    public String getShortAddress() {
        if (address == null || address.length() < 10) {
            return address;
        }
        return address.substring(0, 6) + "..." + address.substring(address.length() - 4);
    }
    
    public static class WalletBalance {
        private String cryptocurrency;
        private double amount;
        private double valueUSD;
        
        public WalletBalance(String cryptocurrency, double amount, double valueUSD) {
            this.cryptocurrency = cryptocurrency;
            this.amount = amount;
            this.valueUSD = valueUSD;
        }
        
        public String getCryptocurrency() {
            return cryptocurrency;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public void setAmount(double amount) {
            this.amount = amount;
        }
        
        public double getValueUSD() {
            return valueUSD;
        }
        
        public void setValueUSD(double valueUSD) {
            this.valueUSD = valueUSD;
        }
    }
}
