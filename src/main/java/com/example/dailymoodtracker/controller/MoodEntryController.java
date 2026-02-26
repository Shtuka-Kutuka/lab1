package com.example.dailymoodtracker.controller;
import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.mapper.MoodEntryMapper;
import com.example.dailymoodtracker.service.MoodEntryService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@RestController
public class MoodEntryController {

    private final MoodEntryService service;
    private final MoodEntryMapper mapper;

    public MoodEntryController(MoodEntryService service, MoodEntryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /* http://localhost:8080/*/
    @GetMapping("/")
    public Map<String, Object> showUserInfo() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userName", "Steve");
        response.put("userStatus", "Java enjouer");
        response.put("lastEntryDate", "2024-05-20");
        response.put("motivation", "no motivation today");
        response.put("available_endpoints", Map.of("filter_by_date", "/api/moods?date=2024-05-20", "get_by_id", "/api/moods/{id}"));
        return response;
    }
    @GetMapping("/api/moods")
    public List<MoodEntryDto> getByDate(@RequestParam LocalDate date) {
        return service.getByDate(date)
            .stream()
            .map(mapper::toDto)
            .toList();
    }
    @GetMapping("/api/moods/{id}")
    public MoodEntryDto getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }
}