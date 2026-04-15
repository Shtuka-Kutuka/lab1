package com.example.dailymoodtracker.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record TagDto(
    Long id,

    @NotBlank(message = "Tag name cannot be empty")
    String name,

    String color,

    List<Long> moodEntryIds
) { }