package com.example.zencash.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ForgotPasswordRequest {
    private String email;
}
