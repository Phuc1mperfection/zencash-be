package com.example.zencash.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String username;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String fullname;
    private String currency;
    private String avatar;
    private Set<String> roles; 

}