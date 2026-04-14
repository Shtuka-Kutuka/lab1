package com.example.dailymoodtracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
    Long id,

    @NotBlank
    String username,

    @NotBlank
    @Email
    String email
) { }