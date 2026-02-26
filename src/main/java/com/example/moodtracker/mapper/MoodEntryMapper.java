package com.example.moodtracker.mapper;
import com.example.moodtracker.model.MoodEntry;
import com.example.moodtracker.dto.MoodEntryDto;
import org.springframework.stereotype.Component;
@Component
public class MoodEntryMapper {
    public MoodEntryDto toDto(MoodEntry entry) {
        return new MoodEntryDto(
                entry.getId(),
                entry.getMood(),
                entry.getDate()
        );
    }
}