package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.TagDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.Tag;
import com.example.dailymoodtracker.repository.TagRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {

    @Mock private TagRepository repository;
    @Mock private MoodEntryService moodEntryService;

    @InjectMocks
    private TagService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_success() {
        Tag tag = new Tag();
        tag.setName("tag");

        when(repository.save(tag)).thenReturn(tag);

        Tag result = service.create(tag);

        assertNotNull(result);
        verify(moodEntryService).invalidateCache();
    }

    @Test
    void create_emptyName_shouldThrow() {
        Tag tag = new Tag();

        assertThrows(DataConflictException.class,
            () -> service.create(tag));
    }

    @Test
    void getById_notFound() {
        when(repository.findWithMoodEntriesById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> service.getById(1L));
    }
}