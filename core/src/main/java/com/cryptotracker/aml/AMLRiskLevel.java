package com.cryptotracker.aml;

/**
 * AML Risk Level enumeration
 */
public enum AMLRiskLevel {
    LOW("Low Risk", 0, 30),
    MEDIUM("Medium Risk", 30, 70),
    HIGH("High Risk", 70, 90),
    CRITICAL("Critical Risk", 90, 100);
    
    private final String displayName;
    private final int minScore;
    private final int maxScore;
    
    AMLRiskLevel(String displayName, int minScore, int maxScore) {
        this.displayName = displayName;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getMinScore() {
        return minScore;
    }
    
    public int getMaxScore() {
        return maxScore;
    }
    
    public static AMLRiskLevel fromScore(int score) {
        if (score < 30) return LOW;
        if (score < 70) return MEDIUM;
        if (score < 90) return HIGH;
        return CRITICAL;
    }
}
