package com.cryptotracker.services;

import com.cryptotracker.models.Transaction;
import com.cryptotracker.models.Wallet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service for managing wallet data
 */
public class WalletService {
    private List<Wallet> wallets;
    private Random random;
    
    public WalletService() {
        this.wallets = new ArrayList<Wallet>();
        this.random = new Random();
        initializeSampleWallets();
    }
    
    private void initializeSampleWallets() {
        // Create sample wallet
        Wallet wallet1 = new Wallet("0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb", "Main Wallet");
        wallet1.addBalance(new Wallet.WalletBalance("BTC", 0.5, 22500.0));
        wallet1.addBalance(new Wallet.WalletBalance("ETH", 5.2, 13000.0));
        wallet1.addBalance(new Wallet.WalletBalance("USDT", 10000.0, 10000.0));
        
        // Add sample transactions
        addSampleTransactions(wallet1);
        
        wallets.add(wallet1);
    }
    
    private void addSampleTransactions(Wallet wallet) {
        // Add some sample transactions with varying amounts
        Transaction tx1 = new Transaction(
            "tx1",
            wallet.getAddress(),
            "0x8ba1f109551bD432803012645Ac136ddd64DBA72",
            0.05,
            "BTC"
        );
        tx1.setType(Transaction.TransactionType.SEND);
        wallet.addTransaction(tx1);
        
        Transaction tx2 = new Transaction(
            "tx2",
            "0x123d35Cc6634C0532925a3b844Bc9e7595f0123",
            wallet.getAddress(),
            1.2,
            "ETH"
        );
        tx2.setType(Transaction.TransactionType.RECEIVE);
        wallet.addTransaction(tx2);
        
        Transaction tx3 = new Transaction(
            "tx3",
            wallet.getAddress(),
            "0x456d35Cc6634C0532925a3b844Bc9e7595f0456",
            5000.0,
            "USDT"
        );
        tx3.setType(Transaction.TransactionType.SEND);
        wallet.addTransaction(tx3);
        
        // Add more transactions for AML testing
        for (int i = 0; i < 10; i++) {
            Transaction tx = new Transaction(
                "tx_" + (i + 4),
                wallet.getAddress(),
                generateRandomAddress(),
                random.nextDouble() * 1000,
                "USDT"
            );
            tx.setType(Transaction.TransactionType.SEND);
            wallet.addTransaction(tx);
        }
    }
    
    private String generateRandomAddress() {
        String chars = "0123456789abcdef";
        StringBuilder sb = new StringBuilder("0x");
        for (int i = 0; i < 40; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    public List<Wallet> getAllWallets() {
        return wallets;
    }
    
    public Wallet getWallet(String address) {
        for (Wallet wallet : wallets) {
            if (wallet.getAddress().equals(address)) {
                return wallet;
            }
        }
        return null;
    }
    
    public void addWallet(Wallet wallet) {
        wallets.add(wallet);
    }
    
    public List<Transaction> getWalletTransactions(String address) {
        Wallet wallet = getWallet(address);
        if (wallet != null) {
            return wallet.getTransactions();
        }
        return new ArrayList<Transaction>();
    }
}
