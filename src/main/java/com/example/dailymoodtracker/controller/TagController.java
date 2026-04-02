package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.TagDto;
import com.example.dailymoodtracker.mapper.TagMapper;
import com.example.dailymoodtracker.model.Tag;
import com.example.dailymoodtracker.service.TagService;
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
@RequestMapping("/api/tags")
public class TagController {

    private final TagService service;
    private final TagMapper mapper;

    public TagController(TagService service, TagMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<TagDto> getAll() {
        return service.getAll().stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public TagDto getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }

    @PostMapping
    public TagDto create(@RequestBody TagDto dto) {
        Tag tag = mapper.toEntity(dto);
        return mapper.toDto(service.create(tag));
    }

    @PutMapping("/{id}")
    public TagDto update(@PathVariable Long id, @RequestBody TagDto dto) {
        return mapper.toDto(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}