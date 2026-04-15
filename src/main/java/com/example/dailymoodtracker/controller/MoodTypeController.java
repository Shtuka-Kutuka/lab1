package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.MoodTypeDto;
import com.example.dailymoodtracker.model.MoodType;
import com.example.dailymoodtracker.service.MoodTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/mood-types")
@Tag(name = "Mood Types", description = "Mood type API")
public class MoodTypeController {

    private final MoodTypeService service;

    public MoodTypeController(MoodTypeService service) {
        this.service = service;
    }

    @Operation(summary = "Get all mood types")
    @GetMapping
    public List<MoodType> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Get mood type by id")
    @GetMapping("/{id}")
    public MoodType getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Create mood type")
    @PostMapping
    public MoodType create(@Valid @RequestBody MoodTypeDto dto) {
        MoodType moodType = new MoodType();
        moodType.setName(dto.name());
        moodType.setEmoji(dto.emoji());
        moodType.setDescription(dto.description());
        return service.create(moodType);
    }

    @Operation(summary = "Update mood type")
    @PutMapping("/{id}")
    public MoodType update(@PathVariable Long id, @Valid @RequestBody MoodTypeDto dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Delete mood type")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}