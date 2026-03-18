package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.model.Tag;
import com.example.dailymoodtracker.repository.TagRepository;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
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

    private static final String NOT_FOUND_MESSAGE = "Tag not found: ";

    private final TagRepository repository;

    public TagController(TagRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Tag> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Tag getById(@PathVariable Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));
    }

    @PostMapping
    public Tag create(@RequestBody Tag tag) {
        return repository.save(tag);
    }

    @PutMapping("/{id}")
    public Tag update(@PathVariable Long id, @RequestBody Tag updated) {
        Tag tag = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        tag.setName(updated.getName());
        tag.setColor(updated.getColor());

        return repository.save(tag);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Tag tag = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        repository.delete(tag);
    }
}