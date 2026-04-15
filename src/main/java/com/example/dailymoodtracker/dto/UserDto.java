package com.example.dailymoodtracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
    Long id,

    @NotBlank(message = "Username cannot be empty")
    String username,

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    String email
) { }