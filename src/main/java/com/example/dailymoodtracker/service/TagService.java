package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.TagDto;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.model.Tag;
import com.example.dailymoodtracker.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {

    private static final String NOT_FOUND_MESSAGE = "Tag not found: ";

    private final TagRepository repository;
    private final MoodEntryService moodEntryService;

    public TagService(TagRepository repository,
                      MoodEntryService moodEntryService) {
        this.repository = repository;
        this.moodEntryService = moodEntryService;
    }

    public Tag create(Tag tag) {

        if (tag.getName() == null || tag.getName().isBlank()) {
            throw new DataConflictException("Tag name cannot be empty");
        }

        Tag saved = repository.save(tag);
        moodEntryService.invalidateCache();
        return saved;
    }

    public List<Tag> getAll() {
        return repository.findAll();
    }

    public Tag getById(Long id) {
        return repository.findWithMoodEntriesById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));
    }

    @Transactional
    public Tag update(Long id, TagDto dto) {
        Tag tag = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        if (dto.name() != null && dto.name().isBlank()) {
            throw new DataConflictException("Tag name cannot be empty");
        }

        if (dto.name() != null) {
            tag.setName(dto.name());
        }

        if (dto.color() != null) {
            tag.setColor(dto.color());
        }

        Tag updated = repository.save(tag);

        moodEntryService.invalidateCache();

        return updated;
    }

    @Transactional
    public void delete(Long id) {
        Tag tag = repository.findWithMoodEntriesById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        for (MoodEntry entry : tag.getMoodEntries()) {
            entry.getTags().remove(tag);
        }

        tag.getMoodEntries().clear();

        repository.delete(tag);

        moodEntryService.invalidateCache();
    }
}