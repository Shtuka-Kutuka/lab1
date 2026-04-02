package com.example.dailymoodtracker.dto;

import java.time.LocalDate;
import java.util.List;

public record MoodEntryDto(
    Long id,
    String mood,
    LocalDate date,
    Long userId,
    List<Long> tagIds
) { }