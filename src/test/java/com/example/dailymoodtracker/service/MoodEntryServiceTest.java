package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.*;
import com.example.dailymoodtracker.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoodEntryServiceTest {

    @Mock private MoodEntryRepository repository;
    @Mock private MoodTypeRepository moodTypeRepo;
    @Mock private UserRepository userRepository;
    @Mock private TagRepository tagRepository;

    @InjectMocks
    private MoodEntryService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private MoodEntryDto validDto() {
        return new MoodEntryDto(
            null, "happy", LocalDate.now(), 1L, List.of(1L)
        );
    }

    @Test
    void save_success() {
        MoodEntry entry = new MoodEntry();
        entry.setEntryDate(LocalDate.now());

        User user = new User();
        user.setId(1L);
        entry.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        MoodEntry result = service.save(entry, validDto());

        assertNotNull(result);
    }

    @Test
    void save_noDate_shouldThrow() {
        MoodEntry entry = new MoodEntry();

        assertThrows(DataConflictException.class,
            () -> service.save(entry, validDto()));
    }

    @Test
    void save_userNotFound() {
        MoodEntry entry = new MoodEntry();
        entry.setEntryDate(LocalDate.now());

        User user = new User();
        user.setId(1L);
        entry.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> service.save(entry, validDto()));
    }

    @Test
    void saveAll_emptyList() {
        assertThrows(DataConflictException.class,
            () -> service.saveAll(List.of()));
    }

    @Test
    void saveAll_success() {
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        List<MoodEntry> result = service.saveAll(List.of(validDto()));

        assertEquals(1, result.size());
    }

    @Test
    void saveAll_errorInside_shouldStop() {
        List<MoodEntryDto> list = List.of(
            validDto(),
            new MoodEntryDto(null, "ERROR", LocalDate.now(), 1L, null)
        );

        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        assertThrows(DataConflictException.class,
            () -> service.saveAll(list));
    }

    @Test
    void saveAllTransactional_shouldRollback() {
        List<MoodEntryDto> list = List.of(
            validDto(),
            new MoodEntryDto(null, "ERROR", LocalDate.now(), 1L, null)
        );

        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        assertThrows(DataConflictException.class,
            () -> service.saveAllTransactional(list));
    }

    @Test
    void findComplex_cacheHit() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<MoodEntry> page = new PageImpl<>(List.of());
        when(repository.findComplex(1L, "happy", pageable)).thenReturn(page);

        service.findComplex(1L, "happy", pageable);
        Page<MoodEntry> cached = service.findComplex(1L, "happy", pageable);

        assertEquals(page, cached);
        verify(repository, times(1)).findComplex(any(), any(), any());
    }

    @Test
    void delete_success() {
        MoodEntry entry = new MoodEntry();
        when(repository.findById(1L)).thenReturn(Optional.of(entry));

        service.delete(1L);

        verify(repository).delete(entry);
    }

    @Test
    void delete_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> service.delete(1L));
    }
}