package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.TagDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.Tag;
import com.example.dailymoodtracker.repository.TagRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock private TagRepository repository;
    @Mock private MoodEntryService moodEntryService;

    @InjectMocks
    private TagService service;

    @Test
    void create_emptyName() {
        Tag tag = new Tag();
        assertThrows(DataConflictException.class, () -> service.create(tag));
    }

    @Test
    void getById_notFound() {
        when(repository.findWithMoodEntriesById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void update_success() {
        Tag tag = new Tag();
        when(repository.findById(1L)).thenReturn(Optional.of(tag));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        TagDto dto = new TagDto(1L, "NEW", "red", null);

        assertEquals("NEW", service.update(1L, dto).getName());
    }

    @Test
    void delete_notFound() {
        when(repository.findWithMoodEntriesById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}