package com.example.zencash.controller;

import com.example.zencash.dto.CurrencyConversionRequest;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.CurrencyConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    @Autowired
    private CurrencyConversionService currencyConversionService;

    @Autowired
    private UserRepository userRepository;

    @PatchMapping("/convert")
    public ResponseEntity<String> convertCurrency(@RequestBody CurrencyConversionRequest conversionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        if (!conversionRequest.getTargetCurrency().equals("VND") && !conversionRequest.getTargetCurrency().equals("USD")) {
            return ResponseEntity.badRequest().body("Invalid target currency. Must be 'VND' or 'USD'.");
        }

        currencyConversionService.convertCurrencyForUser(email, conversionRequest.getTargetCurrency());
        return ResponseEntity.ok("Currency conversion successful.");
    }

}
