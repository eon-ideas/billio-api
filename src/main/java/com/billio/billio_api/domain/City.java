package com.billio.billio_api.domain;

/**
 * City entity representing a city with a name and postal code.
 */
public class City {
    
    private String name;
    private String postalCode;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
