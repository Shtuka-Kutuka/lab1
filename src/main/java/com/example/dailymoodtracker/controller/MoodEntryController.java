package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.mapper.MoodEntryMapper;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.service.MoodEntryService;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/moods")
public class MoodEntryController {

    private final MoodEntryService service;
    private final MoodEntryMapper mapper;

    public MoodEntryController(MoodEntryService service, MoodEntryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<MoodEntryDto> getAll() {
        return service.getAll()
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping("/by-date")
    public List<MoodEntryDto> getByDate(
        @RequestParam("date") LocalDate date) {

        return service.getByDate(date)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public MoodEntryDto getById(@PathVariable Long id) {

        return service.getById(id)
            .map(mapper::toDto)
            .orElseThrow(() ->
                new ResourceNotFoundException("Mood not found: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MoodEntryDto create(@RequestBody MoodEntryDto dto) {

        MoodEntry entry = mapper.toEntity(dto);

        MoodEntry saved = service.save(entry, dto);

        return mapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public MoodEntryDto update(
        @PathVariable Long id,
        @RequestBody MoodEntryDto dto) {

        MoodEntry updated = service.update(id, dto);
        return mapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        service.delete(id);
    }
}