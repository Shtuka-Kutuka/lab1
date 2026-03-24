package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.TagDto;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.Tag;
import com.example.dailymoodtracker.repository.TagRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private static final String NOT_FOUND_MESSAGE = "Tag not found: ";

    private final TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    public Tag create(Tag tag) {
        return repository.save(tag);
    }

    public List<Tag> getAll() {
        List<Tag> tags = repository.findAllWithNPlusOne();
        tags.forEach(tag -> Hibernate.initialize(tag.getMoodEntries()));
        return tags;
    }

    public Tag getById(Long id) {
        Tag tag = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        Hibernate.initialize(tag.getMoodEntries());

        return tag;
    }

    public Tag update(Long id, TagDto dto) {
        Tag tag = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        tag.setName(dto.name());
        tag.setColor(dto.color());

        return repository.save(tag);
    }

    public void delete(Long id) {
        Tag tag = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        repository.delete(tag);
    }
}