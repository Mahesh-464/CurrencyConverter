package com.currencyconverter.controller;

import com.currencyconverter.service.CurrencyService;
import com.currencyconverter.model.ConversionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    /**
     * Convert currency endpoint
     * @param from Source currency code (e.g., USD)
     * @param to Target currency code (e.g., EUR)
     * @param amount Amount to convert
     * @return ConversionResponse with converted amount
     */
    @GetMapping("/convert")
    public ResponseEntity<ConversionResponse> convertCurrency(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("amount") BigDecimal amount) {
        
        try {
            // Validate input parameters
            if (from == null || from.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ConversionResponse(null, "Source currency is required", false));
            }
            
            if (to == null || to.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ConversionResponse(null, "Target currency is required", false));
            }
            
            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest()
                    .body(new ConversionResponse(null, "Valid amount is required", false));
            }

            // Perform currency conversion
            BigDecimal convertedAmount = currencyService.convertCurrency(from, to, amount);
            
            ConversionResponse response = new ConversionResponse(
                convertedAmount, 
                "Conversion successful", 
                true
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ConversionResponse errorResponse = new ConversionResponse(
                null, 
                "Conversion failed: " + e.getMessage(), 
                false
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Currency Converter API is running");
    }

    /**
     * Get supported currencies endpoint
     */
    @GetMapping("/currencies")
    public ResponseEntity<String[]> getSupportedCurrencies() {
        return ResponseEntity.ok(currencyService.getSupportedCurrencies());
    }
}