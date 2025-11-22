package com.cryptotracker.aml;

import java.util.ArrayList;
import java.util.List;

/**
 * AML Analysis Result
 */
public class AMLAnalysisResult {
    private int riskScore;
    private AMLRiskLevel riskLevel;
    private List<String> flags;
    private String walletAddress;
    private double totalTransactionVolume;
    private int suspiciousTransactionCount;
    
    public AMLAnalysisResult(String walletAddress) {
        this.walletAddress = walletAddress;
        this.flags = new ArrayList<String>();
        this.riskScore = 0;
        this.totalTransactionVolume = 0.0;
        this.suspiciousTransactionCount = 0;
    }
    
    public void calculateRiskScore() {
        riskScore = 0;
        
        // Calculate risk based on various factors
        if (totalTransactionVolume > 1000000) {
            riskScore += 20;
            flags.add("High transaction volume detected");
        }
        
        if (suspiciousTransactionCount > 10) {
            riskScore += 30;
            flags.add("Multiple suspicious transactions");
        }
        
        // Check for rapid transactions
        if (suspiciousTransactionCount > 5 && totalTransactionVolume > 500000) {
            riskScore += 25;
            flags.add("Rapid high-value transactions");
        }
        
        // Determine risk level
        riskLevel = AMLRiskLevel.fromScore(riskScore);
    }
    
    public int getRiskScore() {
        return riskScore;
    }
    
    public AMLRiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    public List<String> getFlags() {
        return flags;
    }
    
    public void addFlag(String flag) {
        flags.add(flag);
    }
    
    public String getWalletAddress() {
        return walletAddress;
    }
    
    public double getTotalTransactionVolume() {
        return totalTransactionVolume;
    }
    
    public void setTotalTransactionVolume(double volume) {
        this.totalTransactionVolume = volume;
    }
    
    public int getSuspiciousTransactionCount() {
        return suspiciousTransactionCount;
    }
    
    public void incrementSuspiciousTransactions() {
        this.suspiciousTransactionCount++;
    }
    
    public void setRiskScore(int score) {
        this.riskScore = score;
        this.riskLevel = AMLRiskLevel.fromScore(score);
    }
}
