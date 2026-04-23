package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.MoodTypeDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.MoodType;
import com.example.dailymoodtracker.repository.MoodEntryRepository;
import com.example.dailymoodtracker.repository.MoodTypeRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoodTypeServiceTest {

    @Mock private MoodTypeRepository repository;
    @Mock private MoodEntryRepository moodEntryRepository;
    @Mock private MoodEntryService moodEntryService;

    @InjectMocks
    private MoodTypeService service;

    @Test
    void create_emptyName() {
        MoodType m = new MoodType();
        assertThrows(DataConflictException.class, () -> service.create(m));
    }

    @Test
    void getById_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void update_success() {
        MoodType m = new MoodType();
        when(repository.findById(1L)).thenReturn(Optional.of(m));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        var dto = new MoodTypeDto(1L, "NEW", null, null);

        assertEquals("NEW", service.update(1L, dto).getName());
    }

    @Test
    void delete_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}