// 4. Response Model
// src/main/java/com/currencyconverter/model/ConversionResponse.java
package com.currencyconverter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Response model for currency conversion API
 */
public class ConversionResponse {
    
    @JsonProperty("convertedAmount")
    private BigDecimal convertedAmount;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("success")
    private boolean success;

    // Default constructor
    public ConversionResponse() {}

    // Constructor with parameters
    public ConversionResponse(BigDecimal convertedAmount, String message, boolean success) {
        this.convertedAmount = convertedAmount;
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ConversionResponse{" +
                "convertedAmount=" + convertedAmount +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}