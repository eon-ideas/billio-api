package com.billio.billio_api.domain;

import java.math.BigDecimal;

/**
 * Payment entity representing payment information for barcode generation.
 */
public class Payment {
    
    private BigDecimal amount;
    private String recipientName;
    private Address recipientAddress;
    private String iban;
    private String model;
    private String callNumber;
    private String description;
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    public Address getRecipientAddress() {
        return recipientAddress;
    }
    
    public void setRecipientAddress(Address recipientAddress) {
        this.recipientAddress = recipientAddress;
    }
    
    public String getIban() {
        return iban;
    }
    
    public void setIban(String iban) {
        this.iban = iban;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getCallNumber() {
        return callNumber;
    }
    
    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
