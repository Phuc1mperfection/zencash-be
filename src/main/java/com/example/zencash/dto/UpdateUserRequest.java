package com.example.zencash.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    private String password;
    private String name;

}
