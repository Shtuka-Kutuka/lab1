package com.example.dailymoodtracker.mapper;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.dto.TagDto;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.model.Tag;
import com.example.dailymoodtracker.model.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MoodEntryMapper {

    private final TagMapper tagMapper;

    public MoodEntryMapper(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    public MoodEntryDto toDto(MoodEntry entity) {

        String moodName = entity.getMoodType() != null
            ? entity.getMoodType().getName()
            : "Unknown";

        Long userId = entity.getUser() != null
            ? entity.getUser().getId()
            : null;

        List<Long> tagIds = entity.getTags() != null
            ? entity.getTags().stream()
            .map(Tag::getId)
            .toList()
            : Collections.emptyList();

        List<TagDto> tags = entity.getTags() != null
            ? entity.getTags().stream()
            .map(tagMapper::toDto)
            .toList()
            : Collections.emptyList();

        return new MoodEntryDto(
            entity.getId(),
            moodName,
            entity.getEntryDate(),
            userId,
            tagIds,
            tags
        );
    }

    public MoodEntry toEntity(MoodEntryDto dto) {
        MoodEntry entity = new MoodEntry();
        entity.setEntryDate(dto.date());

        User user = new User();
        user.setId(dto.userId());
        entity.setUser(user);

        return entity;
    }
}