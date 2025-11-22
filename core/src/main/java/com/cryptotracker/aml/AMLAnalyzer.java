package com.cryptotracker.aml;

import com.cryptotracker.models.Transaction;
import java.util.List;

/**
 * AML (Anti-Money Laundering) Analyzer
 * Analyzes cryptocurrency transactions for suspicious patterns
 */
public class AMLAnalyzer {
    
    private static final double SUSPICIOUS_AMOUNT_THRESHOLD = 10000.0;
    private static final int RAPID_TRANSACTION_WINDOW = 60; // seconds
    
    /**
     * Analyze a wallet address for AML compliance
     */
    public AMLAnalysisResult analyzeWallet(String walletAddress, List<Transaction> transactions) {
        AMLAnalysisResult result = new AMLAnalysisResult(walletAddress);
        
        if (transactions == null || transactions.isEmpty()) {
            result.calculateRiskScore();
            return result;
        }
        
        double totalVolume = 0.0;
        int suspiciousCount = 0;
        
        // Analyze each transaction
        for (Transaction tx : transactions) {
            totalVolume += tx.getAmount();
            
            if (isSuspiciousTransaction(tx)) {
                suspiciousCount++;
            }
        }
        
        result.setTotalTransactionVolume(totalVolume);
        
        for (int i = 0; i < suspiciousCount; i++) {
            result.incrementSuspiciousTransactions();
        }
        
        // Check for structuring (breaking large transactions into smaller ones)
        if (detectStructuring(transactions)) {
            result.addFlag("Possible structuring detected");
            result.setRiskScore(result.getRiskScore() + 15);
        }
        
        // Check for mixing services
        if (detectMixingService(transactions)) {
            result.addFlag("Possible use of mixing service");
            result.setRiskScore(result.getRiskScore() + 20);
        }
        
        result.calculateRiskScore();
        
        return result;
    }
    
    /**
     * Check if a transaction is suspicious
     */
    private boolean isSuspiciousTransaction(Transaction tx) {
        // Large transactions are suspicious
        if (tx.getAmount() > SUSPICIOUS_AMOUNT_THRESHOLD) {
            return true;
        }
        
        // Round numbers might indicate structuring
        if (tx.getAmount() == Math.floor(tx.getAmount()) && tx.getAmount() > 1000) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Detect structuring patterns
     */
    private boolean detectStructuring(List<Transaction> transactions) {
        if (transactions.size() < 3) {
            return false;
        }
        
        int similarAmountCount = 0;
        
        // Check for multiple transactions of similar amounts
        for (int i = 0; i < transactions.size() - 1; i++) {
            for (int j = i + 1; j < transactions.size(); j++) {
                double diff = Math.abs(transactions.get(i).getAmount() - transactions.get(j).getAmount());
                if (diff < 100 && transactions.get(i).getAmount() > 1000) {
                    similarAmountCount++;
                }
            }
        }
        
        return similarAmountCount > 2;
    }
    
    /**
     * Detect potential use of mixing services
     */
    private boolean detectMixingService(List<Transaction> transactions) {
        // Check for multiple small transactions to different addresses
        int smallTransactionCount = 0;
        
        for (Transaction tx : transactions) {
            if (tx.getAmount() < 1.0 && tx.getAmount() > 0.001) {
                smallTransactionCount++;
            }
        }
        
        return smallTransactionCount > 5;
    }
    
    /**
     * Analyze a single transaction
     */
    public boolean isHighRiskTransaction(Transaction tx) {
        return tx.getAmount() > SUSPICIOUS_AMOUNT_THRESHOLD;
    }
}
