package com.example.moodtracker.dto;
import java.time.LocalDate;
public class MoodEntryDto {
    private Long id;
    private String mood;
    private LocalDate date;
    public MoodEntryDto() {
    }
    public MoodEntryDto(Long id, String mood, LocalDate date) {
        this.id = id;
        this.mood = mood;
        this.date = date;
    }
    public Long getId() {
        return id;
    }
    public String getMood() {
        return mood;
    }
    public LocalDate getDate() {
        return date;
    }
}