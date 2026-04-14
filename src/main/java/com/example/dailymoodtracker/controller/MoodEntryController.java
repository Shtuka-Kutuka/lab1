package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.mapper.MoodEntryMapper;
import com.example.dailymoodtracker.service.MoodEntryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

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

    @PostMapping
    public MoodEntryDto create(@Valid @RequestBody MoodEntryDto dto) {
        return mapper.toDto(
            service.save(mapper.toEntity(dto), dto)
        );
    }

    @GetMapping
    public List<MoodEntryDto> getAll() {
        return service.findAll()
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping("/user/{userId}")
    public List<MoodEntryDto> getByUserId(@PathVariable Long userId) {
        return service.findByUserId(userId)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/complex")
    public Page<MoodEntryDto> complex(
        @RequestParam Long userId,
        @RequestParam String moodName,
        Pageable pageable
    ) {
        return service.findComplex(userId, moodName, pageable)
            .map(mapper::toDto);
    }

    @GetMapping("/complex/native")
    public Page<MoodEntryDto> complexNative(
        @RequestParam Long userId,
        @RequestParam String moodName,
        Pageable pageable
    ) {
        return service.findComplexNative(userId, moodName, pageable)
            .map(mapper::toDto);
    }
}