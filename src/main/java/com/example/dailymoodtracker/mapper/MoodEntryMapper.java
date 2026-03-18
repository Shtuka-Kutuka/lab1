package com.example.dailymoodtracker.mapper;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class MoodEntryMapper {

    public MoodEntryDto toDto(MoodEntry entity) {

        String moodName = "Unknown";
        if (entity.getMoodType() != null) {
            moodName = entity.getMoodType().getName();
        }

        Long userId = null;
        if (entity.getUser() != null) {
            userId = entity.getUser().getId();
        }

        return new MoodEntryDto(
            entity.getId(),
            moodName,
            entity.getEntryDate(),
            userId
        );
    }

    public MoodEntry toEntity(MoodEntryDto dto) {

        MoodEntry entity = new MoodEntry();

        entity.setEntryDate(dto.date());

        if (dto.userId() != null) {
            User user = new User();
            user.setId(dto.userId());
            entity.setUser(user);
        }

        return entity;
    }
}