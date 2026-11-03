package com.billio.billio_api.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Class representing a HUB 2D barcode data structure compatible with Croatian HRVHUB30 standard.
 */
public class Hub2DBarcode {
    
    public static final String HRVHUB30 = "HRVHUB30";
    public static final String EUR = "EUR";
    public static final String COST = "COST";
    private static final String LF = "\n";
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    
    private String header = HRVHUB30;
    private String currency = EUR;
    private BigDecimal amount;
    private String companyName;
    private String addressStreet;
    private String addressHouseNumber;
    private String addressPostalCode;
    private String addressCity;
    private String iban;
    private String model;
    private String callNumber;
    private String code = COST;
    private String description;
    
    /**
     * Formats the amount according to the HRVHUB30 standard (15 digits with leading zeros).
     * 
     * @return formatted amount string
     */
    private String formattedAmount() {
        if (amount != null) {
            DecimalFormat decimalFormat = new DecimalFormat("000000000000000");
            return decimalFormat.format(amount.multiply(HUNDRED));
        }
        return "";
    }
    
    /**
     * Combines street and house number into a single string.
     * 
     * @return combined street and house number
     */
    private String streetWithHouseNumber() {
        StringBuilder sb = new StringBuilder();
        if (addressStreet != null) {
            sb.append(addressStreet.trim()).append(" ");
        }
        if (addressHouseNumber != null) {
            sb.append(addressHouseNumber.trim());
        }
        return sb.toString().trim();
    }
    
    /**
     * Combines postal code and city into a single string.
     * 
     * @return combined postal code and city
     */
    private String postalCodeWithCity() {
        StringBuilder sb = new StringBuilder();
        if (addressPostalCode != null) {
            sb.append(addressPostalCode.trim()).append(" ");
        }
        if (addressCity != null) {
            sb.append(addressCity.trim());
        }
        return sb.toString().trim();
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getAddressStreet() {
        return addressStreet;
    }
    
    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }
    
    public String getAddressHouseNumber() {
        return addressHouseNumber;
    }
    
    public void setAddressHouseNumber(String addressHouseNumber) {
        this.addressHouseNumber = addressHouseNumber;
    }
    
    public String getAddressPostalCode() {
        return addressPostalCode;
    }
    
    public void setAddressPostalCode(String addressPostalCode) {
        this.addressPostalCode = addressPostalCode;
    }
    
    public String getAddressCity() {
        return addressCity;
    }
    
    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
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
    
    public String getHeader() {
        return header;
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append(LF);
        sb.append(currency).append(LF);
        sb.append(formattedAmount()).append(LF);
        sb.append(LF);
        sb.append(LF);
        sb.append(LF);
        sb.append(companyName != null ? companyName.trim() : "").append(LF);
        sb.append(streetWithHouseNumber()).append(LF);
        sb.append(postalCodeWithCity()).append(LF);
        sb.append(iban != null ? iban.trim() : "").append(LF);
        sb.append(model != null ? model.trim() : "").append(LF);
        sb.append(callNumber != null ? callNumber.trim() : "").append(LF);
        sb.append(code).append(LF);
        sb.append(description != null ? description.trim() : "").append(LF);
        
        return sb.toString();
    }
}
