package com.example.dailymoodtracker.dto;

public record MoodTypeDto(
    Long id,
    String name,
    String emoji,
    String description
) { }