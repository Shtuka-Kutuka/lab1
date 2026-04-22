package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.mapper.MoodEntryMapper;
import com.example.dailymoodtracker.service.MoodEntryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/moods")
@Tag(name = "Mood Entries", description = "Mood tracking API")
public class MoodEntryController {

    private final MoodEntryService service;
    private final MoodEntryMapper mapper;

    public MoodEntryController(MoodEntryService service, MoodEntryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Create mood entry")
    @PostMapping
    public MoodEntryDto create(@Valid @RequestBody MoodEntryDto dto) {
        return mapper.toDto(service.save(mapper.toEntity(dto), dto));
    }

    @Operation(summary = "Bulk create WITHOUT transaction")
    @PostMapping("/bulk")
    public List<MoodEntryDto> bulk(@RequestBody List<MoodEntryDto> dtos) {
        return service.saveAll(dtos)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Operation(summary = "Bulk create WITH transaction")
    @PostMapping("/bulk/tx")
    public List<MoodEntryDto> bulkTx(@RequestBody List<MoodEntryDto> dtos) {
        return service.saveAllTransactional(dtos)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Operation(summary = "Bulk create VALIDATED (business)")
    @PostMapping("/bulk/validated")
    public List<MoodEntryDto> bulkValidated(@RequestBody List<MoodEntryDto> dtos) {
        return service.saveAllValidated(dtos)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Operation(summary = "Get all mood entries")
    @GetMapping
    public List<MoodEntryDto> getAll() {
        return service.findAll().stream().map(mapper::toDto).toList();
    }

    @Operation(summary = "Get mood entries by user id")
    @GetMapping("/user/{userId}")
    public List<MoodEntryDto> getByUserId(@PathVariable Long userId) {
        return service.findByUserId(userId).stream().map(mapper::toDto).toList();
    }

    @Operation(summary = "Delete mood entry")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Operation(summary = "Complex JPQL query")
    @GetMapping("/complex")
    public Page<MoodEntryDto> complex(
        @RequestParam Long userId,
        @RequestParam String moodName,
        Pageable pageable
    ) {
        return service.findComplex(userId, moodName, pageable)
            .map(mapper::toDto);
    }

    @Operation(summary = "Complex native query")
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