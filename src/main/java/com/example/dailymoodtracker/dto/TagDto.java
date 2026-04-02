package com.example.dailymoodtracker.dto;

import java.util.List;

public record TagDto(
    Long id,
    String name,
    String color,
    List<Long> moodEntryIds
) { }