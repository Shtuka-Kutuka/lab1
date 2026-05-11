package com.example.dailymoodtracker.mapper;

import com.example.dailymoodtracker.dto.TagDto;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.model.Tag;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TagMapper {

    public TagDto toDto(Tag tag) {
        List<Long> moodEntryIds = Collections.emptyList();

        if (tag.getMoodEntries() != null) {
            moodEntryIds = tag.getMoodEntries().stream()
                .map(MoodEntry::getId)
                .toList();
        }

        return new TagDto(
            tag.getId(),
            tag.getName(),
            tag.getColor(),
            moodEntryIds
        );
    }

    public Tag toEntity(TagDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.name());
        tag.setColor(dto.color());
        return tag;
    }
}