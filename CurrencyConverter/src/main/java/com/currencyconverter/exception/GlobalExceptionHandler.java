package com.currencyconverter.exception;

import com.currencyconverter.model.ConversionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ConversionResponse> handleHttpClientError(HttpClientErrorException ex) {
        ConversionResponse response = new ConversionResponse(
            null, 
            "External API error: " + ex.getMessage(), 
            false
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ConversionResponse> handleResourceAccessError(ResourceAccessException ex) {
        ConversionResponse response = new ConversionResponse(
            null, 
            "Connection timeout. Please try again.", 
            false
        );
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ConversionResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ConversionResponse response = new ConversionResponse(
            null, 
            "Invalid input: " + ex.getMessage(), 
            false
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ConversionResponse> handleGenericError(Exception ex) {
        ConversionResponse response = new ConversionResponse(
            null, 
            "An unexpected error occurred: " + ex.getMessage(), 
            false
        );
        return ResponseEntity.internalServerError().body(response);
    }
}