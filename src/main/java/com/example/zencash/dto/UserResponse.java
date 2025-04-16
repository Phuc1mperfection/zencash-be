package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse{
    private String email;
    private String username;
    private String fullname;
    private String avatar;
    private String currency;
}
