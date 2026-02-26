package com.example.moodtracker.controller;
import com.example.moodtracker.dto.MoodEntryDto;
import com.example.moodtracker.mapper.MoodEntryMapper;
import com.example.moodtracker.service.MoodEntryService;
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
    public List<MoodEntryDto> getByDate(@RequestParam LocalDate date) {
        return service.getByDate(date)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
    @GetMapping("/{id}")
    public MoodEntryDto getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }
}