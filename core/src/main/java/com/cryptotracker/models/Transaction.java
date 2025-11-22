package com.cryptotracker.models;

/**
 * Transaction model for cryptocurrency transactions
 */
public class Transaction {
    private String id;
    private String fromAddress;
    private String toAddress;
    private double amount;
    private String cryptocurrency;
    private long timestamp;
    private String txHash;
    private TransactionType type;
    
    public enum TransactionType {
        SEND, RECEIVE, SWAP, STAKE
    }
    
    public Transaction(String id, String fromAddress, String toAddress, double amount, String cryptocurrency) {
        this.id = id;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.cryptocurrency = cryptocurrency;
        this.timestamp = System.currentTimeMillis();
        this.type = TransactionType.SEND;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFromAddress() {
        return fromAddress;
    }
    
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    
    public String getToAddress() {
        return toAddress;
    }
    
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getCryptocurrency() {
        return cryptocurrency;
    }
    
    public void setCryptocurrency(String cryptocurrency) {
        this.cryptocurrency = cryptocurrency;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getTxHash() {
        return txHash;
    }
    
    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public String getFormattedAmount() {
        return String.format("%.8f %s", amount, cryptocurrency);
    }
    
    public String getShortFromAddress() {
        if (fromAddress == null || fromAddress.length() < 10) {
            return fromAddress;
        }
        return fromAddress.substring(0, 6) + "..." + fromAddress.substring(fromAddress.length() - 4);
    }
    
    public String getShortToAddress() {
        if (toAddress == null || toAddress.length() < 10) {
            return toAddress;
        }
        return toAddress.substring(0, 6) + "..." + toAddress.substring(toAddress.length() - 4);
    }
}
