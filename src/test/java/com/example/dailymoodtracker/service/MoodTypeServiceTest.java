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

import java.util.ArrayList;
import java.util.List;
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
    void create_success() {
        MoodType m = new MoodType();
        m.setName("OK");

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertNotNull(service.create(m));
    }

    @Test
    void update_blankName() {
        MoodType m = new MoodType();
        when(repository.findById(1L)).thenReturn(Optional.of(m));

        var dto = new MoodTypeDto(1L, "", null, null);

        assertThrows(DataConflictException.class,
            () -> service.update(1L, dto));
    }
    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(List.of());
        assertNotNull(service.getAll());
    }

    @Test
    void getById_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }
    @Test
    void update_withEmojiAndDescription() {
        MoodType m = new MoodType();
        when(repository.findById(1L)).thenReturn(Optional.of(m));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        var dto = new MoodTypeDto(1L, "NEW", "🙂", "desc");

        MoodType result = service.update(1L, dto);

        assertEquals("🙂", result.getEmoji());
        assertEquals("desc", result.getDescription());
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
    @Test
    void delete_fullCoverage() {
        MoodType mood = new MoodType();
        mood.setId(1L);

        com.example.dailymoodtracker.model.MoodEntry entry =
            new com.example.dailymoodtracker.model.MoodEntry();

        MoodType entryMood = new MoodType();
        entryMood.setId(1L);

        entry.setMoodType(entryMood);

        when(repository.findById(1L)).thenReturn(Optional.of(mood));

        List<com.example.dailymoodtracker.model.MoodEntry> entries = new ArrayList<>();
        entries.add(entry);

        when(moodEntryRepository.findAll()).thenReturn(entries);

        service.delete(1L);

        assertNull(entry.getMoodType());

        verify(repository).delete(mood);
        verify(moodEntryService).invalidateCache();
    }
}