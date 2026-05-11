package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.TagDto;
import com.example.dailymoodtracker.mapper.TagMapper;
import com.example.dailymoodtracker.model.Tag;
import com.example.dailymoodtracker.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Tag API")
public class TagController {

    private final TagService service;
    private final TagMapper mapper;

    public TagController(TagService service, TagMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Get all tags")
    @GetMapping
    public List<TagDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    @Operation(summary = "Get tag by id")
    @GetMapping("/{id}")
    public TagDto getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }

    @Operation(summary = "Create tag")
    @PostMapping
    public TagDto create(@Valid @RequestBody TagDto dto) {
        Tag tag = mapper.toEntity(dto);
        return mapper.toDto(service.create(tag));
    }

    @Operation(summary = "Update tag")
    @PutMapping("/{id}")
    public TagDto update(@PathVariable Long id, @Valid @RequestBody TagDto dto) {
        return mapper.toDto(service.update(id, dto));
    }

    @GetMapping("/by-date")
    public List<TagDto> getTagsByDateAndUser(
        @RequestParam Long userId,
        @RequestParam String date
    ) {
        LocalDate localDate = LocalDate.parse(date);

        return service.findTagsByUserIdAndDate(userId, localDate)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Operation(summary = "Delete tag")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}