package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String username;
    private String email;
    private String accessToken;
    private String refreshToken;
}