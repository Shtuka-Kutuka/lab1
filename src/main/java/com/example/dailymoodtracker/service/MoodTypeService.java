package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.MoodTypeDto;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.model.MoodType;
import com.example.dailymoodtracker.repository.MoodEntryRepository;
import com.example.dailymoodtracker.repository.MoodTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MoodTypeService {

    private static final String NOT_FOUND_MESSAGE = "MoodType not found: ";

    private final MoodTypeRepository repository;
    private final MoodEntryRepository moodEntryRepository;
    private final MoodEntryService moodEntryService;

    public MoodTypeService(MoodTypeRepository repository,
                           MoodEntryRepository moodEntryRepository,
                           MoodEntryService moodEntryService) {
        this.repository = repository;
        this.moodEntryRepository = moodEntryRepository;
        this.moodEntryService = moodEntryService;
    }

    public MoodType create(MoodType moodType) {

        if (moodType.getName() == null || moodType.getName().isBlank()) {
            throw new DataConflictException("Mood name cannot be empty");
        }

        MoodType saved = repository.save(moodType);
        moodEntryService.invalidateCache();
        return saved;
    }

    public List<MoodType> getAll() {
        return repository.findAll();
    }

    public MoodType getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));
    }

    @Transactional
    public MoodType update(Long id, MoodTypeDto dto) {
        MoodType moodType = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        if (dto.name() != null && dto.name().isBlank()) {
            throw new DataConflictException("Mood name cannot be empty");
        }

        if (dto.name() != null) {
            moodType.setName(dto.name());
        }

        if (dto.emoji() != null) {
            moodType.setEmoji(dto.emoji());
        }

        if (dto.description() != null) {
            moodType.setDescription(dto.description());
        }

        MoodType updated = repository.save(moodType);

        moodEntryService.invalidateCache();

        return updated;
    }

    @Transactional
    public void delete(Long id) {
        MoodType moodType = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));
        List<MoodEntry> entries = moodEntryRepository.findAll();

        for (MoodEntry entry : entries) {
            if (entry.getMoodType() != null &&
                entry.getMoodType().getId().equals(id)) {
                entry.setMoodType(null);
            }
        }

        repository.delete(moodType);

        moodEntryService.invalidateCache();
    }
}