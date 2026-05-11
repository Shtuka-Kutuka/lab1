package com.example.dailymoodtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record MoodEntryDto(
    Long id,

    @NotBlank
    String mood,

    @NotNull
    LocalDate date,

    @NotNull
    Long userId,

    List<Long> tagIds,

    List<TagDto> tags
) { }