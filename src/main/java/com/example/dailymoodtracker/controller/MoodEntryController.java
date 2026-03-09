package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.mapper.MoodEntryMapper;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.service.MoodEntryService;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public List<MoodEntryDto> getByDate(
        @RequestParam("date")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date) {

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
        return mapper.toDto(service.save(entry));
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