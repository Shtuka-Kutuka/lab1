package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.MoodTypeDto;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.MoodType;
import com.example.dailymoodtracker.repository.MoodTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoodTypeService {

    private static final String NOT_FOUND_MESSAGE = "MoodType not found: ";

    private final MoodTypeRepository repository;

    public MoodTypeService(MoodTypeRepository repository) {
        this.repository = repository;
    }

    public MoodType create(MoodType moodType) {
        return repository.save(moodType);
    }

    public List<MoodType> getAll() {
        return repository.findAll();
    }

    public MoodType getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));
    }

    public MoodType update(Long id, MoodTypeDto dto) {
        MoodType moodType = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        moodType.setName(dto.name());
        moodType.setEmoji(dto.emoji());
        moodType.setDescription(dto.description());

        return repository.save(moodType);
    }

    public void delete(Long id) {
        MoodType moodType = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        repository.delete(moodType);
    }
}