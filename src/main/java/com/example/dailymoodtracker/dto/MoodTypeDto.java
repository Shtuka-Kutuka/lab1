package com.example.dailymoodtracker.dto;

import jakarta.validation.constraints.NotBlank;

public record MoodTypeDto(
    Long id,

    @NotBlank(message = "Mood name cannot be empty")
    String name,

    String emoji,
    String description


) { }