package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.model.MoodType;
import com.example.dailymoodtracker.model.Tag;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

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

    // ---------------- SAVE ALL ----------------

    @Test
    void saveAll_emptyList() {
        assertThrows(DataConflictException.class,
            () -> service.saveAll(List.of()));
    }

    @Test
    void saveAll_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser()));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        MoodEntryDto dto =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());

        List<MoodEntry> result = service.saveAll(List.of(dto));

        assertEquals(1, result.size());
    }

    @Test
    void saveAll_errorBreaksFlow() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser()));

        MoodEntryDto ok =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());
        MoodEntryDto fail =
            new MoodEntryDto(null, "ERROR", LocalDate.now(), 1L, List.of());

        assertThrows(DataConflictException.class,
            () -> service.saveAll(List.of(ok, fail)));
    }

    @Test
    void saveAllTransactional_nullList() {
        assertThrows(DataConflictException.class,
            () -> service.saveAllTransactional(null));
    }

    @Test
    void saveAllTransactional_emptyList() {
        assertThrows(DataConflictException.class,
            () -> service.saveAllTransactional(List.of()));
    }

    @Test
    void saveAllTransactional_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser()));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        MoodEntryDto dto1 =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());
        MoodEntryDto dto2 =
            new MoodEntryDto(null, "CALM", LocalDate.now(), 1L, List.of());

        List<MoodEntry> result = service.saveAllTransactional(List.of(dto1, dto2));

        assertEquals(2, result.size());
        verify(repository, times(2)).save(any(MoodEntry.class));
    }

    // ---------------- SAVE ALL VALIDATED ----------------

    @Test
    void saveAllValidated_nullOrEmpty() {
        assertThrows(DataConflictException.class,
            () -> service.saveAllValidated(null));

        assertThrows(DataConflictException.class,
            () -> service.saveAllValidated(List.of()));
    }

    @Test
    void saveAllValidated_differentUsers() {
        MoodEntryDto d1 =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());
        MoodEntryDto d2 =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), 2L, List.of());

        assertThrows(DataConflictException.class,
            () -> service.saveAllValidated(List.of(d1, d2)));
    }

    @Test
    void saveAllValidated_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser()));
        when(repository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        MoodEntryDto dto =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());

        List<MoodEntry> result = service.saveAllValidated(List.of(dto));

        assertEquals(1, result.size());
    }

    // ---------------- SAVE ----------------

    @Test
    void save_userMissing() {
        MoodEntry entry = new MoodEntry();

        MoodEntryDto dto =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), null, List.of());

        assertThrows(ResourceNotFoundException.class,
            () -> service.save(entry, dto));
    }

    @Test
    void save_entryDateNull() {
        User user = mockUser();

        MoodEntry entry = new MoodEntry();
        entry.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        MoodEntryDto dto =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());

        assertThrows(DataConflictException.class,
            () -> service.save(entry, dto));
    }

    @Test
    void save_fullFlow() {
        User user = mockUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        MoodType mood = new MoodType();
        mood.setId(1L);

        when(moodTypeRepository.findByName("HAPPY"))
            .thenReturn(Optional.of(mood));

        Tag tag = new Tag();
        tag.setId(10L);

        when(tagRepository.findById(10L)).thenReturn(Optional.of(tag));

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        MoodEntry entry = new MoodEntry();
        entry.setUser(user);
        entry.setEntryDate(LocalDate.now());

        MoodEntryDto dto =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of(10L));

        MoodEntry result = service.save(entry, dto);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(mood, result.getMoodType());
        assertTrue(result.getTags().contains(tag));
    }

    // ---------------- FIND ----------------

    @Test
    void findAll() {
        when(repository.findAll()).thenAnswer(i -> List.of());

        assertNotNull(service.findAll());
    }

    @Test
    void findByUserId() {
        when(repository.findByUserId(1L)).thenAnswer(i -> List.of());

        assertNotNull(service.findByUserId(1L));
    }

    // ---------------- COMPLEX ----------------

    @Test
    void findComplex_cacheMiss() {
        when(repository.findComplex(any(), any(), any()))
            .thenReturn(mock(Page.class));

        assertNotNull(service.findComplex(1L, "HAPPY",
            PageRequest.of(0, 10)));
    }

    @Test
    void findComplexNative_empty() {
        Page<MoodEntry> page = mock(Page.class);

        when(page.getContent()).thenReturn(List.of());
        when(repository.findComplexNative(any(), any(), any()))
            .thenReturn(page);

        assertNotNull(service.findComplexNative(1L, "HAPPY",
            PageRequest.of(0, 10)));
    }

    @Test
    void findComplexNative_fullFlow() {
        MoodEntry entry = new MoodEntry();
        entry.setId(1L);
        entry.setUserId(1L);
        entry.setMoodTypeId(1L);

        Page<MoodEntry> page = mock(Page.class);
        when(page.getContent()).thenReturn(List.of(entry));

        when(repository.findComplexNative(any(), any(), any()))
            .thenReturn(page);

        User user = mockUser();
        when(userRepository.findAllById(any()))
            .thenAnswer(i -> List.of(user));

        MoodType mood = new MoodType();
        mood.setId(1L);

        when(moodTypeRepository.findAllById(any()))
            .thenAnswer(i -> List.of(mood));

        Tag tag = new Tag();
        tag.setId(10L);

        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{1L, tag});

        when(tagRepository.findTagsByMoodEntryIds(any()))
            .thenReturn(rows);

        Page<MoodEntry> result = service.findComplexNative(
            1L, "HAPPY", PageRequest.of(0, 10));

        assertNotNull(result);
    }

    @Test
    void loadTags_emptyIds_returnsEmptyMap() throws Exception {
        Method method = MoodEntryService.class.getDeclaredMethod("loadTags", List.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<Long, Set<Tag>> result = (Map<Long, Set<Tag>>) method.invoke(service, List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---------------- UPDATE ----------------

    @Test
    void update_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        MoodEntryDto dto =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());

        assertThrows(ResourceNotFoundException.class,
            () -> service.update(1L, dto));
    }

    @Test
    void update_success() {
        MoodEntry entry = new MoodEntry();

        when(repository.findById(1L)).thenReturn(Optional.of(entry));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        MoodEntryDto dto =
            new MoodEntryDto(null, "HAPPY", LocalDate.now(), 1L, List.of());

        assertNotNull(service.update(1L, dto));
    }

    // ---------------- DELETE ----------------

    @Test
    void delete_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> service.delete(1L));
    }

    @Test
    void delete_success() {
        MoodEntry entry = new MoodEntry();

        when(repository.findById(1L)).thenReturn(Optional.of(entry));

        service.delete(1L);

        verify(repository).delete(entry);
    }

    // ---------------- CACHE ----------------

    @Test
    void invalidateCache() {
        assertDoesNotThrow(() -> service.invalidateCache());
    }
}