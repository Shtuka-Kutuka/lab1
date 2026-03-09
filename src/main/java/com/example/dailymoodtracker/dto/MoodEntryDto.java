package com.example.dailymoodtracker.dto;
import java.time.LocalDate;
public record MoodEntryDto(Long id, String mood, LocalDate date) {}