package com.example.dailymoodtracker.mapper;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.model.MoodType;
import org.springframework.stereotype.Component;

@Component
public class MoodEntryMapper {

    public MoodEntryDto toDto(MoodEntry entity) {
        String moodName = "Unknown";
        if (entity.getMoodType() != null && entity.getMoodType().getName() != null) {
            moodName = entity.getMoodType().getName();
        }

        return new MoodEntryDto(
            entity.getId(),
            moodName,
            entity.getEntryDate()
        );
    }

    public MoodEntry toEntity(MoodEntryDto dto) {
        MoodEntry entity = new MoodEntry();
        entity.setId(dto.id());
        entity.setEntryDate(dto.date());

        if (dto.mood() != null && !dto.mood().isEmpty()) {
            MoodType moodType = new MoodType();
            moodType.setName(dto.mood());
            entity.setMoodType(moodType);
        }

        return entity;
    }
}