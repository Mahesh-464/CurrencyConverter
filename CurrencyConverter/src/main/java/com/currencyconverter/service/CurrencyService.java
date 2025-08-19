package com.currencyconverter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {
    
    // Using ExchangeRate-API (free tier allows 1500 requests/month)
    private static final String API_BASE_URL = "https://api.exchangerate-api.com/v4/latest/";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // Cache for exchange rates (simple in-memory cache)
    private final Map<String, Map<String, BigDecimal>> rateCache = new HashMap<>();
    private final Map<String, Long> cacheTimestamps = new HashMap<>();
    private static final long CACHE_DURATION = 300000; // 5 minutes in milliseconds

    public CurrencyService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Convert currency using real-time exchange rates
     * @param fromCurrency Source currency code
     * @param toCurrency Target currency code
     * @param amount Amount to convert
     * @return Converted amount
     * @throws Exception if conversion fails
     */
    public BigDecimal convertCurrency(String fromCurrency, String toCurrency, BigDecimal amount) 
            throws Exception {
        
        // Normalize currency codes to uppercase
        fromCurrency = fromCurrency.toUpperCase().trim();
        toCurrency = toCurrency.toUpperCase().trim();
        
        // If same currency, return original amount
        if (fromCurrency.equals(toCurrency)) {
            return amount.setScale(6, RoundingMode.HALF_UP);
        }

        // Get exchange rate
        BigDecimal exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        
        // Calculate converted amount
        BigDecimal convertedAmount = amount.multiply(exchangeRate);
        return convertedAmount.setScale(6, RoundingMode.HALF_UP);
    }

    /**
     * Get exchange rate between two currencies with caching
     * @param fromCurrency Source currency
     * @param toCurrency Target currency
     * @return Exchange rate
     * @throws Exception if unable to fetch rate
     */
    private BigDecimal getExchangeRate(String fromCurrency, String toCurrency) throws Exception {
        
        // Check cache first
        if (isCacheValid(fromCurrency)) {
            Map<String, BigDecimal> rates = rateCache.get(fromCurrency);
            if (rates != null && rates.containsKey(toCurrency)) {
                return rates.get(toCurrency);
            }
        }

        // Fetch fresh rates from API
        try {
            Map<String, BigDecimal> rates = fetchExchangeRatesFromAPI(fromCurrency);
            
            if (!rates.containsKey(toCurrency)) {
                throw new Exception("Target currency '" + toCurrency + "' not supported");
            }
            
            // Cache the rates
            rateCache.put(fromCurrency, rates);
            cacheTimestamps.put(fromCurrency, System.currentTimeMillis());
            
            return rates.get(toCurrency);
            
        } catch (HttpClientErrorException e) {
            throw new Exception("Failed to fetch exchange rates: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Error processing exchange rate data: " + e.getMessage());
        }
    }

    /**
     * Fetch exchange rates from external API
     * @param baseCurrency Base currency for rates
     * @return Map of currency codes to exchange rates
     * @throws Exception if API call fails
     */
    private Map<String, BigDecimal> fetchExchangeRatesFromAPI(String baseCurrency) throws Exception {
        String url = API_BASE_URL + baseCurrency;
        String response = restTemplate.getForObject(url, String.class);
        
        if (response == null) {
            throw new Exception("Empty response from exchange rate API");
        }

        // Parse JSON response
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode ratesNode = rootNode.get("rates");
        
        if (ratesNode == null) {
            throw new Exception("Invalid response format from exchange rate API");
        }

        Map<String, BigDecimal> rates = new HashMap<>();
        
        // Add all available rates
        ratesNode.fields().forEachRemaining(entry -> {
            String currency = entry.getKey();
            double rate = entry.getValue().asDouble();
            rates.put(currency, BigDecimal.valueOf(rate));
        });
        
        // Add base currency rate (always 1.0)
        rates.put(baseCurrency, BigDecimal.ONE);
        
        return rates;
    }

    /**
     * Check if cached rates are still valid
     * @param currency Currency to check
     * @return true if cache is valid, false otherwise
     */
    private boolean isCacheValid(String currency) {
        Long timestamp = cacheTimestamps.get(currency);
        if (timestamp == null) {
            return false;
        }
        
        return (System.currentTimeMillis() - timestamp) < CACHE_DURATION;
    }

    /**
     * Get list of supported currencies
     * @return Array of supported currency codes
     */
    public String[] getSupportedCurrencies() {
        return new String[]{
            "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "INR", "KRW",
            "SGD", "NZD", "MXN", "NOK", "SEK", "RUB", "ZAR", "BRL", "HKD", "TRY",
            "PLN", "DKK", "CZK", "HUF", "ILS", "CLP", "PHP", "AED", "COP", "PEN",
            "THB", "MYR", "EGP", "SAR", "QAR", "KWD", "BHD", "OMR", "JOD", "LBP"
        };
    }
}