package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.User;
import com.example.dailymoodtracker.repository.MoodEntryRepository;
import com.example.dailymoodtracker.repository.MoodTypeRepository;
import com.example.dailymoodtracker.repository.TagRepository;
import com.example.dailymoodtracker.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoodEntryServiceTest {

    @Mock private MoodEntryRepository repository;
    @Mock private MoodTypeRepository moodTypeRepository;
    @Mock private UserRepository userRepository;
    @Mock private TagRepository tagRepository;

    @InjectMocks
    private MoodEntryService service;

    private User mockUser() {
        User u = new User();
        u.setId(1L);
        return u;
    }

    @Test
    void saveAll_emptyList() {
        List<MoodEntryDto> list = List.of();
        assertThrows(DataConflictException.class, () -> service.saveAll(list));
    }

    @Test
    void saveAll_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser()));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        MoodEntryDto dto = new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());
        List<MoodEntryDto> list = List.of(dto);

        assertEquals(1, service.saveAll(list).size());
    }

    @Test
    void saveAll_errorBreaksFlow() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser()));

        MoodEntryDto ok = new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());
        MoodEntryDto fail = new MoodEntryDto(null, "ERROR", LocalDate.now(), 1L, List.of());

        List<MoodEntryDto> list = List.of(ok, fail);

        assertThrows(DataConflictException.class,
            () -> service.saveAll(list));
    }

    @Test
    void saveAllValidated_differentUsers() {
        MoodEntryDto d1 = new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());
        MoodEntryDto d2 = new MoodEntryDto(null, "HAPPY", LocalDate.now(), 2L, List.of());

        List<MoodEntryDto> list = List.of(d1, d2);

        assertThrows(DataConflictException.class,
            () -> service.saveAllValidated(list));
    }

    @Test
    void saveAllValidated_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser()));
        when(repository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        MoodEntryDto dto = new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());
        List<MoodEntryDto> list = List.of(dto);

        assertEquals(1, service.saveAllValidated(list).size());
    }

    @Test
    void save_userMissing() {
        MoodEntryDto dto = new MoodEntryDto(null, "HAPPY", LocalDate.now(), null, List.of());

        com.example.dailymoodtracker.model.MoodEntry entry =
            new com.example.dailymoodtracker.model.MoodEntry();

        assertThrows(ResourceNotFoundException.class,
            () -> service.save(entry, dto));
    }

    @Test
    void delete_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Long id = 1L;

        assertThrows(ResourceNotFoundException.class,
            () -> service.delete(id));
    }
}