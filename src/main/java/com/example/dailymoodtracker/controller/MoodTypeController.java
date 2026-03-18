package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.model.MoodType;
import com.example.dailymoodtracker.repository.MoodTypeRepository;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/mood-types")
public class MoodTypeController {

    private static final String NOT_FOUND_MESSAGE = "MoodType not found: ";

    private final MoodTypeRepository repository;

    public MoodTypeController(MoodTypeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<MoodType> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public MoodType getById(@PathVariable Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));
    }

    @PostMapping
    public MoodType create(@RequestBody MoodType moodType) {
        return repository.save(moodType);
    }

    @PutMapping("/{id}")
    public MoodType update(@PathVariable Long id, @RequestBody MoodType updated) {
        MoodType moodType = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        moodType.setName(updated.getName());
        moodType.setEmoji(updated.getEmoji());
        moodType.setDescription(updated.getDescription());

        return repository.save(moodType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        MoodType moodType = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        repository.delete(moodType);
    }
}