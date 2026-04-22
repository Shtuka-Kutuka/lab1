package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.MoodTypeDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.MoodType;
import com.example.dailymoodtracker.repository.MoodEntryRepository;
import com.example.dailymoodtracker.repository.MoodTypeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoodTypeServiceTest {

    @Mock private MoodTypeRepository repository;
    @Mock private MoodEntryRepository moodEntryRepository;
    @Mock private MoodEntryService moodEntryService;

    @InjectMocks
    private MoodTypeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_success() {
        MoodType mt = new MoodType();
        mt.setName("happy");

        when(repository.save(mt)).thenReturn(mt);

        MoodType result = service.create(mt);

        assertNotNull(result);
        verify(moodEntryService).invalidateCache();
    }

    @Test
    void create_emptyName_shouldThrow() {
        MoodType mt = new MoodType();

        assertThrows(DataConflictException.class,
            () -> service.create(mt));
    }

    @Test
    void getById_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> service.getById(1L));
    }
}