package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.mapper.MoodEntryMapper;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.service.MoodEntryService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moods")
public class MoodEntryController {

    private final MoodEntryService service;
    private final MoodEntryMapper mapper;

    public MoodEntryController(MoodEntryService service,
                               MoodEntryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/")
    public Map<String, Object> showUserInfo() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userName", "Steve");
        response.put("userStatus", "Java enjoyer");
        response.put("lastEntryDate", "2026-03-01");
        response.put("motivation", "finally some data!");
        response.put("available_endpoints", Map.of(
            "by_date", "/api/moods?date=2026-03-01",
            "by_id", "/api/moods/1"
        ));
        return response;
    }

    @GetMapping
    public List<MoodEntryDto> getByDate(
        @RequestParam("date")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date) {

        List<MoodEntry> entries = service.getByDate(date);

        return entries.stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public MoodEntryDto getById(
        @PathVariable("id") Long id) {

        MoodEntry entry = service.getById(id);

        if (entry == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Mood entry not found with id: " + id
            );
        }

        return mapper.toDto(entry);
    }
}